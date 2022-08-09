import aop.advice.AroundAdvice;
import aop.annotation.Aspect;
import aop.annotation.Order;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@Order(2)
@Aspect(pointcut = "@within(core.annotation.Controller)")
public class TestAspect2 implements AroundAdvice {
    @Override
    public void afterReturning(Class<?> clazz, Object returnValue, Method method, Object[] args) throws Throwable {
        log.info("After Aspect 2 --> class: {}, method: {}", clazz.getName(), method.getName());
    }

    @Override
    public void before(Class<?> clazz, Method method, Object[] args) throws Throwable {
        log.info("Before Aspect 2 --> class: {}, method: {}", clazz.getName(), method.getName());
    }

    @Override
    public void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable e) {
        log.error("Throw Aspect 2 --> class: {}, method: {}, exception: {}", clazz, method.getName(), e.getMessage());
    }
}
