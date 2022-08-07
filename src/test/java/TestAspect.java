import aop.advice.AroundAdvice;
import aop.annotation.Aspect;
import core.annotation.Controller;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@Aspect(pointcut = "execution(* TestController.helloForAspect(..))")
public class TestAspect implements AroundAdvice {
    @Override
    public void afterReturning(Class<?> clazz, Object returnValue, Method method, Object[] args) throws Throwable {
        log.info("After Aspect --> class: {}, method: {}", clazz.getName(), method.getName());
    }

    @Override
    public void before(Class<?> clazz, Method method, Object[] args) throws Throwable {
        log.info("Before Aspect --> class: {}, method: {}", clazz.getName(), method.getName());
    }

    @Override
    public void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable e) {
        log.error("Throw Aspect --> class: {}, method: {}, exception: {}", clazz, method.getName(), e.getMessage());
    }
}
