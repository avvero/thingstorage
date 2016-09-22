package com.avvero.thingstorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by avvero on 31.08.2016.
 */
@Controller
public class PageController {
    @RequestMapping(value = {
            "/"
    }, method = RequestMethod.GET)
    public String someOtherPage(HttpServletRequest request, HttpServletResponse response) {
        return "index";
    }
}
