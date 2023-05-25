package top.clueli.reggie.utils;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.File;

@Component
public class ImageUtil {
    public static String basePath;

    @Value("${reggie.path}")
    public void setBasePath(String path) {
        ImageUtil.basePath = path;
    }

    public static void delete(String name){
        File file = new File(basePath + name);
        if (file != null) {
            file.delete();
        }
    }
}
