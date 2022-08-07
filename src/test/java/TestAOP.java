import aop.AOP;
import core.BeanContainer;
import core.annotation.Controller;
import ioc.IOC;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestAOP {
    @Test
    public void doAOP() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("");
        new AOP().doAOP();
        new IOC().doIOC();
        TestController controller = (TestController) beanContainer.getBean(TestController.class);
        controller.hello();
        controller.helloForAspect();
    }
}
