package lsh.mvc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lsh.mvc.handler.Handler;
import lsh.mvc.render.DefaultRender;
import lsh.mvc.render.InternalErrorRender;
import lsh.mvc.render.Render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * HTTP request handler chain.
 */
@Data
@Slf4j
public class RequestHandlerChain {
    // handler iterator
    private Iterator<Handler> handlerIterator;
    // http request
    private HttpServletRequest request;
    // http response
    private HttpServletResponse response;
    // http method
    private String requestMethod;
    // http path
    private String requestPath;
    // response status code
    private int responseStatusCode;
    // result render
    private Render render;

    public RequestHandlerChain(Iterator<Handler> handlerIterator, HttpServletRequest request, HttpServletResponse response) {
        this.handlerIterator = handlerIterator;
        this.request = request;
        this.response = response;
        this.requestMethod = request.getMethod();
        this.requestPath = request.getPathInfo();
        this.responseStatusCode = HttpServletResponse.SC_OK;
    }

    /**
     * execute handler chain
     */
    public void doHandlerChain() {
        try {
            while (handlerIterator.hasNext()) {
                if (!handlerIterator.next().handle(this)) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("doHandlerChain error", e);
            render = new InternalErrorRender();
        }
    }

    /**
     * execute render
     */
    public void doRender() {
        if (null == render) {
            render = new DefaultRender();
        }
        try {
            render.render(this);
        } catch (Exception e) {
            log.error("doRender error", e);
            throw new RuntimeException(e);
        }
    }
}
