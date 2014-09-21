import io.netty.handler.codec.http.HttpRequest;

/**
 * @Author: ivan
 * Date: 21.09.14
 * Time: 18:29
 */
public abstract class UriHandlerBased {

    public abstract void process(HttpRequest request, StringBuilder buff);

    public String getContentType() {
        return "text/plain; charset=UTF-8";
    }
}
