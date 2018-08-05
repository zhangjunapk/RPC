package client;

import org.zj.demo.service.IStudent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ZhangJun on 2018/8/5.
 */
public class ServerInvoker<T> {
    //从远程服务获得对象
    public T getInvokeServer(Class c,String address,int port) throws IOException, ClassNotFoundException {
        Socket socket=new Socket(address,port);
        String name = c.getName();
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
        System.out.println("客户端向服务端发送请求调用服务");
        objectOutputStream.writeObject(c);

        ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());

        return (T) objectInputStream.readObject();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        IStudent invokeServer = new ServerInvoker<IStudent>().getInvokeServer(IStudent.class, "localhost", 9999);
        invokeServer.ml();
    }
}
