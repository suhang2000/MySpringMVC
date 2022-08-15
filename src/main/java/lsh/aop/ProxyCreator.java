package lsh.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.List;
//import net.sf.cglib.proxy.MethodProxy;
//
//import java.lang.reflect.Method;

public class ProxyCreator {
//    public static Object createProxy(Class<?> targetClass, ProxyAdvisor proxyAdvisor) {
////        Enhancer enhancer = new Enhancer();
////        enhancer.setSuperclass(targetClass);
////        enhancer.setCallback(new MethodInterceptor() {
////            @Override
////            public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
////                return proxyAdvisor.doProxy(target, targetClass, method, args, methodProxy);
////            }
////        });
////        MethodInterceptor methodInterceptor = (target, method, args, methodProxy) -> proxyAdvisor.doProxy(target, targetClass, method, args, methodProxy);
////        enhancer.setCallback(methodInterceptor);
////        return enhancer.create();
//        // same as above
//        return Enhancer.create(targetClass, (MethodInterceptor)(target, method, args, methodProxy) ->
//                proxyAdvisor.doProxy(target, targetClass, method, args, methodProxy));
//    }

    public static Object createProxy(Class<?> targetClass, List<ProxyAdvisor> proxyAdvisorList) {
        return Enhancer.create(targetClass, (MethodInterceptor)(target, method, args, methodProxy) ->
                new AdviceChain(targetClass, target, method, args, methodProxy, proxyAdvisorList).doAdviceChain());
    }
}
