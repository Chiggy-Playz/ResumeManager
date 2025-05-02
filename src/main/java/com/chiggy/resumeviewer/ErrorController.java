package com.chiggy.resumeviewer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        if (response.getStatus() == 404) {
            return "forward:/404.html";
        }
        return "error"; // you can add a generic error page if needed
    }

    @GetMapping("/error-page")
    public String showErrorPage(@RequestParam(name = "msg", required = false, defaultValue = "Something went wrong!") String msg,
                                Model model) {
        model.addAttribute("errorMessage", msg);
        return "error";
    }

}