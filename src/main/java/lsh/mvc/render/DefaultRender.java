package lsh.mvc.render;

import lsh.mvc.RequestHandlerChain;

public class DefaultRender implements Render{
    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        int status = handlerChain.getResponseStatusCode();
        handlerChain.getResponse().setStatus(status);
    }
}
