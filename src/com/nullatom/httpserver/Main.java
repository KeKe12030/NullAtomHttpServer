package com.nullatom.httpserver;

import com.nullatom.httpserver.handler.NAServerHandler;
import com.nullatom.httpserver.utils.Request;
import com.nullatom.httpserver.utils.Response;

import java.util.Scanner;


/**
 * 演示类，用于测试NAServer
 * */
public class Main {

    public static void main(String[] args) {

        NAServer naServer = new NAServer(50000);
        naServer.setHandler(new NAServerHandler() {
            @Override
            public void handle(Request request, Response response) {
                System.out.println("客户端请求方式："+request.getMethod());
                System.out.println("客户端请求路径："+request.getUrl());
                System.out.println("客户端请求参数："+request.getQueryStr());
                System.out.println("链接状态："+request.getStatus());
                response.println("<p>test ok</p>");
                try {
                    response.pushToBrowser(200);
                }catch (Exception e){
                    System.out.println(e);
                    System.out.println("链接中断");
                }
            }
        });

        new Thread(()->{
            naServer.start();
        }).start();//开启线程运行NA服务器

        Scanner s = new Scanner(System.in);
        while(true) {
            System.out.print(">: ");
            String cmd = s.next();
            switch (cmd){
                case "help":
                    System.out.println("===================");
                    System.out.println("help : 查看帮助");
                    System.out.println("stop : 停止服务器");
                    System.out.println("info : 查看服务器信息");
                    System.out.println("===================");
                    break;
                case "stop":
                    naServer.stop();
                    System.exit(0);
                    break;
                case "info":
                    System.out.println("服务器运行端口："+ naServer.getPort());
                    System.out.println("服务器运行状态："+ (naServer.getStatus()));
                    break;
                default:
                    System.out.println("未找到命令"+cmd+"  请检查命令或者输入help查看帮助");
            }
        }

    }

}
