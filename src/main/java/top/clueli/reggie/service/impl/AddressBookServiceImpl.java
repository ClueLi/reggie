package top.clueli.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.clueli.reggie.entity.AddressBook;
import top.clueli.reggie.mapper.AddressBookMapper;
import top.clueli.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
