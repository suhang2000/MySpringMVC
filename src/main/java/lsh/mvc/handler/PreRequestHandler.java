package lsh.mvc.handler;

import lombok.extern.slf4j.Slf4j;
import lsh.mvc.RequestHandlerChain;

/**
 * request preprocessing
 */
@Slf4j
public class PreRequestHandler implements Handler{
    @Override
    public boolean handle(RequestHandlerChain handlerChain) throws Exception {
        // set http encoding
        handlerChain.getRequest().setCharacterEncoding("UTF-8");
        // process request path
        String requestPath = handlerChain.getRequestPath();
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            handlerChain.setRequestPath(requestPath.substring(0, requestPath.length()-1));
        }
        log.info("PreProcessing {} {}", handlerChain.getRequestMethod(), handlerChain.getRequestPath());
        return true;
    }
}
