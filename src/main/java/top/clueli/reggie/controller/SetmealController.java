package top.clueli.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import top.clueli.reggie.common.R;
import top.clueli.reggie.dto.DishDto;
import top.clueli.reggie.dto.SetmealDto;
import top.clueli.reggie.entity.Category;
import top.clueli.reggie.entity.Dish;
import top.clueli.reggie.entity.Setmeal;
import top.clueli.reggie.entity.SetmealDish;
import top.clueli.reggie.service.CategoryService;
import top.clueli.reggie.service.DishService;
import top.clueli.reggie.service.SetmealDishService;
import top.clueli.reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    public R<Page> getLit(@RequestParam int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null&&!name.equals(""), Setmeal::getName, name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtoRecords = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoRecords);
        return R.success(setmealDtoPage);
    }

    //新增套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增成功");
    }

    //根据id获取套餐数据,服务后端
    @GetMapping("{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    //根据id获取套餐数据,服务前端
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> getByIdWithFront(@PathVariable Long id) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Dish::getId, item.getDishId());
            Dish one = dishService.getOne(lambdaQueryWrapper);

            BeanUtils.copyProperties(one, dishDto);
            //获取份数
            Integer copies = item.getCopies();
            dishDto.setCopies(copies);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

    //批量更改套餐状态
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus, status);
        updateWrapper.in(Setmeal::getId, ids);

        setmealService.update(updateWrapper);
        return R.success("修改成功");
    }

    /**
     * 根据套餐id删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestParam List<Long> ids){
        for (Long id : ids) {
            setmealService.removeWithDish(id);
        }
        return R.success("修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
