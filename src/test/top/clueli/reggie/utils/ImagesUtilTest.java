package top.clueli.reggie.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImagesUtilTest {

    @Test
    public void deleteImage() {
        ImageUtil.delete("123456.jpg");
    }

}
