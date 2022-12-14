import lsh.core.annotation.Controller;
import lsh.ioc.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TestController {
    @Autowired
    private TestService testService;

    public void hello() {
        log.info(testService.helloWorld());
    }

    public void helloForAspect() {
        log.info("Hello AspectJ");
    }
}
