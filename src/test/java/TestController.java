import core.annotation.Controller;
import ioc.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TestController {
    @Autowired
    private TestService testService;

    public void hello() {
        log.info(testService.helloWorld());
    }
}
