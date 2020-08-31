package com.nullatom.httpserver.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;


/**
 * 相应HTTP请求类
 * @author VioletTec
 * */
public class Response {

    private BufferedWriter bw;

    //正文
    private StringBuilder content;

    //协议头（状态行与请求头 回车）信息
    private StringBuilder headInfo;

    private int len; //正文的字节数

    private final static String BLANK =" ";//空格

    private final static String CRLF = "\r\n";//回车


    private String responseType = "text/html";//默认返回内容为HTML

    /**
     * 无参构造器，初始化变量
     * */
    private Response() {
        content =new StringBuilder();
        headInfo=new StringBuilder();
        len =0;
    }

    /**
     * 有参数的构造器，根据传入的客户端Socket初始化BufferedWriter
     * @param client 客户端的Socket链接
     * */
    public Response(Socket client) {
        this();//调用无参构造器初始化变量
        try {
            bw=new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8"));//设置UTF8编码
        } catch (IOException e) {
            e.printStackTrace();
            headInfo = null;
        }
    }


    /**
     * 有参数的构造器，根据传入的OutputStream流初始化BufferedWriter
     * @param os 客户端的OutputStream流
     * */
    public Response(OutputStream os) {
        this();//调用无参构造器初始化变量
        bw=new BufferedWriter(new OutputStreamWriter(os));
    }


    /**
     * 向HTTP链接中发送文字<strong>（不换行）</strong>
     *
     * @param info 需要发送的文字
     * */
    public	Response print(String info) {
        content.append(info);
        len+=info.getBytes().length;
        return this;
    }

    /**
     * 向HTTP链接中发送文字<strong>换行）</strong>
     *
     * @param info 需要发送的文字
     * */
    public	Response println(String info) {
        content.append(info).append(CRLF);
        len+=(info+CRLF).getBytes().length;
        return this;
    }

    //推送响应信息
    public void pushToBrowser(int code) throws IOException {
        if(null ==headInfo) {
            code = 500;
        }
        createHeadInfo(code);
        bw.append(headInfo);
        bw.append(content);
        bw.flush();
    }

    /**
     * 构建头部信息，头部信息保存在全局变量 headInfo 中
     *
     * @param code 服务器响应状态码
     * */
    private void createHeadInfo(int code) {
        //1、响应行: HTTP/1.1 200 OK
        headInfo.append("HTTP/1.1").append(BLANK);
        headInfo.append(code).append(BLANK);
        switch(code) {
            case 200:
                headInfo.append("OK").append(CRLF);
                break;
            case 404:
                headInfo.append("NOT FOUND").append(CRLF);
                break;
            case 505:
                headInfo.append("SERVER ERROR").append(CRLF);
                break;
        }
        //2、响应头(最后一行存在空行):
        headInfo.append("Date:").append(new Date()).append(CRLF);
        headInfo.append("Server:").append("NullAtomHttpServer").append(CRLF);
        headInfo.append("Content-type:"+responseType+";charset=utf-8").append(CRLF);
        headInfo.append("Content-length:").append(len).append(CRLF);
        headInfo.append(CRLF);
    }

    /**
     * 设置服务器返回给客户端的返回内容类型，默认为 text/html，所有responseType后自动添加一个;charset=utf-8
     * <p>如：setResponseType("text/json");</p>
     * @param responseType 需要设置的返回类型
     * */
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

}
