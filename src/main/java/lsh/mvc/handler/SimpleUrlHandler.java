package lsh.mvc.handler;

import lombok.extern.slf4j.Slf4j;
import lsh.MainApplication;
import lsh.mvc.RequestHandlerChain;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * Process static resources
 */
@Slf4j
public class SimpleUrlHandler implements Handler{
    // tomcat default RequestDispatcher's name
    private static final String TOMCAT_DEFAULT_SERVLET = "default";
    // default RequestDispatcher, process static resources
    private RequestDispatcher defaultRequestDispatcher;

    public SimpleUrlHandler(ServletContext servletContext) {
        defaultRequestDispatcher = servletContext.getNamedDispatcher(TOMCAT_DEFAULT_SERVLET);

        if (null == defaultRequestDispatcher) {
            throw new RuntimeException("No default servlet");
        }

        log.info("Default servlet for static resource is [{}]", TOMCAT_DEFAULT_SERVLET);
    }
    @Override
    public boolean handle(RequestHandlerChain handlerChain) throws Exception {
        if (isStaticResource(handlerChain.getRequestPath())) {
            defaultRequestDispatcher.forward(handlerChain.getRequest(), handlerChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * determine if static resources
     */
    private boolean isStaticResource(String url) {
        return url.startsWith(MainApplication.getConfiguration().getAssetPath());
    }
}
