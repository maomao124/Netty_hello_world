package mao.t1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Project name(项目名称)：Netty_hello_world
 * Package(包名): mao.t1
 * Class(类名): Client
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/17
 * Time(创建时间)： 14:18
 * Version(版本): 1.0
 * Description(描述)： 客户端
 */

@Slf4j
public class Client
{
    @SneakyThrows
    public static void main(String[] args)
    {
        new Bootstrap()
                //创建 NioEventLoopGroup，同 Server
                .group(new NioEventLoopGroup())
                //选择客户 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现
                .channel(NioSocketChannel.class)
                //添加 SocketChannel 的处理器，ChannelInitializer 处理器（仅执行一次），
                // 它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
                .handler(new ChannelInitializer<Channel>()
                {
                    /**
                     * 初始化通道
                     *
                     * @param channel 通道
                     * @throws Exception 异常
                     */
                    @Override
                    protected void initChannel(Channel channel) throws Exception
                    {
                        //消息会经过通道 handler 处理，这里是将 String => ByteBuf 发出
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                //指定要连接的服务器和端口
                .connect(new InetSocketAddress(8080))
                //Netty 中很多方法都是异步的，如 connect，这时需要使用 sync 方法等待 connect 建立连接完毕
                .sync()
                //获取 channel 对象，它即为通道抽象，可以进行数据读写操作
                .channel()
                //写入消息并清空缓冲区
                .writeAndFlush("hello world.");
    }
}
