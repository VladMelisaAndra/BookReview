package com.univbuc.bookreview.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String errorMessage = "Unknown error";
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if(statusCode == 404) {
                errorMessage = "The requested page was not found.";
            } else if (statusCode == 500) {
                errorMessage = "The server encountered an unexpected condition.";
            }
        }

        model.addAttribute("error", "Status " + status + ": " + errorMessage);
        model.addAttribute("message", "Please try again later or contact support if the problem persists.");
        return "error";
    }

    public String getErrorPath() {
        return "/error";
    }
}
