package edu.rit.csh.pings.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticServeController {

    /**
     * to deal with React routes
     */
    @RequestMapping(value = {"/", "/{x:[\\w\\-]+}", "/{x:^(?!api$).*$}/*/{y:[\\w\\-]+}", "/error"})
    public String getIndex() {
        return "/index.html";
    }
}
