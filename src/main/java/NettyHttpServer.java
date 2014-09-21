import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @Author: ivan
 * Date: 21.09.14
 * Time: 18:30
 */
public class NettyHttpServer {

    private final int port;

    public NettyHttpServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            Channel ch = b.bind(port).sync().channel();
            System.out.println("Server :: Successfully bound to port " + port + "!" +
                    "\nAwaiting new connections...");
            ch.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            p.addLast("decoder", new HttpRequestDecoder());
            p.addLast("encoder", new HttpResponseEncoder());
            p.addLast("handler", new ServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyHttpServer(8081).start();
    }
}
