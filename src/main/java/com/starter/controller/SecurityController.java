package com.starter.controller;

import com.starter.annotation.AccessLimited;
import com.common.util.LogUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/")
public class SecurityController {
    @AccessLimited(count = 1)
    @GetMapping(value = "/login")
    public Object login(@RequestParam(required = false) String username, @RequestParam(required = false) String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            LogUtil.info("Wrong user name or password");
            throw e;
        } catch (AuthorizationException e) {
            LogUtil.info("no permission");
            throw e;
        }

        return new HashMap<String, Object>() {{
            put("username", username);
            put("msg", "success");
        }};
    }
}
