package io.github.gdx945.rpc;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gdx945.rpc.method.RpcMethod;
import io.github.gdx945.rpc.param.RpcReq;
import io.github.gdx945.rpc.param.RpcResp;
import io.github.gdx945.util.CommonFuture;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
public class RpcService {

    private final static Logger logger = LoggerFactory.getLogger(RpcService.class);

    private final Map<RpcMethod, Function<Serializable, Object>> SERVICE_HANDLER = new ConcurrentHashMap<>();

    private final Map<RpcMethod, ExecutorService> DEAL_EXECUTOR_SERVICE_MAP = new ConcurrentHashMap<>();

    private final Map<RpcMethod, ExecutorService> RETURN_EXECUTOR_SERVICE_MAP = new ConcurrentHashMap<>();

    private ExecutorService executorService = Executors.newFixedThreadPool(8, new DefaultThreadFactory("RpcService"));

    public RpcService putServiceHandler(RpcMethod method, Function<Serializable, Object> serviceHandler) {
        // todo check duplicate method
        SERVICE_HANDLER.put(method, serviceHandler);
        return this;
    }

    public RpcService putServiceHandler(RpcMethod method, Function<Serializable, Object> serviceHandler, ExecutorService dealExecutorService,
        ExecutorService returnExecutorService) {
        putServiceHandler(method, serviceHandler);
        if (dealExecutorService != null) {
            DEAL_EXECUTOR_SERVICE_MAP.put(method, dealExecutorService);
        }
        if (returnExecutorService != null) {
            RETURN_EXECUTOR_SERVICE_MAP.put(method, returnExecutorService);
        }
        return this;
    }

    public RpcService(int port) {
        start(port);
    }

    private void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ObjectDecoder(4096, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
                    .addLast(new ObjectEncoder()).addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                        RpcReq rpcReq = (RpcReq) msg;
                        chooseExecutorService(DEAL_EXECUTOR_SERVICE_MAP, rpcReq.getMethod()).submit(() -> {
                            Object result = SERVICE_HANDLER.get(rpcReq.getMethod()).apply(rpcReq.getParam());
                            RpcResp rpcResp = new RpcResp(rpcReq.getRpcId());
                            rpcResp.setMethod(rpcReq.getMethod());
                            if (result instanceof CommonFuture) {
                                chooseExecutorService(RETURN_EXECUTOR_SERVICE_MAP, rpcReq.getMethod()).submit(() -> {
                                    try {
                                        rpcResp.setResult(((CommonFuture) result).get(1024, TimeUnit.MILLISECONDS));
                                        ctx.writeAndFlush(rpcResp);
                                    }
                                    catch (InterruptedException | ExecutionException | TimeoutException e) {
                                        logger.error("return future failed.", e);
                                    }
                                });
                            }
                            else {
                                rpcResp.setResult((Serializable) result);
                                ctx.writeAndFlush(rpcResp);
                            }
                        });
                    }
                });
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

        // 绑定端口，开始接收进来的连接
        b.bind(port);
    }

    private ExecutorService chooseExecutorService(Map<RpcMethod, ExecutorService> executorServiceMap, RpcMethod rpcMethod) {
        return Optional.ofNullable(executorServiceMap.get(rpcMethod)).orElse(this.executorService);
    }
}
