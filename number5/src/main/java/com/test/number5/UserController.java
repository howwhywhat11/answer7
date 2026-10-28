package com.test.number5;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    //实现 register 接口
    @PostMapping("/register")
    public Map<String,Object> register (@Valid @RequestBody UserRequest body){
        userService.register(body.getUsername(), body.getPassword());
        Map<String,Object> res= new HashMap<>();
        res.put("message","注册成功");
        return res;

    }

    //实现 login 接口
    @PostMapping("/login")
    public Map<String,Object> login (@RequestBody(required = false) UserRequest body,
                                     HttpServletRequest request){
        String token = userService.login(request.getHeader("Authorization"),body.getUsername(), body.getPassword());
        Map<String,Object> res= new HashMap<>();
        res.put("message","success");
        res.put("token",token);
        return res;

    }

    @PostMapping("/refresh-token")
    public Map<String,Object> refreshToken (HttpServletRequest request){
        String username = (String) request.getAttribute("currentUser");
        String role = (String) request.getAttribute("currentRole");
        String token = userService.refreshToken(username,role);
        Map<String,Object> res = new HashMap<>();
        res.put("message","success");
        res.put("newToken",token);
        return res;
    }

    //分页查询，仅管理员可用
    @GetMapping("/users")
    public Map<String,Object> findUsers(@RequestParam(defaultValue = "1")int page,
                                        @RequestParam(defaultValue = "10")int size,
                                        HttpServletRequest request){
        if (!((String) request.getAttribute("currentRole")).equals("ADMIN")){
            throw new RuntimeException("权限不足，仅管理员可查询");
        }


        List<User> data  =  userService.findByPage(page,size);
        Map<String,Object> res = new HashMap<>();
        res.put("code",200);
        res.put("message","查询成功");
        res.put("data",data);
        return res;
    }

    //查自己信息，所有人都可使用
    @GetMapping("/user/info")
    public Map<String,Object> findMyInfo(HttpServletRequest request){
        String username = (String) request.getAttribute("currentUser");
        User user = userService.findByUsername(username);
        if (user == null){
            throw new RuntimeException("用户不存在");
        }
        //脱敏处理，不显示密码
        user.setPassword("******");
        Map<String,Object> res = new HashMap<>();
        res.put("code",200);
        res.put("message","查询成功");
        res.put("data",user);
        return res;
    }

    //按id查询，仅管理员可用
    @GetMapping("/user/{id}")
    public Map<String,Object> findById(@PathVariable Long id,HttpServletRequest request){
        String currentRole = (String) request.getAttribute("currentRole");

        if (!"ADMIN".equals(currentRole)){
            throw new RuntimeException("权限不足，仅管理员可查询");
        }
        User user = userService.findById(id);
        Map<String,Object> res = new HashMap<>();
        res.put("code",200);
        res.put("message","查询成功");
        res.put("data",user);
        return res;

    }
}
