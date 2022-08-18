package lsh.mvc.handler;

import lsh.mvc.RequestHandlerChain;

public interface Handler {
    // request handler
    boolean handle(final RequestHandlerChain handlerChain) throws Exception;
}
