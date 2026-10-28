package com.test.number5;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${user.usernamePattern}")
    String usernamePattern;
    @Value("${user.passwordMinLength}")
    int passwordMinLength;
    @Value("${user.encrypt-algorithm:BCRYPT}")
    //默认选择BCRYPT加密，若要修改，到yaml文件修改
    private String encryptAlgorithm;

    //用于BCRYPT加密密码
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //密码加密方法
    public String encryptPassword(String rawPassword) {
        //如果选择了MD5加密
        if ("MD5".equalsIgnoreCase(encryptAlgorithm)) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] array = md.digest(rawPassword.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : array) {
                    sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
                }
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException("MD5加密失败");
            }
        }else {
            //默认选择BCRYPT加密
            return passwordEncoder.encode(rawPassword);
        }

    }

    //密码对照方法
    public boolean verifyPassword(String password,String realPassword){
        //如果选择了MD5加密
        if ("MD5".equalsIgnoreCase(encryptAlgorithm)){
            //将输入的密码再加密后与存储的密码对照
            String encodedpassword = encryptPassword(password);
            return encodedpassword.equals(realPassword);
        }else {
            //默认选择BCRYPT加密，直接用BCRYPT对照
            return passwordEncoder.matches(password,realPassword);
        }

    }

    public void register (String username,String password){
        //判断用户名重复
        User existingUsers = userMapper.findByUsername(username);
        if (existingUsers != null){
            throw new RuntimeException("用户名已存在");
        }
        //判断密码长度是否合法
        if(password.length() < passwordMinLength){
            throw new RuntimeException("密码长度至少为"+passwordMinLength+ "位");
        }
        //判断用户名是否合法
        if (!username.matches(usernamePattern)){
            throw new RuntimeException("用户名不合法");
        }
        //加密密码
        String EncodedPassword = encryptPassword(password);
        //添加用户名和密码
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(EncodedPassword);
        userMapper.insert(newUser);
    }

    public String login (String token,String username,String password){
        if (token != null && !token.isBlank()){
            try {
                Claims claims = jwtUtil.parseToken(token);
                String tokenName = claims.getSubject();
                String role = (String) claims.get("role");
                return jwtUtil.generateToken(tokenName,role);
            } catch (Exception e) {
                System.out.println("token登录失败，转为密码登录");
            }
        }

        User existingUser = userMapper.findByUsername(username);
        //用户名不存在时，为了安全仍然提示"用户名或密码错误"
        if (existingUser == null){
            throw new RuntimeException("用户名或密码错误");
        }
        String RealPassword = existingUser.getPassword();
        //密码错误时，提示"用户名或密码错误"
        if (!verifyPassword(password,RealPassword)){
            throw new RuntimeException("用户名或密码错误");
        }

        //获取用户的身份
        String role = existingUser.getRole();
        //返回token
        return jwtUtil.generateToken(username,role);
    }

    public List<User> findByPage (int page, int size){
        int offset = (page-1)*size;
        return userMapper.findByPage(offset,size);
    }

    public User findByUsername(String username){
        return userMapper.findByUsername(username);
    }

    public User findById(Long id){
        return userMapper.findById(id);
    }

    public String refreshToken (String username,String role){
        return jwtUtil.generateToken(username,role);
    }


}
