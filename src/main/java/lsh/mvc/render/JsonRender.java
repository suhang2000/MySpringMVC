package lsh.mvc.render;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lsh.mvc.RequestHandlerChain;

import java.io.PrintWriter;

@Slf4j
public class JsonRender implements Render{
    private Object jsonData;

    public JsonRender(Object jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        handlerChain.getResponse().setContentType("application/json");
        handlerChain.getResponse().setCharacterEncoding("UTF-8");

        try (PrintWriter writer = handlerChain.getResponse().getWriter()) {
            writer.write(JSON.toJSONString(jsonData));
            writer.flush();
        }
    }
}
