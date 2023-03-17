

# Netty概述

## Netty 是什么？

Netty 是一个异步的、基于事件驱动的网络应用框架，用于快速开发可维护、高性能的网络服务器和客户端



## Netty 的地位

Netty 在 Java 网络应用框架中的地位就好比：Spring 框架在 JavaEE 开发中的地位

以下的框架都使用了 Netty，因为它们有网络通信需求！

* Cassandra - nosql 数据库
* Spark - 大数据分布式计算框架
* Hadoop - 大数据分布式存储框架
* RocketMQ - ali 开源的消息队列
* ElasticSearch - 搜索引擎
* gRPC - rpc 框架
* Dubbo - rpc 框架
* Spring 5.x - flux api 完全抛弃了 tomcat ，使用 netty 作为服务器端
* Zookeeper - 分布式协调框架







## Netty 的优势

* 解决 TCP 传输问题，如粘包、半包
* epoll 空轮询导致 CPU 100%
* 对 API 进行增强，使之更易用，如 FastThreadLocal => ThreadLocal，ByteBuf => ByteBuffer
* Netty 的开发迭代更迅速，API 更简洁、文档更优秀
* 久经考验









# Netty入门

## 目标

开发一个简单的服务器端和客户端

* 客户端向服务器端发送 hello, world
* 服务器仅接收，不返回





## maven依赖

```xml
<!--netty-->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.39.Final</version>
</dependency>
```





## 服务器端

```java
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
```





## 客户端

```java
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
```





服务端运行结果：

