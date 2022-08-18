package lsh.mvc.handler;

import lombok.extern.slf4j.Slf4j;
import lsh.core.BeanContainer;
import lsh.mvc.ControllerInfo;
import lsh.mvc.PathInfo;
import lsh.mvc.RequestHandlerChain;
import lsh.mvc.annotation.RequestMapping;
import lsh.mvc.annotation.RequestParam;
import lsh.mvc.annotation.ResponseBody;
import lsh.mvc.render.JsonRender;
import lsh.mvc.render.NotFoundRender;
import lsh.mvc.render.Render;
import lsh.mvc.render.ViewRender;
import lsh.util.CastUtil;
import lsh.util.ValidateUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ControllerHandler implements Handler{
    private Map<PathInfo, ControllerInfo> pathControllerMap = new ConcurrentHashMap<>();
    private BeanContainer beanContainer;

    public ControllerHandler() {
        beanContainer = BeanContainer.getInstance();

        Set<Class<?>> mappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        initPathControllerMap(mappingSet);
    }

    private void initPathControllerMap(Set<Class<?>> mappingSet) {
        mappingSet.forEach(this::addPathController);
    }

    private void addPathController(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        String basePath = requestMapping.value();
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                String methodPath = methodRequest.value();
                if (!methodPath.startsWith("/")) {
                    methodPath = "/" + methodPath;
                }
                String url;
                if (basePath.equals("/")) {
                    url = methodPath;
                }else {
                    url = basePath + methodPath;
                }
                Map<String, Class<?>> methodParams = this.getMethodParams(method);
                String httpMethod = String.valueOf(methodRequest.method());
                PathInfo pathInfo = new PathInfo(httpMethod, url);

                if (pathControllerMap.containsKey(pathInfo)) {
                    log.warn("repeated url: {}", pathInfo.getHttpPath());
                }
                ControllerInfo controllerInfo = new ControllerInfo(clazz, method, methodParams);

                this.pathControllerMap.put(pathInfo, controllerInfo);
                log.info("mapped:[{} {}], controller:[{}@{}]",
                        pathInfo.getHttpMethod(), pathInfo.getHttpPath(),
                        controllerInfo.getControllerClass().getName(), controllerInfo.getInvokeMethod().getName());
            }
        }
    }

    private Map<String, Class<?>> getMethodParams(Method method) {
        Map<String, Class<?>> map = new HashMap<>();
        for (Parameter parameter : method.getParameters()) {
            RequestParam param = parameter.getAnnotation(RequestParam.class);
            if (null == param) {
                throw new RuntimeException("Parameter should be annotated with RequestParam");
            }
            map.put(param.value(), parameter.getType());
        }
        return map;
    }

    @Override
    public boolean handle(RequestHandlerChain handlerChain) throws Exception {
        String requestMethod = handlerChain.getRequestMethod();
        String requestPath = handlerChain.getRequestPath();
        ControllerInfo controllerInfo = pathControllerMap.get(new PathInfo(requestMethod, requestPath));
        if (null == controllerInfo) {
            handlerChain.setRender(new NotFoundRender());
            return false;
        }
        Object result = invokeController(controllerInfo, handlerChain.getRequest());
        setRender(result, controllerInfo, handlerChain);
        return true;
    }

    private void setRender(Object result, ControllerInfo controllerInfo, RequestHandlerChain handlerChain) {
        if (null == result) return;
        Render render;
        boolean isJson = controllerInfo.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            render = new JsonRender(result);
        } else {
            render = new ViewRender(result);
        }
        handlerChain.setRender(render);
    }

    private Object invokeController(ControllerInfo controllerInfo, HttpServletRequest request) {
        Map<String, String> requestParams = getRequestParams(request);
        List<Object> methodParams = instantiateMethodArgs(controllerInfo.getMethodParameter(), requestParams);

        Object controller = beanContainer.getBean(controllerInfo.getControllerClass());
        Method invokeMethod = controllerInfo.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private List<Object> instantiateMethodArgs(Map<String, Class<?>> methodParameter, Map<String, String> requestParams) {
        return methodParameter.keySet().stream().map(paramName -> {
                    Class<?> type = methodParameter.get(paramName);
                    String requestValue = requestParams.get(paramName);
                    Object result;
                    if (null == requestValue) {
                        result = CastUtil.primitiveNull(type);
                    } else {
                        result = CastUtil.convert(type, requestValue);
                    }
                    return result;
                }).collect(Collectors.toList());
    }

    /**
     * Get parameters from HttpServletRequest
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterMap().forEach((name, value) -> {
            if (ValidateUtil.isNotEmpty(value)) {
                paramMap.put(name, value[0]);
            }
        });
        return paramMap;
    }
}
