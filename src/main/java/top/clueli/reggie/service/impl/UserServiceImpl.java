package top.clueli.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.clueli.reggie.entity.User;
import top.clueli.reggie.mapper.UserMapper;
import top.clueli.reggie.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
