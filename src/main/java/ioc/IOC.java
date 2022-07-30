package ioc;

import core.BeanContainer;
import ioc.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import util.ClassUtil;

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
                    final Class<?> fieldType = field.getType();
                    Object fieldValue = getClassInstance(fieldType); // create instance
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
                    Class<?> implementClass = getImplementClass(clz);
                    if (null != implementClass) {
                        return beanContainer.getBean(implementClass);
                    }
                    return null;
                });
    }

    private Class<?> getImplementClass(final Class<?> interfaceClass) {
        return beanContainer.getClassesBySuper(interfaceClass)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
