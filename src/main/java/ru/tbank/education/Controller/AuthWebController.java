package ru.tbank.education.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.tbank.education.Service.AuthService;
import ru.tbank.education.DTO.AuthResponse;
import ru.tbank.education.DTO.LoginRequest;
import ru.tbank.education.DTO.RegisterRequest;


@Controller
public class AuthWebController {

    @GetMapping("/login")
    public String loginPage(Model model) {

        return "login";
    }
    @GetMapping("/registration")
    public String registrationPage(Model model) {

        return "registration";
    }
    @GetMapping("/app")
    public String appPage(Model model) {

        return "app";
    }
    @GetMapping("/trips/{tripId}")
    public String tripPage(@PathVariable Long tripId) {
        return "trip";
    }
    @GetMapping("/trips/{tripId}/memories/{memoryId}")
    public String memoryPage(@PathVariable Long tripId,
                             @PathVariable Long memoryId) {
        return "memory";
    }
    @GetMapping("/admintags")
    public String tagsPage() {
        return "admintags";

    }
}