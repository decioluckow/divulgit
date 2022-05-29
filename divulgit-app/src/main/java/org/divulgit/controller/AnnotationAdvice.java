package org.divulgit.controller;

import org.divulgit.model.User;
import org.divulgit.security.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = Controller.class)
public class AnnotationAdvice {

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return userDetails.getUser();
    }
}