```sh
2023-03-17  14:01:23.644  [main] DEBUG io.netty.util.internal.logging.InternalLoggerFactory:  Using SLF4J as the default logging framework
2023-03-17  14:01:23.650  [main] DEBUG io.netty.channel.MultithreadEventLoopGroup:  -Dio.netty.eventLoopThreads: 64
2023-03-17  14:01:23.661  [main] DEBUG io.netty.util.internal.InternalThreadLocalMap:  -Dio.netty.threadLocalMap.stringBuilder.initialSize: 1024
2023-03-17  14:01:23.661  [main] DEBUG io.netty.util.internal.InternalThreadLocalMap:  -Dio.netty.threadLocalMap.stringBuilder.maxSize: 4096
2023-03-17  14:01:23.666  [main] DEBUG io.netty.channel.nio.NioEventLoop:  -Dio.netty.noKeySetOptimization: false
2023-03-17  14:01:23.666  [main] DEBUG io.netty.channel.nio.NioEventLoop:  -Dio.netty.selectorAutoRebuildThreshold: 512
2023-03-17  14:01:23.672  [main] DEBUG io.netty.util.internal.PlatformDependent:  Platform: Windows
2023-03-17  14:01:23.674  [main] DEBUG io.netty.util.internal.PlatformDependent0:  -Dio.netty.noUnsafe: false
2023-03-17  14:01:23.674  [main] DEBUG io.netty.util.internal.PlatformDependent0:  Java version: 16
2023-03-17  14:01:23.675  [main] DEBUG io.netty.util.internal.PlatformDependent0:  sun.misc.Unsafe.theUnsafe: available
2023-03-17  14:01:23.675  [main] DEBUG io.netty.util.internal.PlatformDependent0:  sun.misc.Unsafe.copyMemory: available
2023-03-17  14:01:23.676  [main] DEBUG io.netty.util.internal.PlatformDependent0:  java.nio.Buffer.address: available
2023-03-17  14:01:23.676  [main] DEBUG io.netty.util.internal.PlatformDependent0:  direct buffer constructor: unavailable
java.lang.UnsupportedOperationException: Reflective setAccessible(true) disabled

2023-03-17  14:01:23.684  [main] DEBUG io.netty.util.internal.PlatformDependent0:  java.nio.Bits.unaligned: available, true
2023-03-17  14:01:23.684  [main] DEBUG io.netty.util.internal.PlatformDependent0:  jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable
java.lang.IllegalAccessException: class io.netty.util.internal.PlatformDependent0$6 cannot access class jdk.internal.misc.Unsafe (in module java.base) because module java.base does not export jdk.internal.misc to unnamed module @70a9f84e

2023-03-17  14:01:23.685  [main] DEBUG io.netty.util.internal.PlatformDependent0:  java.nio.DirectByteBuffer.<init>(long, int): unavailable
2023-03-17  14:01:23.685  [main] DEBUG io.netty.util.internal.PlatformDependent:  sun.misc.Unsafe: available
2023-03-17  14:01:23.685  [main] DEBUG io.netty.util.internal.PlatformDependent:  maxDirectMemory: 8522825728 bytes (maybe)
2023-03-17  14:01:23.686  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.tmpdir: C:\Users\mao\AppData\Local\Temp (java.io.tmpdir)
2023-03-17  14:01:23.686  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.bitMode: 64 (sun.arch.data.model)
2023-03-17  14:01:23.686  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.maxDirectMemory: -1 bytes
2023-03-17  14:01:23.687  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.uninitializedArrayAllocationThreshold: -1
2023-03-17  14:01:23.688  [main] DEBUG io.netty.util.internal.CleanerJava9:  java.nio.ByteBuffer.cleaner(): available
2023-03-17  14:01:23.688  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.noPreferDirect: false
2023-03-17  14:01:23.691  [main] DEBUG io.netty.util.internal.PlatformDependent:  org.jctools-core.MpscChunkedArrayQueue: available
2023-03-17  14:01:23.806  [main] DEBUG io.netty.channel.DefaultChannelId:  -Dio.netty.processId: 9908 (auto-detected)
2023-03-17  14:01:23.808  [main] DEBUG io.netty.util.NetUtil:  -Djava.net.preferIPv4Stack: false
2023-03-17  14:01:23.808  [main] DEBUG io.netty.util.NetUtil:  -Djava.net.preferIPv6Addresses: false
2023-03-17  14:01:23.816  [main] DEBUG io.netty.util.NetUtil:  Loopback interface: lo (Software Loopback Interface 1, 127.0.0.1)
2023-03-17  14:01:23.816  [main] DEBUG io.netty.util.NetUtil:  Failed to get SOMAXCONN from sysctl and file \proc\sys\net\core\somaxconn. Default: 200
2023-03-17  14:01:23.872  [main] DEBUG io.netty.channel.DefaultChannelId:  -Dio.netty.machineId: 70:d8:23:ff:fe:4a:12:94 (auto-detected)
2023-03-17  14:01:23.878  [main] DEBUG io.netty.util.ResourceLeakDetector:  -Dio.netty.leakDetection.level: simple
2023-03-17  14:01:23.878  [main] DEBUG io.netty.util.ResourceLeakDetector:  -Dio.netty.leakDetection.targetRecords: 4
2023-03-17  14:01:23.895  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.numHeapArenas: 64
2023-03-17  14:01:23.895  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.numDirectArenas: 64
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.pageSize: 8192
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.maxOrder: 11
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.chunkSize: 16777216
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.tinyCacheSize: 512
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.smallCacheSize: 256
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.normalCacheSize: 64
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.maxCachedBufferCapacity: 32768
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.cacheTrimInterval: 8192
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.cacheTrimIntervalMillis: 0
2023-03-17  14:01:23.896  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.useCacheForAllThreads: true
2023-03-17  14:01:23.897  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.maxCachedByteBuffersPerChunk: 1023
2023-03-17  14:01:23.904  [main] DEBUG io.netty.buffer.ByteBufUtil:  -Dio.netty.allocator.type: pooled
2023-03-17  14:01:23.904  [main] DEBUG io.netty.buffer.ByteBufUtil:  -Dio.netty.threadLocalDirectBufferSize: 0
2023-03-17  14:01:23.904  [main] DEBUG io.netty.buffer.ByteBufUtil:  -Dio.netty.maxThreadLocalCharBufferSize: 16384
2023-03-17  14:25:15.366  [nioEventLoopGroup-2-2] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxCapacityPerThread: 4096
2023-03-17  14:25:15.367  [nioEventLoopGroup-2-2] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxSharedCapacityFactor: 2
2023-03-17  14:25:15.367  [nioEventLoopGroup-2-2] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.linkCapacity: 16
2023-03-17  14:25:15.367  [nioEventLoopGroup-2-2] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.ratio: 8
2023-03-17  14:25:15.370  [nioEventLoopGroup-2-2] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkAccessible: true
2023-03-17  14:25:15.370  [nioEventLoopGroup-2-2] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkBounds: true
2023-03-17  14:25:15.371  [nioEventLoopGroup-2-2] DEBUG io.netty.util.ResourceLeakDetectorFactory:  Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@2283b228
2023-03-17  14:25:15.381  [nioEventLoopGroup-2-2] DEBUG mao.t1.Server:  ChannelHandlerContext(Server$1$1#0, [id: 0xef4fe496, L:/127.0.0.1:8080 - R:/127.0.0.1:54853])
2023-03-17  14:25:15.381  [nioEventLoopGroup-2-2] DEBUG mao.t1.Server:  hello world.
```



