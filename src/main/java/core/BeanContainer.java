package core;

import core.annotation.Component;
import core.annotation.Controller;
import core.annotation.Repository;
import core.annotation.Service;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {
    // A Map of all beans
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    // whether to load bean
    private boolean isLoadBean = false;

    // annotations list of bean
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class);

    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * get instance of bean
     */
    public Object getBean(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        return beanMap.get(clazz);
    }

    /**
     * get all beans
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    public void removeBean(Class<?> clazz) {
        beanMap.remove(clazz);
    }

    public int size() {
        return beanMap.size();
    }

    /**
     * all classes in beanMap
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * get classes in beanMap by annotation
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return beanMap.keySet()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    /**
     * get classes in beanMap by Implementation class or Parent class
     */
    public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
        return beanMap.keySet()
                .stream()
                .filter(superClass::isAssignableFrom)
                .filter(clazz -> !clazz.equals(superClass))
                .collect(Collectors.toSet());
    }

    /**
     * scan and load bean
     */
    public void loadBeans(String basePackage) {
        if (isLoadBean()) {
            log.warn("bean has been loaded");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);
        classSet.stream()
                .filter(clazz -> {
                    for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                        if (clazz.isAnnotationPresent(annotation)) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(clazz -> beanMap.put(clazz, ClassUtil.newInstance(clazz)));
        isLoadBean = true;
    }

    private boolean isLoadBean() {
        return isLoadBean;
    }
}
