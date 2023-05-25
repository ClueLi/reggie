package top.clueli.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import top.clueli.reggie.dto.SetmealDto;
import top.clueli.reggie.entity.Setmeal;

import java.util.List;

@Transactional
public interface SetmealService extends IService<Setmeal> {

    //保存并且保存setmealdish表
    @Transactional
    public void saveWithDish(SetmealDto setmealDto);

    //根据setmeal的id查找，并带有setmealDish数据
    public SetmealDto getByIdWithDish(Long id);

    //根据id删除并删除setmealDish的数据

    public void removeWithDish(Long id);
}
