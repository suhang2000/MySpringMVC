package sample;

import lsh.core.annotation.Controller;
import lsh.mvc.annotation.RequestMapping;
import lsh.mvc.annotation.ResponseBody;

@Controller
@RequestMapping
public class HelloController {
    @RequestMapping
    @ResponseBody
    public String hello() {
        return "hello world";
    }
}
