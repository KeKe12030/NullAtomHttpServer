package com.nullatom.httpserver;

import com.nullatom.httpserver.handler.NAServerHandler;
import com.nullatom.httpserver.utils.Dispatcher;
import com.nullatom.httpserver.utils.Response;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * NAHTTP服务器对象
 *
 * @author VioletTec
 *
 */
public class NAServer implements Runnable{
    private ServerSocket serverSocket ;//TCP的ServerSocket
    private boolean isRunning;//循环标志
    private int port = 50000;//NAServer端口默认50000
    private NAServerHandler handler = null;//handler类

    public NAServer(int port){
        this.port = port;
    }


    @Override
    public void run() {

        this.start();//开始


    }

    //启动服务
    public void start() {
        if(this.handler==null){
            throw new RuntimeException("请添加NAServerHandler");
        }
        try {
            serverSocket =  new ServerSocket(50000);
            isRunning = true;
            receive();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器启动失败....");
            stop();
        }
    }


    /**
     * 接受链接，并且丢给handler进行处理链接
     * */
    public void receive() {
        while(isRunning) {
            Socket client = null;
            try {
                client = serverSocket.accept();//接受客户端请求
                //多线程处理
                new Thread(new Dispatcher(handler,client)).start();//丢给Dispatcher进行处理
            } catch (IOException e) {
                if(isRunning) {
                    e.printStackTrace();
                    System.out.println("客户端链接错误");
                }
            }
        }
    }


    /**
     * 停止服务器
     * */
    public void stop() {
        isRunning = false;//标志位设为0
        try {
            serverSocket.close();
            System.out.println("服务器已停止");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*各种GETTER和SETTER*/

    /**
     * 设置NAServer的handler对象
     * @param handler 处理类，禁止为null
     * */
    @NotNull
    public void setHandler(NAServerHandler handler){
        this.handler = handler;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStatus() {
         return isRunning ? "正在运行": "已停止";
    }
}
