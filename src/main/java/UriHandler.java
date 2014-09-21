import io.netty.handler.codec.http.HttpRequest;

/**
 * @Author: ivan
 * Date: 21.09.14
 * Time: 19:05
 */
public class UriHandler extends UriHandlerBased {

    @Override
    public void process(HttpRequest request, StringBuilder buff) {

        System.out.println("Server :: Received a new message on uri: " +request.getUri());
        buff.append("HELLO HANDLER1!");
    }
}
