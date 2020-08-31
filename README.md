# NullAtomHttpServer

![](https://img.shields.io/badge/license-MIT-blue)  ![Open Source Love](https://camo.githubusercontent.com/d41b9884bd102b525c8fb9a8c3c8d3bbed2b67f0/68747470733a2f2f6261646765732e66726170736f66742e636f6d2f6f732f76312f6f70656e2d736f757263652e7376673f763d313033)

![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)  ![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)

> 基于JavaSE开发的一款轻量级处理HTTP请求的Web服务器
> 可以处理一些简单的请求，建议用作API接口服务器使用
> 根据 **尚学堂** 的Java教程里的半成品源码二次开发（懒得自己分割字符串了（滑稽）

---
### 使用方法：
+ 下载NullAtomHttpServer.jar之后，当作依赖导入你的项目。
+ `NAServer.java`为 NAHttpServer 的核心类 （从这里开始）
+ 创建一个实现了`com.nullatom.httpserver.handler.NAServerHandler`的`Handler`类

---

### 代码演示（具体演示在 demo/Main.java 中）

[![](https://img.shields.io/badge/Demo-Main.java-lightgrey)](https://github.com/KeKe12030/NullAtomHttpServer/blob/master/demo/Main.java)👈点击查看

```java
public static void main(){
    NAServer naServer = new NAServer(50000);//参数为NAServer的运行端口
    naServer.setHandler(new NAServerHandler(){//为NAServer设置Handler类
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
}
```

---

