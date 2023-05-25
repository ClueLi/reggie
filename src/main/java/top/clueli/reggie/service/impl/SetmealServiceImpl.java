package top.clueli.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.clueli.reggie.dto.SetmealDto;
import top.clueli.reggie.entity.Category;
import top.clueli.reggie.entity.Setmeal;
import top.clueli.reggie.entity.SetmealDish;
import top.clueli.reggie.mapper.SetmealMapper;
import top.clueli.reggie.service.CategoryService;
import top.clueli.reggie.service.SetmealDishService;
import top.clueli.reggie.service.SetmealService;
import top.clueli.reggie.utils.ImageUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //设置setmeal的数据
        Setmeal setmeal = this.getById(id);

        //拷贝setmeal数据到setmealDto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //设置关联的setmealDish数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        //设置分类名称
        Long categoryId = setmealDto.getCategoryId();
        String name = categoryService.getById(categoryId).getName();
        setmealDto.setCategoryName(name);

        return setmealDto;
    }

    @Override
    public void removeWithDish(Long id) {
        Setmeal byId = this.getById(id);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(queryWrapper);
        this.removeById(id);

        ImageUtil.delete(byId.getImage());
    }
}
