package io.github.gdx945.rpc;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gdx945.rpc.method.RpcMethod;
import io.github.gdx945.rpc.param.RpcReq;
import io.github.gdx945.rpc.param.RpcResp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-22 19:39:40
 * @since : 0.1
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private static final AtomicInteger RPC_ID_SEQ = new AtomicInteger(Integer.MAX_VALUE);

    private static final Map<Integer, InvokeFuture<Serializable>> RPC_CALLBACK = new ConcurrentHashMap<>();

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(),
        new DefaultThreadFactory("rpcClient"));

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.address = host.concat(":").concat(String.valueOf(port));
        start();
    }

    private String address;

    private String host;

    private int port;

    private ChannelFuture channelFuture;

    private Bootstrap bootstrap;

    private final Lock reconnectLock = new ReentrantLock();

    private long lastConnectTimeMillis = 0;

    private long minReconnectDelay = 1000;

    private void start() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(workerGroup);
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) minReconnectDelay); // 建立连接的超时时间
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // tcp是否发心跳包，这个是系统级别
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ObjectDecoder(4096, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
                    .addLast(new ObjectEncoder())
                    //                    .addLast(new io.netty.handler.codec.protobuf.ProtobufDecoder(io.github.gdx945.protobuf.Map.map.getDefaultInstance()))
                    //                    .addLast(new io.netty.handler.codec.protobuf.ProtobufEncoder())
                    .addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) {
                            threadPoolExecutor.execute(() -> {
                                RpcResp rpcResp = (RpcResp) msg;
                                InvokeFuture<Serializable> invokeFuture = RPC_CALLBACK.remove(rpcResp.getRpcId());
                                if (invokeFuture != null) {
                                    invokeFuture.trySuccess(rpcResp.getResult());
                                }
                                logger.debug("rpc invoke end: service- {}:{}, method- {}, rpcId- {}", host, port, rpcResp.getMethod(),
                                    rpcResp.getRpcId());
                            });
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            super.channelInactive(ctx);
                            logger.debug("rpcClient channel inactive: {}:{}, reconnect~", host, port);
                            reconnect(minReconnectDelay);
                        }
                    });
            }
        });

        doConnect();
    }

    private void doConnect() {
        // 启动客户端
        logger.debug("rpc connect start: service- {}:{}", this.host, this.port);
        this.lastConnectTimeMillis = System.currentTimeMillis();
        this.channelFuture = this.bootstrap.connect(this.host, this.port);
        this.channelFuture.addListener(future -> {
            if (!channelFuture.isSuccess()) { // 连接失败
                logger.debug("rpc connect failed: service- {}:{}, reconnect~", this.host, this.port);
                reconnect(timeoutLeft(this.minReconnectDelay, this.lastConnectTimeMillis));
            }
            else {
                logger.debug("rpc connect success: service- {}:{}", this.host, this.port);
            }
        });
    }

    private void reconnect(long timeout) {
        long startTimeMillis = System.currentTimeMillis();

        if (isNeedReconnect()) {
            if (timeoutLeft(timeout, startTimeMillis) < timeoutLeft(this.minReconnectDelay, this.lastConnectTimeMillis)) {
                try {
                    TimeUnit.MILLISECONDS.sleep(timeoutLeft(timeout, startTimeMillis));
                }
                catch (InterruptedException e) {
                    // ignore
                }
                return;
            }

            try {
                if (this.reconnectLock.tryLock(timeoutLeft(timeout, startTimeMillis), TimeUnit.MILLISECONDS)) {
                    if (isNeedReconnect()) {
                        TimeUnit.MILLISECONDS.sleep(timeoutLeft(this.minReconnectDelay, this.lastConnectTimeMillis));
                        doConnect();
                    }
                }
            }
            catch (InterruptedException e) {
                logger.error("reconnect exception~", e);
            }
            finally {
                try {
                    this.reconnectLock.unlock();
                }
                catch (Exception e) {
                    // ignore
                }
            }
        }

    }

    private boolean isNeedReconnect() {
        return this.channelFuture.isDone() && (!this.channelFuture.isSuccess() || !this.channelFuture.channel().isWritable());
    }

    public Serializable invoke(RpcMethod rpcMethod, Serializable param, long timeout) throws TimeoutException {
        long startTimeMillis = System.currentTimeMillis();

        int rpcId = RPC_ID_SEQ.incrementAndGet();
        logger.debug("rpc invoke start: service- {}:{}, method- {}, rpcId- {}", this.host, this.port, rpcMethod, rpcId);

        checkTimeout(timeout, startTimeMillis);
        this.channelFuture.awaitUninterruptibly(timeoutLeft(timeout, startTimeMillis), TimeUnit.MILLISECONDS);

        if (this.channelFuture.isSuccess()) {
            RpcReq rpcReq = new RpcReq(rpcId, rpcMethod, param);
            InvokeFuture<Serializable> invokeFuture = new InvokeFuture<>();
            RPC_CALLBACK.put(rpcId, invokeFuture);
            checkTimeout(timeout, startTimeMillis);
            this.channelFuture.channel().writeAndFlush(rpcReq);
            try {
                return invokeFuture.get(timeoutLeft(timeout, startTimeMillis), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                RPC_CALLBACK.remove(rpcId);
                throw new TimeoutException(
                    "rpc invoke to ".concat(this.address).concat(" timeout? ").concat(String.valueOf(timeoutLeft(timeout, startTimeMillis))));
            }
        }
        else {
            reconnect(timeoutLeft(timeout, startTimeMillis));
            checkTimeout(timeout, startTimeMillis);
            return invoke(rpcMethod, param, timeoutLeft(timeout, startTimeMillis));
        }
    }

    public InvokeFuture<? extends Serializable> invokeAsyn(RpcMethod rpcMethod, Serializable param, long timeout) throws TimeoutException {
        long startTimeMillis = System.currentTimeMillis();

        int rpcId = RPC_ID_SEQ.incrementAndGet();
        logger.debug("rpc invoke start: service- {}:{}, method- {}, rpcId- {}", this.host, this.port, rpcMethod, rpcId);

        checkTimeout(timeout, startTimeMillis);
        this.channelFuture.awaitUninterruptibly(timeoutLeft(timeout, startTimeMillis), TimeUnit.MILLISECONDS);

        if (this.channelFuture.isSuccess()) {
            RpcReq rpcReq = new RpcReq(rpcId, rpcMethod, param);
            InvokeFuture<Serializable> invokeFuture = new InvokeFuture<>();
            RPC_CALLBACK.put(rpcId, invokeFuture);
            checkTimeout(timeout, startTimeMillis);
            this.channelFuture.channel().writeAndFlush(rpcReq);
            return invokeFuture;
        }
        else {
            reconnect(timeoutLeft(timeout, startTimeMillis));
            checkTimeout(timeout, startTimeMillis);
            return invokeAsyn(rpcMethod, param, timeoutLeft(timeout, startTimeMillis));
        }
    }

    // 根据已用的时间更新timeout
    private long timeoutLeft(long timeout, long startTimeMillis) {
        return timeout - (System.currentTimeMillis() - startTimeMillis);
    }

    private void checkTimeout(long timeout, long startTimeMillis) throws TimeoutException {
        if (timeoutLeft(timeout, startTimeMillis) <= 0) {
            throw new TimeoutException("rpc invoke timeout.");
        }
    }

    public String getAddr() {
        return this.address;
    }
}
