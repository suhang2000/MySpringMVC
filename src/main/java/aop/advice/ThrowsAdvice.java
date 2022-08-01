package aop.advice;

import java.lang.reflect.Method;

public interface ThrowsAdvice {
    void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable e);
}
