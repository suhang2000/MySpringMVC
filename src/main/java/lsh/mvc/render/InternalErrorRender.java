package lsh.mvc.render;

import lsh.mvc.RequestHandlerChain;

import javax.servlet.http.HttpServletResponse;

public class InternalErrorRender implements Render{
    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        handlerChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
