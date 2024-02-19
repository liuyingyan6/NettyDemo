package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

public class NettyClient {
    public static void main(String[] args) throws Exception{
        //根据ip地址和端口号获得连接通道
        ChannelFuture cf = getChannelFuture("127.0.0.1",10000);
        //向服务端发送一个消息，必须使用：Unpooled.copiedBuffer()
        String msg = "hello from client";
        cf.channel().writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        //对关闭服务器通道进行监听
        cf.channel().closeFuture().sync();
        System.out.println("断开连接,主线程结束..");
    }
    /**
     * 封装获得连接通道的代码
     * @param host
     * @param port
     * @return
     * @throws InterruptedException
     */
    private static ChannelFuture getChannelFuture(String host,Integer port) throws InterruptedException {
        //定义事件组
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();//定义引导启动类
        b.group(group)//设置事件组
                .channel(NioSocketChannel.class)//设置通道方式采用NIO通讯模式
                .handler(new LoggingHandler(LogLevel.INFO))//设置日志级别
                .handler(new ChannelInitializer<SocketChannel>() {//设置事件组需要处理的事件
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        System.out.println("initialize...");
                        sc.pipeline().addLast(new DataClientHandler());
                    }
                });
        //通过IP地址，端口与服务器建立连接
        ChannelFuture cf = b.connect(host, port).sync();
        return cf;
    }
}
