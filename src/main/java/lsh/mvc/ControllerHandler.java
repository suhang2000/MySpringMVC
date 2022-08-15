package lsh.mvc;

import lsh.core.BeanContainer;
import lombok.extern.slf4j.Slf4j;
import lsh.mvc.annotation.RequestMapping;
import lsh.mvc.annotation.RequestMethod;
import lsh.mvc.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ControllerHandler {
    // path -> controller
    private Map<PathInfo, ControllerInfo> pathControllerMap = new ConcurrentHashMap<>();

    private BeanContainer beanContainer;

    public ControllerHandler() {
        beanContainer = BeanContainer.getInstance();
        Set<Class<?>> requestMappingClassSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        for (Class<?> clazz : requestMappingClassSet) {
            putPathController(clazz);
        }
    }

    private void putPathController(Class<?> clazz) {
        RequestMapping controllerRequestMapping = clazz.getAnnotation(RequestMapping.class);
        String basePath = controllerRequestMapping.value();
        Method[] controllerMethods = clazz.getDeclaredMethods();
        // 遍历 Controller 中的方法; traverse methods in controller
        for (Method method : controllerMethods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                // 获取方法的参数名字和参数类型; Get parameter name and parameter type
                Map<String, Class<?>> params = new HashMap<>();
                for (Parameter methodParameter : method.getParameters()) {
                    RequestParam requestParam = methodParameter.getAnnotation(RequestParam.class);
                    if (null == requestParam) {
                        throw new RuntimeException("No parameter name specified by RequestParam");
                    }
                    params.put(requestParam.value(), methodParameter.getType());
                }
                // 获取方法上的 RequestMapping 注解; Get RequestMapping annotation on the method
                RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                String methodPath = methodRequestMapping.value();
                RequestMethod requestMethod = methodRequestMapping.method();
                PathInfo pathInfo = new PathInfo(requestMethod.name(), basePath + methodPath);
                if (pathControllerMap.containsKey(pathInfo)) {
                    log.error("url:{} repeat registration", pathInfo.getHttpPath());
                    throw new RuntimeException("url repeat registration");
                }
                // 生成 ControllerInfo 并存入 Map 中; Create ControllerInfo and put it into PathControllerMap
                ControllerInfo controllerInfo = new ControllerInfo(clazz, method, params);
                pathControllerMap.put(pathInfo, controllerInfo);
                log.info("Add Controller RequestMethod: {}, RequestPath: {}, Controller: {}, Method: {}",
                        pathInfo.getHttpMethod(), pathInfo.getHttpPath(),
                        controllerInfo.getControllerClass().getName(), controllerInfo.getInvokeMethod().getName());
            }
        }
    }

    /**
     * Get ControllerInfo by requestMethod and requestPath
     */
    public ControllerInfo getController(String requestMethod, String requestPath) {
        PathInfo pathInfo = new PathInfo(requestMethod, requestPath);
        return pathControllerMap.get(pathInfo);
    }
}
