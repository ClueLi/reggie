package top.clueli.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.clueli.reggie.common.CustomException;
import top.clueli.reggie.dto.DishDto;
import top.clueli.reggie.entity.Dish;
import top.clueli.reggie.entity.DishFlavor;
import top.clueli.reggie.entity.SetmealDish;
import top.clueli.reggie.mapper.DishMapper;
import top.clueli.reggie.service.DishFlavorService;
import top.clueli.reggie.service.DishService;
import top.clueli.reggie.service.SetmealDishService;
import top.clueli.reggie.utils.ImageUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;
    /*
    * 新增彩票，同事保存对应的口味数据
    * */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    //根据id查询菜品信息和对应的口味信息
    @Override
    public DishDto getByidWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dishInfo = this.getById(id);

        //从口味表查询对应的口味
        LambdaQueryWrapper<DishFlavor> queryWapper = new LambdaQueryWrapper<>();
        queryWapper.eq(dishInfo.getId() != 0,DishFlavor::getDishId, dishInfo.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWapper);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dishInfo, dishDto);
        dishDto.setFlavors(list);
        return dishDto;
    }

    //跟新菜品表和口味表
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //跟新dish表
        this.updateById(dishDto);

        //删除当前提交的信息-------dish_flavor表中的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味信息------dish_flavor表中的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void removeWithFlavor(Long id) {
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(SetmealDish::getDishId, id);
        int count = setmealDishService.count(queryWrapper1);
        if(count > 0) {
            throw new CustomException("当前菜品有套餐在使用，不能删除");
        }

        Dish byId = this.getById(id);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        dishFlavorService.remove(queryWrapper);
        this.removeById(id);

        ImageUtil.delete(byId.getImage());
    }
}
