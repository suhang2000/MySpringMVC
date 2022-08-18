package lsh.mvc.render;

import lsh.mvc.RequestHandlerChain;

public interface Render {
    void render(RequestHandlerChain handlerChain) throws Exception;
}
