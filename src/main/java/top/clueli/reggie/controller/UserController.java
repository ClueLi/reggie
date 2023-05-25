package top.clueli.reggie.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.clueli.reggie.common.R;
import top.clueli.reggie.dto.UserDto;
import top.clueli.reggie.entity.User;
import top.clueli.reggie.service.UserService;

import javax.servlet.http.HttpSession;



@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取前端发送的手机号码和验证码
     * @param userDto
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody UserDto userDto, HttpSession session){
        log.info(userDto.getPhone() + "&&" + userDto.getCode());
        //获取手机号
        String phone = userDto.getPhone();

        if(!StringUtils.isEmpty(phone)) {
            //前端生成的验证码,直接进行保存即可
            session.setAttribute(phone, userDto.getCode());
            return R.success("成功");
        }

        return R.error("失败");
    }

    /**
     * 验证登录判断
     * @param userDto
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody UserDto userDto, HttpSession session) {
        log.info(userDto.getPhone() + "&&" + userDto.getCode());
        String codeInSession =(String) session.getAttribute(userDto.getPhone());
        String code = userDto.getCode();
        String phone = userDto.getPhone();

        if (!StringUtils.isEmpty(codeInSession) && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(!StringUtils.isEmpty(userDto.getPhone()),User::getPhone, userDto.getPhone());
            User user = userService.getOne(queryWrapper);
            if(user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success("登录成功");
        }

        return R.error("登录失败");
    }

}
