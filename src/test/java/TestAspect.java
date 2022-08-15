import lsh.aop.advice.AroundAdvice;
import lsh.aop.annotation.Aspect;
import lsh.aop.annotation.Order;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@Order(1)
@Aspect(pointcut = "@within(lsh.core.annotation.Controller)")
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
