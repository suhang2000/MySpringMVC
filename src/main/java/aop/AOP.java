package aop;

import aop.advice.Advice;
import aop.annotation.Aspect;
import core.BeanContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AOP {
    private BeanContainer beanContainer;

    public AOP() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doAOP() {
        beanContainer.getClassesBySuper(Advice.class)
                // get implementation class of `Advice`
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(Aspect.class))
                // find the classes annotated with `Aspect`
                .forEach(clazz -> {
                    final Advice advice = (Advice) beanContainer.getBean(clazz);  // aspect
                    Aspect aspect = clazz.getAnnotation(Aspect.class);
                    beanContainer.getClassesByAnnotation(aspect.target())
                            .stream()
                            .filter(target -> !Advice.class.isAssignableFrom(target))
                            // target is not implementation class of `Advice`
                            .filter(target -> !target.isAnnotationPresent(Aspect.class))
                            // target is not annotated with `Aspect`
                            .forEach(target -> {
                                ProxyAdvisor advisor = new ProxyAdvisor(advice);
                                Object proxyBean = ProxyCreator.createProxy(target, advisor);
                                beanContainer.addBean(target, proxyBean);
                            });
                });
    }
}
