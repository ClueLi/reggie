package top.clueli.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.clueli.reggie.entity.Category;


public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
