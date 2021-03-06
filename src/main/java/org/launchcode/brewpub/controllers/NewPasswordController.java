package org.launchcode.brewpub.controllers;

import org.launchcode.brewpub.models.User;
import org.launchcode.brewpub.models.data.UserRepository;
import org.launchcode.brewpub.models.dto.NewPasswordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;


@Controller
public class NewPasswordController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/forgotPassword")
    public String viewForgotPasswordForm(Model model) {
        model.addAttribute("title","Forgot Password");
        model.addAttribute(new NewPasswordDTO());
        return "editAccount/forgotPassword";
    }

    @RequestMapping(value="/newPassword", method = { RequestMethod.GET, RequestMethod.POST })
    public String newPasswordForm(Model model, NewPasswordDTO newPasswordDTO, Principal principal) {
        model.addAttribute("title", "newPassword");
        User existingUser = userRepository.findByEmail(newPasswordDTO.getEmail());
        if (existingUser == null && principal == null) {
            return "redirect:";
        } else if (principal != null) {
            Optional<User> resultUser= Optional.ofNullable(userRepository.findByUsername(principal.getName()));
            if (resultUser.isPresent()) {
                User user = resultUser.get();
                newPasswordDTO.setEmail(user.getEmail());
                model.addAttribute("newPasswordDTO", newPasswordDTO);
            } else {
                return "redirect:";
            }

        } else if (existingUser != null) {
            model.addAttribute("newPasswordDTO", newPasswordDTO);
        }
        return "editAccount/newPassword";
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public String resetUserPassword(@ModelAttribute @Valid NewPasswordDTO newPasswordDTO, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "newPassword");
            model.addAttribute("errors", errors);
            model.addAttribute("newPasswordDTO", newPasswordDTO);
            return "editAccount/newPassword";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (newPasswordDTO.getEmail() != null && newPasswordDTO.getPassword().equals(newPasswordDTO.getVerifyPassword())) {
            User existingUser = userRepository.findByEmail(newPasswordDTO.getEmail());
            existingUser.setPwhash(encoder.encode(newPasswordDTO.getPassword()));
            userRepository.save(existingUser);
            model.addAttribute("message", "Password successfully reset. You can now log in with the new credentials.");
            return "login";
        } else {
            model.addAttribute("title","New Password");
            model.addAttribute("message","Passwords do not match.");
            return "editAccount/newPassword";
        }
    }
}