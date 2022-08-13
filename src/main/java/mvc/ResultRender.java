package mvc;

import com.alibaba.fastjson.JSON;
import core.BeanContainer;
import lombok.extern.slf4j.Slf4j;
import mvc.annotation.ResponseBody;
import mvc.bean.ModelAndView;
import util.CastUtil;
import util.ValidateUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ResultRender {
    private BeanContainer beanContainer;

    public ResultRender() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * invoke method in controller
     */
    public void invokeController(HttpServletRequest request, HttpServletResponse response, ControllerInfo controllerInfo) {
        // 获取 HttpServletRequest 所有参数; Get parameters from http request
        Map<String, String> requestParam = getRequestParams(request);
        // 实例化调用方法要传入的参数值; initialize parameter instance
        List<Object> methodParams = instantiateMethodArgs(controllerInfo.getMethodParameter(), requestParam);
        Object controller = beanContainer.getBean(controllerInfo.getControllerClass());
        Method invokeMethod = controllerInfo.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        // invoke method by reflection
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // return page or JSON
        resultResolver(controllerInfo, result, request, response);
    }

    /**
     * parse result after executing controller
     */
    private void resultResolver(ControllerInfo controllerInfo, Object result, HttpServletRequest request, HttpServletResponse response) {
        if (null == result) {
            return;
        }
        boolean isJSON = controllerInfo.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJSON) {
            // set response header
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(JSON.toJSONString(result));
                writer.flush();
            } catch (IOException e) {
                log.error("forwarding request failed", e);
                throw new RuntimeException(e);
            }
        } else {
            String path;
            if (result instanceof ModelAndView) {
                ModelAndView modelAndView = (ModelAndView) result;
                path = modelAndView.getView();
                Map<String, Object> model = modelAndView.getModel();
                if (ValidateUtil.isNotEmpty(model)) {
                    for (Map.Entry<String, Object> entry : model.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
            } else if (result instanceof String) {
                path = (String) result;
            } else {
                throw new RuntimeException("return value not supported");
            }
            try {
                request.getRequestDispatcher("/templates/" + path).forward(request, response);
            } catch (ServletException | IOException e) {
                log.error("forwarding request failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * retrieve parameter from http request
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        // Get requestParam (GET or POST)
        request.getParameterMap().forEach((paramName, paramValues) -> {
            if (ValidateUtil.isNotEmpty(paramValues)) {
                paramMap.put(paramName, paramValues[0]);
            }
        });
        return paramMap;
    }

    /**
     * 实例化方法参数; Instantiate method parameters
     */
    private List<Object> instantiateMethodArgs(Map<String, Class<?>> methodParams, Map<String, String> requestParams) {
        return methodParams.keySet().stream()
                .map(paramName -> {
                    Class<?> type = methodParams.get(paramName);
                    String requestValue = requestParams.get(paramName);
                    Object value;
                    if (null == requestValue) {
                        value = CastUtil.primitiveNull(type);
                    } else {
                        value = CastUtil.convert(type, requestValue);
                    }
                    return value;
                }).collect(Collectors.toList());
    }
}
