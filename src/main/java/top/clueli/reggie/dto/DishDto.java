package top.clueli.reggie.dto;

import lombok.Data;
import top.clueli.reggie.entity.Dish;
import top.clueli.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
