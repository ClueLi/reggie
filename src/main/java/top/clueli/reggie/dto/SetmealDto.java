package top.clueli.reggie.dto;

import lombok.Data;
import top.clueli.reggie.entity.Setmeal;
import top.clueli.reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
