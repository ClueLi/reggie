package top.clueli.reggie.common;

/*
* 基于ThreadLocal封装工具，用户保存和获取当前登录id
* */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
