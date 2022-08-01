import core.BeanContainer;
import ioc.IOC;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestIOC {
    @Test
    public void doIOCTest() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("");
        new IOC().doIOC();
        TestController controller = (TestController) beanContainer.getBean(TestController.class);
        controller.hello();
    }
}
