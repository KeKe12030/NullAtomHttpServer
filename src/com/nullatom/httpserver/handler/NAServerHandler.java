package com.nullatom.httpserver.handler;

import com.nullatom.httpserver.utils.Request;
import com.nullatom.httpserver.utils.Response;

import java.net.Socket;


/**
 * NA服务器操作类接口
 * @author VioletTec
 * */
public interface NAServerHandler {
    /**
     * 当有链接进入时，调用这个方法，会传入可以操作客户端返回的Response对象
     * @param request 可以获取服务端的请求信息
     * @param response 操作客户端链接的Response对象
     * */
    public void handle(Request request,Response response);//定义一个接口，
}
