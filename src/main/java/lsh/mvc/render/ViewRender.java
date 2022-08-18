package lsh.mvc.render;

import lombok.extern.slf4j.Slf4j;
import lsh.MainApplication;
import lsh.mvc.RequestHandlerChain;
import lsh.mvc.bean.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class ViewRender implements Render{
    private ModelAndView mv;

    public ViewRender(Object mv) {
        if (mv instanceof ModelAndView) {
            this.mv = (ModelAndView) mv;
        } else if (mv instanceof String) {
            this.mv = new ModelAndView().setView((String) mv);
        } else {
            throw new RuntimeException("Illegal type");
        }
    }

    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        HttpServletRequest request = handlerChain.getRequest();
        HttpServletResponse response = handlerChain.getResponse();
        String path = mv.getView();
        Map<String, Object> model = mv.getModel();
        model.forEach(request::setAttribute);
        request.getRequestDispatcher(MainApplication.getConfiguration().getViewPath()+path).forward(request, response);
    }
}
