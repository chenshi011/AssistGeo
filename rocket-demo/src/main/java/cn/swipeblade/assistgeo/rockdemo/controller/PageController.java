package cn.swipeblade.assistgeo.rockdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by GOT.hodor on 2017/12/20.
 */

@Controller
@RequestMapping(value = {"/rest/page", "/web/page"})
public class PageController {

    @RequestMapping(value = "/index")
    public ModelAndView websocket() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("wsContext", "ws://127.0.0.1:8080/bladeOne/sk");
        mv.setViewName("index");
        return mv;
    }


}
