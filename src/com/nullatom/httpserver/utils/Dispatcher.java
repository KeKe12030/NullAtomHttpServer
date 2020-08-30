package com.nullatom.httpserver.utils;

import com.nullatom.httpserver.handler.NAServerHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
/**
 * 分发器，处理HTTP请求
 *
 * @author VioletTec
 *
 */
public class Dispatcher implements Runnable {
    private Socket client;
    private Request request;
    private Response response ;
    private NAServerHandler handler = null;//handler类

    /**
     * 通过客户端Socket，初始化request和response对象，初始化Dispatcher对象。
     * @param client 客户端的Socket链接对象
     * */
    public Dispatcher(NAServerHandler handler, Socket client) {
        this.client = client;
        this.handler = handler;
        try {
            //获取请求协议
            //获取响应协议
            request =new Request(client);
            response =new Response(client);
        } catch (IOException e) {
            e.printStackTrace();
            this.release();
        }
    }


    @Override
    public void run() {

        /*这里写业务逻辑代码*/

        handler.handle(request,response);//丢给handler处理

        release();///释放资源
    }


    //释放资源
    private void release() {
        try {
            client.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
