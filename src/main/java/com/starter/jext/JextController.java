package com.starter.jext;

import com.starter.annotation.AccessLimited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jext")
public class JextController {
    @Autowired
    JextService jextService;

    @AccessLimited(count = 1)
    @GetMapping(value = "")
    public Object info() {
        return jextService.getInfo();
    }
}
