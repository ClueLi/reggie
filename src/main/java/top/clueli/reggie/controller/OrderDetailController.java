package top.clueli.reggie.controller;

import io.swagger.annotations.Api;
import top.clueli.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单明细
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
@Api(tags = "订单细节接口")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

}