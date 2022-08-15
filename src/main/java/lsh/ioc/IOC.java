package lsh.ioc;

import lsh.core.BeanContainer;
import lsh.ioc.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import lsh.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
public class IOC {
    private BeanContainer beanContainer;

    public IOC() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * do IOC
     */
    public void doIOC() {
        for (Class<?> clz : beanContainer.getClasses()) {
            // traverse beans
            final Object targetBean = beanContainer.getBean(clz);
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                // traverse fields in a bean
                if (field.isAnnotationPresent(Autowired.class)) {
                    // this field is annotated by Autowired
                    final Class<?> fieldType = field.getType();  // get the class of given field, then retrieve the instance by the class
                    Object fieldValue = getClassInstance(fieldType);  // get instance by above type class
                    if (null != fieldValue) {
                        ClassUtil.setField(field, targetBean, fieldValue);
                    } else {
                        throw new RuntimeException("Can't inject class: " + fieldType.getName());
                    }
                }
            }
        }
    }

    private Object getClassInstance(final Class<?> clz) {
        return Optional.ofNullable(beanContainer.getBean(clz))
                .orElseGet(() -> {
                    // if there is no matching class in beanContainer
                    // maybe this class is an interface
                    // then we go to find its implementation class
                    Class<?> implementClass = getImplementClass(clz);
                    if (null != implementClass) {
                        return beanContainer.getBean(implementClass);
                    }
                    return null;
                });
    }

    // get the implementation class of an interface
    private Class<?> getImplementClass(final Class<?> interfaceClass) {
        return beanContainer.getClassesBySuper(interfaceClass)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
