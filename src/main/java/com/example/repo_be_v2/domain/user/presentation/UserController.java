package com.example.repo_be_v2.domain.user.presentation;

import com.example.repo_be_v2.domain.user.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserLoginService userLoginService;

}
