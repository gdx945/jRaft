package io.github.gdx945.rpc.serialization;

import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-04 21:00:33
 * @since : 0.1
 */
public class ProtobufEncoder extends MessageToByteEncoder<Message> {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        int startIdx = out.writerIndex();

        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        try {
            bout.write(LENGTH_PLACEHOLDER);
            msg.writeTo(bout);
            //            bout.flush();
        }
        finally {
            bout.close();
        }

        int endIdx = out.writerIndex();

        out.setInt(startIdx, endIdx - startIdx - 4);
    }
}
