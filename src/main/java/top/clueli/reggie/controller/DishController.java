package top.clueli.reggie.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.clueli.reggie.common.R;
import top.clueli.reggie.dto.DishDto;
import top.clueli.reggie.entity.Category;
import top.clueli.reggie.entity.Dish;
import top.clueli.reggie.entity.DishFlavor;
import top.clueli.reggie.entity.Employee;
import top.clueli.reggie.service.CategoryService;
import top.clueli.reggie.service.DishFlavorService;
import top.clueli.reggie.service.DishService;
import top.clueli.reggie.utils.ImageUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
* 菜品管理
* */
@RestController
@RequestMapping("/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 获取所有菜品信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("获取所有菜品信息")
    public R<Page> page(int page, int pageSize, String name){
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getPrice).orderByAsc(Dish::getUpdateTime);

        //执行查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝,忽略records属性
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();

            //根据菜品分类id获取菜品的名称
            Category category = categoryService.getById(categoryId);

            if(category != null) {
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /*
    * 新增菜品
    * */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("dishDto:{}", dishDto);

        dishService.saveWithFlavor(dishDto);

        return R.success("保存成功");
    }

    /*
    * 根据id查询菜品信息和对应的口味信息
    * */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByidWithFlavor(id);
        return R.success(dishDto);
    }

    /*
    * 修改菜品
    * */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        Dish oldDish = dishService.getById(dishDto.getId());
        if (!oldDish.getImage().equals(dishDto.getImage())) {
            // 删除旧的照片
            ImageUtil.delete(oldDish.getImage());
        }
        dishService.updateWithFlavor(dishDto);

        // 清除指定的redis缓存key
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        stringRedisTemplate.delete(key);
        return R.success("更新成功");
    }

    /*
    * 根据id删除菜品
    * */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        for (Long id : ids) {
            dishService.removeWithFlavor(id);
        }
        // 清除全部的redis缓存key
        Set<String> keys = stringRedisTemplate.keys("dish_*");
        if (keys != null)  stringRedisTemplate.delete(keys);
        return R.success("删除成功");
    }

    /*
    * 设置菜品的售卖状态
    * */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids){

        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(Dish::getStatus, status);
        queryWrapper.in(Dish::getId, ids);
        dishService.update(queryWrapper);
        return R.success("修改成功");
    }

    /**
     * 获取指定菜品分类id的所有菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        // 先从redis中获取缓存
        String dishDtoListString = stringRedisTemplate.opsForValue().get(key);


        if (dishDtoListString != null) {
            dishDtoList = JSON.parseArray(dishDtoListString, DishDto.class);
            //如果存在直接返回
            return R.success(dishDtoList);
        }

        // 如果不存在，需要查询数据库，将查询的菜品数据缓存到redis
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(dish.getName() !=null && !dish.getName().equals(""), Dish::getName, dish.getName());
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId, dish.getCategoryId());
        //只显示起售状态的商品
        queryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishList = dishService.list(queryWrapper);

        dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            //根据菜品分类id获取菜品的名称
            Category category = categoryService.getById(categoryId);

            if(category != null) {
                dishDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            if(dishFlavors != null) {
                dishDto.setFlavors(dishFlavors);
            }

            return dishDto;
        }).collect(Collectors.toList());

        // 将查询到的数据缓存到redis
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(dishDtoList), 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


}
