package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class DataClientHandler extends ChannelInboundHandlerAdapter {
    //从通道中读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String content = buf.toString(CharsetUtil.UTF_8);
        System.out.println("DataClientHandler channelRead() =>" + content);
        super.channelRead(ctx, msg);//传输给下一个处理器
    }
    //通道读取完毕之后
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("finished reading", CharsetUtil.UTF_8));
    }
    //抛出异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();//关闭连接通道
    }
}
