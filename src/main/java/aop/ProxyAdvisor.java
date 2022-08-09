package aop;

import aop.advice.Advice;
import aop.advice.AfterReturningAdvice;
import aop.advice.MethodBeforeAdvice;
import aop.advice.ThrowsAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyAdvisor {
    private Advice advice;
    private ProxyPointcut pointcut;
    // execute order
    private int order;
//    public Object doProxy(Object target, Class<?> targetClass, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    // callback
    public Object doProxy(AdviceChain adviceChain) throws Throwable {
        Object result = null;
        Class<?> targetClass = adviceChain.getTargetClass();
        Method method = adviceChain.getMethod();
        Object[] args = adviceChain.getArgs();
        if (advice instanceof MethodBeforeAdvice) {
            ((MethodBeforeAdvice) advice).before(targetClass, method, args);
        }
        try {
            // execute proxy advice chain method 执行代理链方法
            result = adviceChain.doAdviceChain();
            if (advice instanceof AfterReturningAdvice) {
                ((AfterReturningAdvice) advice).afterReturning(targetClass, result, method, args);
            }
        } catch (Exception e) {
            if (advice instanceof ThrowsAdvice) {
                ((ThrowsAdvice) advice).afterThrowing(targetClass, method, args, e);
            } else {
                throw new Throwable(e);
            }
        }
        return result;
    }
}
