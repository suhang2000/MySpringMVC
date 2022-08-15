package lsh.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * implement multiple proxy by AdviceChain
 */
public class AdviceChain {
    // target class
    @Getter
    private final Class<?> targetClass;
    // target instance
    @Getter
    private final Object target;
    // target method
    @Getter
    private final Method method;
    // target method parameters
    @Getter
    private final Object[] args;
    // proxy method
    private final MethodProxy methodProxy;
    // proxy advisor list
    private List<ProxyAdvisor> proxyAdvisorList;
    // index of proxyAdvisorList
    private int adviceIndex = 0;

    public AdviceChain(Class<?> targetClass, Object target, Method method, Object[] args, MethodProxy methodProxy, List<ProxyAdvisor> proxyAdvisorList) {
        this.targetClass = targetClass;
        this.target = target;
        this.method = method;
        this.args = args;
        this.methodProxy = methodProxy;
        this.proxyAdvisorList = proxyAdvisorList;
    }

    public Object doAdviceChain() throws Throwable {
        Object result = null;
        while (adviceIndex < proxyAdvisorList.size()) {
            if (proxyAdvisorList.get(adviceIndex).getPointcut().matches(method)) {
                break;
            }
            adviceIndex++;
        }
        if (adviceIndex < proxyAdvisorList.size()) {
            // do proxy method
            result = proxyAdvisorList.get(adviceIndex++).doProxy(this);
        } else {
            // method does not match the pointcut, go to execute the original method
            result = methodProxy.invokeSuper(target, args);
        }
        return result;
    }
}
