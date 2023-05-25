package top.clueli.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.clueli.reggie.entity.OrderDetail;
import top.clueli.reggie.mapper.OrderDetailMapper;
import top.clueli.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}