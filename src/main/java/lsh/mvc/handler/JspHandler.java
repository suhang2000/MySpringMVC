package lsh.mvc.handler;

import lombok.extern.slf4j.Slf4j;
import lsh.MainApplication;
import lsh.mvc.RequestHandlerChain;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

@Slf4j
public class JspHandler implements Handler{
    private static final String JSP_SERVLET = "jsp";
    // jsp RequestDispatcher, process jsp resources
    private RequestDispatcher jspRequestDispatcher;

    public JspHandler(ServletContext servletContext) {
        jspRequestDispatcher = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (null == jspRequestDispatcher) {
            throw new RuntimeException("No JSP Servlet");
        }
    }
    @Override
    public boolean handle(RequestHandlerChain handlerChain) throws Exception {
        if (isPageView(handlerChain.getRequestPath())) {
            jspRequestDispatcher.forward(handlerChain.getRequest(), handlerChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * determine if jsp resource
     */
    private boolean isPageView(String url) {
        return url.startsWith(MainApplication.getConfiguration().getViewPath());
    }
}
