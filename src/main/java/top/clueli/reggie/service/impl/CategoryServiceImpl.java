package top.clueli.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.clueli.reggie.common.CustomException;
import top.clueli.reggie.entity.Category;
import top.clueli.reggie.entity.Dish;
import top.clueli.reggie.entity.Setmeal;
import top.clueli.reggie.mapper.CategoryMapper;
import top.clueli.reggie.service.CategoryService;
import top.clueli.reggie.service.DishService;
import top.clueli.reggie.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /*
    * 根据id删除分类，删除之前进行判断
    * */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            //当前分类关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类关联了菜品,不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0) {
            //当前分类关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类关联了套餐,不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
