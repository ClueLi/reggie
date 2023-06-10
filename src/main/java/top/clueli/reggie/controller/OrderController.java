package top.clueli.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import top.clueli.reggie.common.BaseContext;
import top.clueli.reggie.common.R;
import top.clueli.reggie.dto.OrdersDto;
import top.clueli.reggie.entity.AddressBook;
import top.clueli.reggie.entity.OrderDetail;
import top.clueli.reggie.entity.Orders;
import top.clueli.reggie.entity.User;
import top.clueli.reggie.service.AddressBookService;
import top.clueli.reggie.service.OrderDetailService;
import top.clueli.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.clueli.reggie.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
@Api(tags = "订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(Orders::getCheckoutTime);

        orderService.page(ordersPage, queryWrapper);
        List<Orders> records = ordersPage.getRecords();

        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item, ordersDto);

            // 设置dto中的ordersDetail
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> list = orderDetailService.list(queryWrapper1);
            ordersDto.setOrderDetails(list);

            // 设置账号名称
            Long userId = item.getUserId();
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getId, userId);
            User user = userService.getOne(userLambdaQueryWrapper);
            ordersDto.setUserName(user.getName());

            // 设置派送地址、派送电话、被派送的用户名称
            Long addressBookId = item.getAddressBookId();
            LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
            addressBookLambdaQueryWrapper.eq(AddressBook::getId, addressBookId);
            AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);
            ordersDto.setConsignee(addressBook.getConsignee());
            ordersDto.setPhone(addressBook.getPhone());
            ordersDto.setAddress(addressBook.getDetail());

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }
}