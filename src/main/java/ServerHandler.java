import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ivan
 * Date: 21.09.14
 * Time: 18:27
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;
    private final StringBuilder buf = new StringBuilder();
    private Map<String, UriHandlerBased> handlers = new HashMap<String, UriHandlerBased>();

    public ServerHandler() {
        if (handlers.size() == 0) {
            handlers.put("/h1", new UriHandler());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        UriHandlerBased handler = null;
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            buf.setLength(0);
            String context = queryStringDecoder.path();
            handler = handlers.get(context);
            if (handler != null) {
                handler.process(request, buf);
            }
        }
        if (msg instanceof LastHttpContent) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    ((LastHttpContent) msg).getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
                    Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8)
            );
            response.headers().set(CONTENT_TYPE, handler.getContentType());

            if (isKeepAlive(request)) {
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            }
            ctx.write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
