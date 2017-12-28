package cn.swipeblade.assistgeo.rockdemo.controller;

import cn.swipeblade.assistgeo.rockdemo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/rest/user", "/web/user"})
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/{code}")
    public Object findUserByCode(@PathVariable String code) {
        return userService.findUserByCode(code);
    }


}
