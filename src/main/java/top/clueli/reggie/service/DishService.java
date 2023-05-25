package top.clueli.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.clueli.reggie.dto.DishDto;
import top.clueli.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同事插入菜品对应的口味数据，需要操作两张表：dish、dosh_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByidWithFlavor(Long id);

    //跟新菜品表和口味表
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品以及对应的口味表
    public void removeWithFlavor(Long id);
}
