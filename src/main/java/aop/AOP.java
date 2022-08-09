package aop;

import aop.advice.Advice;
import aop.annotation.Aspect;
import aop.annotation.Order;
import core.BeanContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AOP {
    private BeanContainer beanContainer;

    public AOP() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doAOP() {
//        beanContainer.getClassesBySuper(Advice.class)
//                // get implementation class of `Advice`
//                .stream()
//                .filter(clazz -> clazz.isAnnotationPresent(Aspect.class))
//                // find the classes annotated with `Aspect`
//                .forEach(clazz -> {
//                    final Advice advice = (Advice) beanContainer.getBean(clazz);  // aspect
//                    Aspect aspect = clazz.getAnnotation(Aspect.class);
//                    beanContainer.getClassesByAnnotation(aspect.target())
//                            .stream()
//                            .filter(target -> !Advice.class.isAssignableFrom(target))
//                            // target is not implementation class of `Advice`
//                            .filter(target -> !target.isAnnotationPresent(Aspect.class))
//                            // target is not annotated with `Aspect`
//                            .forEach(target -> {
//                                ProxyAdvisor advisor = new ProxyAdvisor(advice);
//                                Object proxyBean = ProxyCreator.createProxy(target, advisor);
//                                beanContainer.addBean(target, proxyBean);
//                            });
//                });
        // 创建所有的代理通知列表 create proxy advisor list
        List<ProxyAdvisor> proxyAdvisorList = beanContainer.getClassesBySuper(Advice.class)
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(Aspect.class))  // @Aspect
                .map(this::createProxyAdvisor)
                .collect(Collectors.toList());
        beanContainer.getClasses()
                .stream()
                .filter(clazz -> !Advice.class.isAssignableFrom(clazz))
                .filter(clazz -> !clazz.isAnnotationPresent(Aspect.class))
                .forEach(clazz -> {
                    List<ProxyAdvisor> matchProxy = createMatchProxies(proxyAdvisorList, clazz);
                    if (matchProxy.size() > 0) {
                        Object proxyBean = ProxyCreator.createProxy(clazz, matchProxy);
                        beanContainer.addBean(clazz, proxyBean);
                    }
                });
//        beanContainer.getClassesBySuper(Advice.class)
//                .stream()
//                .filter(clazz -> clazz.isAnnotationPresent(Aspect.class))  // first, find the aspect
//                .map(this::createProxyAdvisor)
//                .forEach(proxyAdvisor -> beanContainer.getClasses()
//                        .stream()
//                        .filter(targetClass -> !Advice.class.isAssignableFrom(targetClass))
//                        .filter(targetClass -> !targetClass.isAnnotationPresent(Aspect.class))
//                        // 除了切面类的所有类
//                        .forEach(targetClass -> {
//                            if (proxyAdvisor.getPointcut().matches(targetClass)) {
//                                Object proxyBean = ProxyCreator.createProxy(targetClass, proxyAdvisor);
//                                beanContainer.addBean(targetClass, proxyBean);
//                            }
//                        }));
    }

    /**
     * create proxy advisor class through aspect class
     * 通过 Aspect 切面类创建代理通知类
     */
    private ProxyAdvisor createProxyAdvisor(Class<?> aspectClass) {
        int order = 0;
        if (aspectClass.isAnnotationPresent(Order.class)) {
            order = aspectClass.getAnnotation(Order.class).value();
        }
        String pointcutExpression = aspectClass.getAnnotation(Aspect.class).pointcut();
        ProxyPointcut proxyPointcut = new ProxyPointcut();
        proxyPointcut.setExpression(pointcutExpression);
        Advice advice = (Advice) beanContainer.getBean(aspectClass);
        return new ProxyAdvisor(advice, proxyPointcut, order);
    }

    /**
     * aspect list of matched target class 匹配目标类的切面集合
     *
     * @param proxyAdvisorList list of all proxy
     * @param targetClass      target class to be matched
     * @return matched proxy advisors
     */
    private List<ProxyAdvisor> createMatchProxies(List<ProxyAdvisor> proxyAdvisorList, Class<?> targetClass) {
        return proxyAdvisorList.stream()
                .filter(proxyAdvisor -> proxyAdvisor.getPointcut().matches(targetClass))
                .sorted(Comparator.comparingInt(ProxyAdvisor::getOrder))
                .collect(Collectors.toList());
    }
}
