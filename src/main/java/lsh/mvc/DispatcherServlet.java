package lsh.mvc;

import lombok.extern.slf4j.Slf4j;
import lsh.mvc.handler.ControllerHandler;
import lsh.mvc.handler.Handler;
import lsh.mvc.handler.JspHandler;
import lsh.mvc.handler.PreRequestHandler;
import lsh.mvc.handler.SimpleUrlHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All http requests will be forwarded by DispatcherServlet
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {
//    private ControllerHandler controllerHandler = new ControllerHandler();
//
//    private ResultRender resultRender = new ResultRender();
//
//    @Override
//    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setCharacterEncoding("UTF-8");
//        // get method name and request path
//        String reqMethod = req.getMethod();
//        String reqPathInfo = req.getPathInfo();
//        log.info("[config] {} {}", reqMethod, reqPathInfo);
//        if (reqPathInfo.endsWith("/")) {
//            reqPathInfo = reqPathInfo.substring(0, reqPathInfo.length() - 1);
//        }
//        ControllerInfo controllerInfo = controllerHandler.getController(reqMethod, reqPathInfo);
//        log.info("{}", controllerInfo);
//        if (null == controllerInfo) {
//            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//        resultRender.invokeController(req, resp, controllerInfo);
//    }

    private final List<Handler> handler = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        handler.add(new PreRequestHandler());
        handler.add(new SimpleUrlHandler(getServletContext()));
        handler.add(new JspHandler(getServletContext()));
        handler.add(new ControllerHandler());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestHandlerChain requestHandlerChain = new RequestHandlerChain(handler.iterator(), req, resp);
        requestHandlerChain.doHandlerChain();
        requestHandlerChain.doRender();
    }
}
