package top.clueli.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.clueli.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}