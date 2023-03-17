package mao.t1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Project name(项目名称)：Netty_hello_world
 * Package(包名): mao.t1
 * Class(类名): Server
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/17
 * Time(创建时间)： 13:55
 * Version(版本): 1.0
 * Description(描述)： 服务端
 */

@Slf4j
public class Server
{
    public static void main(String[] args)
    {
        new ServerBootstrap()
                //创建 NioEventLoopGroup，可以简单理解为 `线程池 + Selector`
                .group(new NioEventLoopGroup())
                //选择服务 Socket 实现类，其中 NioServerSocketChannel 表示基于 NIO 的服务器端实现
                .channel(NioServerSocketChannel.class)
                //为啥方法叫 childHandler，是接下来添加的处理器都是给 SocketChannel 用的，而不是给 ServerSocketChannel。
                // ChannelInitializer 处理器（仅执行一次），
                // 它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
                .childHandler(new ChannelInitializer<NioSocketChannel>()
                {
                    /**
                     * 初始化通道
                     *
                     * @param nioSocketChannel nio套接字通道
                     * @throws Exception 异常
                     */
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception
                    {
                        //SocketChannel 的处理器，解码 ByteBuf => String
                        nioSocketChannel.pipeline().addLast(new StringDecoder())
                                //SocketChannel 的业务处理器，使用上一个处理器的处理结果
                                .addLast(new SimpleChannelInboundHandler<String>()
                                {
                                    /**
                                     * 通道读
                                     *
                                     * @param channelHandlerContext 通道处理程序上下文
                                     * @param s                     字符串
                                     * @throws Exception 异常
                                     */
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s)
                                            throws Exception
                                    {
                                        log.debug(channelHandlerContext.toString());
                                        log.debug(s);
                                    }
                                });
                    }
                    //ServerSocketChannel 绑定的监听端口
                }).bind(8080);
    }
}
