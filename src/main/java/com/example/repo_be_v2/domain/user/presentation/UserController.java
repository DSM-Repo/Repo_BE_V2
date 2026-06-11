package com.example.repo_be_v2.domain.user.presentation;

import com.example.repo_be_v2.domain.user.service.UserLoginService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserLoginService userLoginService;

}
