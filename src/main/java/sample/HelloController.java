package sample;

import lsh.core.annotation.Controller;
import lsh.mvc.annotation.RequestMapping;
import lsh.mvc.annotation.RequestParam;
import lsh.mvc.annotation.ResponseBody;

@Controller
@RequestMapping
public class HelloController {
    @RequestMapping
    @ResponseBody
    public String hello() {
        System.out.println("hello world");
        return "hello world";
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test1(@RequestParam("paramName") String paramName) {
        System.out.println(paramName);
        return paramName;
    }
}
