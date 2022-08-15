package lsh.aop.advice;

import java.lang.reflect.Method;

public interface AfterReturningAdvice {
    void afterReturning(Class<?> clazz, Object returnValue, Method method, Object[] args) throws Throwable;
}
