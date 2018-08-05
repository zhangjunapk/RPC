package service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangJun on 2018/8/5.
 */
public class DemoService implements IService {

    final String packageName = "org.zj.demo.service";

    Map<Class, Object> container = new HashMap<Class, Object>();

    //扫描所有需要扫描的包，并将里面的类放到容器里
    public IService registe() {
        File file=new File(getPackagePath(packageName));
        try {
            inflateContainer(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    //往容器里面填入键值对
    private void inflateContainer(File file) throws Exception {
        inflate(container,file);
    }

    private void inflate(Map<Class,Object> container,File file) throws Exception {
        if(file!=null&&file.isFile()){
            if(file.getName().endsWith(".java")){
                Class<?> aClass = Class.forName(getClassName(file.getAbsolutePath()));

                if(aClass.isInterface())
                    return;

                Object o = aClass.newInstance();
                container.put(Class.forName(getClassName(file.getAbsolutePath())), o);
                        //还要将接口和实现类的映射关系放到map
                for(Class c:aClass.getInterfaces()){
                    container.put(Class.forName(c.getName()),o);
                }
            }
        }

        //如果是文件夹，递归调用
        if(file!=null&&file.isDirectory()){
            for(File f:file.listFiles()){
                inflate(container,f);
            }
        }

    }

    //开启服务，接受客户端的服务调用
    public void start(int port) {
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("服务端已经开启了");
            while(true){
                Socket accept = serverSocket.accept();
                InputStream is = accept.getInputStream();

                OutputStream outputStream = accept.getOutputStream();

                ObjectInputStream objectInputStream=new ObjectInputStream(is);

                ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);

                System.out.println("收到客户端的服务调用请求");
                Object o = objectInputStream.readObject();
                System.out.println(o);

                //给客户端写入
                objectOutputStream.writeObject(container.get(o));

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //通过java文件的绝对路径来获得类全名
    public static String getClassName(String path){
        path=path.replace(getSrcPath(),"");
        String replace = path.replace("\\", ".");
        return replace.substring(0,replace.length()-5);
    }

    public static void main(String[] args) {
        new DemoService().registe().start(9999);
    }

    //返回指定包的路径
    public static String getPackagePath(String packageName) {
        return getSrcPath() + packageName.replace(".", "/");
    }

    //获得源码路径
    public static String getSrcPath(){
        return getPath()+"\\src\\main\\java\\";
    }

    //返回用户路径
    public static String getPath() {
        return System.getProperty("user.dir");
    }

}
