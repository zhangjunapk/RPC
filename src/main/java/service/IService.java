package service;

/**
 * Created by ZhangJun on 2018/8/5.
 */
public interface IService {
    //注册服务
    IService registe();
    //开启服务
    void start(int port);
}
