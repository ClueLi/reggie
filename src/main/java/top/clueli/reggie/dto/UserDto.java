package top.clueli.reggie.dto;

import lombok.Data;
import top.clueli.reggie.entity.User;

@Data
public class UserDto extends User {
    //验证码
    private String code;
}
