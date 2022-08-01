package com.make.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApiController {

    @RequestMapping("/toChinaManager")
    public String toChinaMannge() {
        return "admin/chinadatamanger";
    }
}
