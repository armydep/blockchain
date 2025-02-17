package am.com.blockchain.wallet.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Validated
@Controller
public class LoginWebController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}