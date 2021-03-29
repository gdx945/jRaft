package io.github.gdx945.rpc.serialization;

import io.github.gdx945.protobuf.Map;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-04 21:04:32
 * @since : 0.1
 */
public class ProtobufDecoder extends LengthFieldBasedFrameDecoder {

    public ProtobufDecoder(int maxObjectSize) {
        super(maxObjectSize, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        return Map.map.parseFrom(in.array());
    }
}
