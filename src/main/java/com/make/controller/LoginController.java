package com.make.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.make.entity.User;
import com.make.service.UserService;
import com.make.vo.DataView;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 生成验证码
     * @param response
     * @param session
     * @throws IOException
     */
    @RequestMapping("/login/getCode")
    public void getCode(HttpServletResponse response, HttpSession session) throws IOException {
        // 验证码对象
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(116, 36, 4, 10);
        // 放入session
        session.setAttribute("code", captcha.getCode());
        // 输出
        ServletOutputStream outputStream = response.getOutputStream();
        captcha.write(outputStream);
        // 关闭输出流
        outputStream.close();
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param code
     * @param session
     * @return
     */
    @RequestMapping("/login/login")
    @ResponseBody
    public DataView login(String username, String password, String code, HttpSession session){
        DataView dataView = new DataView();
        String sessionCode = session.getAttribute("code").toString();
        if (code!= null && sessionCode.equals(code)){
            // 登录逻辑
            // User user = userService.login(username,password);
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            subject.login(token);
            User user = (User) subject.getPrincipal();

            // 判断
            if (user != null) {
                dataView.setCode(200);
                dataView.setMsg("登录成功");
                session.setAttribute("user", user);
                return dataView;
            }else {
                dataView.setCode(100);
                dataView.setMsg("用户名或密码错误，登录失败");
                return dataView;
            }
        }
        dataView.setCode(100);
        dataView.setMsg("验证码错误");
        return dataView;
    }

    /**
     * 退出登录
     * @return
     */
    @RequestMapping("/login/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "login";
    }
}
