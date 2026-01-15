package com.douradelivery;

import com.douradelivery.after.exception.BusinessException;
import com.douradelivery.after.model.user.dto.UserUpdatePasswordRequestDTO;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testng.annotations.Test;

import static org.testng.Assert.assertThrows;

@RestController
@RequiredArgsConstructor
public class TestController {

    UserService userService;

    @GetMapping("/")
    public String home() {
        return "API online.";
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }

}
