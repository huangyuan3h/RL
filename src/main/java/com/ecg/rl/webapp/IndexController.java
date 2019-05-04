package com.ecg.rl.webapp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/easterEgg")
    public String index(){
        return "index";
    }
}