客户端运行结果：

```sh
2023-03-17  14:25:15.088  [main] DEBUG io.netty.util.internal.logging.InternalLoggerFactory:  Using SLF4J as the default logging framework
2023-03-17  14:25:15.099  [main] DEBUG io.netty.channel.MultithreadEventLoopGroup:  -Dio.netty.eventLoopThreads: 64
2023-03-17  14:25:15.110  [main] DEBUG io.netty.util.internal.InternalThreadLocalMap:  -Dio.netty.threadLocalMap.stringBuilder.initialSize: 1024
2023-03-17  14:25:15.110  [main] DEBUG io.netty.util.internal.InternalThreadLocalMap:  -Dio.netty.threadLocalMap.stringBuilder.maxSize: 4096
2023-03-17  14:25:15.115  [main] DEBUG io.netty.channel.nio.NioEventLoop:  -Dio.netty.noKeySetOptimization: false
2023-03-17  14:25:15.115  [main] DEBUG io.netty.channel.nio.NioEventLoop:  -Dio.netty.selectorAutoRebuildThreshold: 512
2023-03-17  14:25:15.127  [main] DEBUG io.netty.util.internal.PlatformDependent:  Platform: Windows
2023-03-17  14:25:15.128  [main] DEBUG io.netty.util.internal.PlatformDependent0:  -Dio.netty.noUnsafe: false
2023-03-17  14:25:15.129  [main] DEBUG io.netty.util.internal.PlatformDependent0:  Java version: 16
2023-03-17  14:25:15.130  [main] DEBUG io.netty.util.internal.PlatformDependent0:  sun.misc.Unsafe.theUnsafe: available
2023-03-17  14:25:15.131  [main] DEBUG io.netty.util.internal.PlatformDependent0:  sun.misc.Unsafe.copyMemory: available
2023-03-17  14:25:15.131  [main] DEBUG io.netty.util.internal.PlatformDependent0:  java.nio.Buffer.address: available
2023-03-17  14:25:15.131  [main] DEBUG io.netty.util.internal.PlatformDependent0:  direct buffer constructor: unavailable
java.lang.UnsupportedOperationException: Reflective setAccessible(true) disabled
2023-03-17  14:25:15.140  [main] DEBUG io.netty.util.internal.PlatformDependent0:  java.nio.Bits.unaligned: available, true
2023-03-17  14:25:15.140  [main] DEBUG io.netty.util.internal.PlatformDependent0:  jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable
java.lang.IllegalAccessException: class io.netty.util.internal.PlatformDependent0$6 cannot access class jdk.internal.misc.Unsafe (in module java.base) because module java.base does not export jdk.internal.misc to unnamed module @70a9f84e
2023-03-17  14:25:15.141  [main] DEBUG io.netty.util.internal.PlatformDependent0:  java.nio.DirectByteBuffer.<init>(long, int): unavailable
2023-03-17  14:25:15.141  [main] DEBUG io.netty.util.internal.PlatformDependent:  sun.misc.Unsafe: available
2023-03-17  14:25:15.141  [main] DEBUG io.netty.util.internal.PlatformDependent:  maxDirectMemory: 8522825728 bytes (maybe)
2023-03-17  14:25:15.141  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.tmpdir: C:\Users\mao\AppData\Local\Temp (java.io.tmpdir)
2023-03-17  14:25:15.142  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.bitMode: 64 (sun.arch.data.model)
2023-03-17  14:25:15.142  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.maxDirectMemory: -1 bytes
2023-03-17  14:25:15.142  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.uninitializedArrayAllocationThreshold: -1
2023-03-17  14:25:15.143  [main] DEBUG io.netty.util.internal.CleanerJava9:  java.nio.ByteBuffer.cleaner(): available
2023-03-17  14:25:15.143  [main] DEBUG io.netty.util.internal.PlatformDependent:  -Dio.netty.noPreferDirect: false
2023-03-17  14:25:15.146  [main] DEBUG io.netty.util.internal.PlatformDependent:  org.jctools-core.MpscChunkedArrayQueue: available
2023-03-17  14:25:15.259  [main] DEBUG io.netty.channel.DefaultChannelId:  -Dio.netty.processId: 2604 (auto-detected)
2023-03-17  14:25:15.261  [main] DEBUG io.netty.util.NetUtil:  -Djava.net.preferIPv4Stack: false
2023-03-17  14:25:15.261  [main] DEBUG io.netty.util.NetUtil:  -Djava.net.preferIPv6Addresses: false
2023-03-17  14:25:15.269  [main] DEBUG io.netty.util.NetUtil:  Loopback interface: lo (Software Loopback Interface 1, 127.0.0.1)
2023-03-17  14:25:15.270  [main] DEBUG io.netty.util.NetUtil:  Failed to get SOMAXCONN from sysctl and file \proc\sys\net\core\somaxconn. Default: 200
2023-03-17  14:25:15.309  [main] DEBUG io.netty.channel.DefaultChannelId:  -Dio.netty.machineId: 70:d8:23:ff:fe:4a:12:94 (auto-detected)
2023-03-17  14:25:15.315  [main] DEBUG io.netty.util.ResourceLeakDetector:  -Dio.netty.leakDetection.level: simple
2023-03-17  14:25:15.316  [main] DEBUG io.netty.util.ResourceLeakDetector:  -Dio.netty.leakDetection.targetRecords: 4
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.numHeapArenas: 64
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.numDirectArenas: 64
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.pageSize: 8192
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.maxOrder: 11
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.chunkSize: 16777216
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.tinyCacheSize: 512
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.smallCacheSize: 256
2023-03-17  14:25:15.332  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.normalCacheSize: 64
2023-03-17  14:25:15.333  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.maxCachedBufferCapacity: 32768
2023-03-17  14:25:15.333  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.cacheTrimInterval: 8192
2023-03-17  14:25:15.333  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.cacheTrimIntervalMillis: 0
2023-03-17  14:25:15.333  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.useCacheForAllThreads: true
2023-03-17  14:25:15.333  [main] DEBUG io.netty.buffer.PooledByteBufAllocator:  -Dio.netty.allocator.maxCachedByteBuffersPerChunk: 1023
2023-03-17  14:25:15.338  [main] DEBUG io.netty.buffer.ByteBufUtil:  -Dio.netty.allocator.type: pooled
2023-03-17  14:25:15.338  [main] DEBUG io.netty.buffer.ByteBufUtil:  -Dio.netty.threadLocalDirectBufferSize: 0
2023-03-17  14:25:15.338  [main] DEBUG io.netty.buffer.ByteBufUtil:  -Dio.netty.maxThreadLocalCharBufferSize: 16384
2023-03-17  14:25:15.351  [main] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxCapacityPerThread: 4096
2023-03-17  14:25:15.352  [main] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxSharedCapacityFactor: 2
2023-03-17  14:25:15.352  [main] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.linkCapacity: 16
2023-03-17  14:25:15.352  [main] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.ratio: 8
2023-03-17  14:25:15.358  [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkAccessible: true
2023-03-17  14:25:15.358  [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkBounds: true
2023-03-17  14:25:15.359  [nioEventLoopGroup-2-1] DEBUG io.netty.util.ResourceLeakDetectorFactory:  Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@43140ebb
```









## 总结

* 把 channel 理解为数据的通道
* 把 msg 理解为流动的数据，最开始输入是 ByteBuf，但经过 pipeline 的加工，会变成其它类型对象，最后输出又变成 ByteBuf
* 把 handler 理解为数据的处理工序
  * 工序有多道，合在一起就是 pipeline，pipeline 负责发布事件（读、读取完成...）传播给每个 handler， handler 对自己感兴趣的事件进行处理（重写了相应事件处理方法）
  * handler 分 Inbound 和 Outbound 两类
* 把 eventLoop 理解为处理数据的工人
  * 工人可以管理多个 channel 的 io 操作，并且一旦工人负责了某个 channel，就要负责到底（绑定）
  * 工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个 channel 的待处理任务，任务分为普通任务、定时任务
  * 工人按照 pipeline 顺序，依次按照 handler 的规划（代码）处理数据，可以为每道工序指定不同的工人







