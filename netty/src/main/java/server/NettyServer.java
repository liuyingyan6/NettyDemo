package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {
    public static void main(String[] args) {
        // 定义2大事件组：父事件循环组，子事件循环组
        // 父事件循环组：负责监听并接受传入的连接请求，将已接受的连接分配给子事件循环组的一个事件循环处理
        NioEventLoopGroup pGroup = new NioEventLoopGroup();
        // 子事件循环组：有自己的一组事件循环县城，负责处理已接受的客户端连接，例如读取数据、写入数据、处理业务逻辑
        NioEventLoopGroup cGroup = new NioEventLoopGroup();
        // 定义服务引导类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture cf = null;
        try{
            serverBootstrap.group(pGroup,cGroup)
                    .channel(NioServerSocketChannel.class) // 设置通道技术采用nio
                    .option(ChannelOption.SO_BACKLOG,1024) // 设置最大连接数为1024
                    .handler(new LoggingHandler(LogLevel.INFO)) // 设置日志级别为info
                    .childOption(ChannelOption.SO_KEEPALIVE,true) // 保持激活状态
                    .childOption(ChannelOption.TCP_NODELAY, true) // 优化网络流量
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 定义子循环组，需要执行的任务
                        // 配置入站、出站事件channel
                        protected void initChannel(SocketChannel sc) {
                            // 得到客户端的ip地址
                            System.out.println(sc.remoteAddress().getHostString() + " has a task...");
                            sc.pipeline().addLast(new DataServerHandler()); // 将任务处理类交给子事务组进行处理
                        }
                    });
            // 监听服务器上的10000端口，等待客户端从该端口传输数据
            cf = serverBootstrap.bind(10000).sync().addListener(future -> {
                System.out.println(future.isSuccess()? "listening succeed":"listening failed");
            });
            cf.channel().closeFuture().sync(); // 对关闭服务器通道进行监听
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            pGroup.shutdownGracefully(); // 关闭父事件循环组
            cGroup.shutdownGracefully(); // 关闭子事件循环组
        }
    }
}
