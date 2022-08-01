package aop.advice;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends Advice{
    // Why is here declared to throw a Throwable?
    // Because the original method (i.e. the "before" method), might throw an Error, RuntimeException, or a checked exception.
    // So it only makes sense to declare "before" to throw a Throwable.
    void before(Class<?> clazz, Method method, Object[] args) throws Throwable;
}
