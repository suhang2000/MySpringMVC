package lsh.mvc;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * All http requests will be forwarded by DispatcherServlet
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {
    private ControllerHandler controllerHandler = new ControllerHandler();

    private ResultRender resultRender = new ResultRender();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        // get method name and request path
        String reqMethod = req.getMethod();
        String reqPathInfo = req.getPathInfo();
        log.info("[config] {} {}", reqMethod, reqPathInfo);
        if (reqPathInfo.endsWith("/")) {
            reqPathInfo = reqPathInfo.substring(0, reqPathInfo.length() - 1);
        }
        ControllerInfo controllerInfo = controllerHandler.getController(reqMethod, reqPathInfo);
        log.info("{}", controllerInfo);
        if (null == controllerInfo) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resultRender.invokeController(req, resp, controllerInfo);
    }
}
