package com.nullatom.httpserver.utils;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请求HTTP类
 *
 * @author VioletTec
 *
 */
public class Request {
    //协议信息
    private String requestInfo;

    //请求方式
    private String method;

    //请求url
    private String url;

    //请求参数
    private String queryStr;

    //存储参数
    private Map<String,List<String>> parameterMap;

    //换行
    private static final String CRLF = "\r\n";

    //状态
    private boolean isOK = true;//链接是否正常

    private final String pattern = "content-length:\\s(\\d+)";
    private final Pattern r = Pattern.compile(pattern);
    private Matcher m = null;


    /**
     * 根据传入的InputStream读取HTTP信息，并且分解字符串
     * @param is 客户端的InputStream流
     * */
    public Request(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        parameterMap = new HashMap<String,List<String>>();
        byte[] bytes = new byte[1024];
        int len = 0;
        StringBuilder requestInfoSb = new StringBuilder();
        try {
            String[] infos = null;//存放数据大小的数组，[1]是数据大小，记得trim一下再转换为Integer
            while(true){
                String info = br.readLine();
                requestInfoSb.append(info+CRLF);
                if (this.method==null && info.contains("/")) {
                    this.method = info.substring(0, info.indexOf("/")).toLowerCase().trim();//获取请求方法
                }
                String group = "";
                if(this.method.equals("post")) {//如果是POST方式，则\r\n\r\n有参数
                    if (info.toLowerCase().contains("content-length:")) {
                        m = r.matcher(info.toLowerCase());
                        if (m.matches()) {
                            group = m.group();
                            infos = group.split(":");
                        } else {
                            continue;
                        }
                    }
                    //如果sb的大小等于Content-length的大小，则结束循环
                    if (infos != null && Integer.valueOf(infos[1].trim()) == requestInfoSb.substring(requestInfoSb.indexOf("\r\n\r\n") + 2, requestInfoSb.length()).toString().getBytes().length) {
                        break;
                    }
                }else{//如果是GET或者其他方式，则是以\r\n\r\n结尾的
                    if(requestInfoSb.toString().contains("\r\n\r\n")){

                        break;
                    }
                }

            }
            this.requestInfo = requestInfoSb.toString();
        } catch (Exception e) {
            if(e.getClass()==NullPointerException.class){
                System.out.println("使用浏览器访问，链接异常！");
                return;
            }
            e.printStackTrace();
            return;
        }
        //分解字符串
        parseRequestInfo();
    }


    /**
     * 根据传入的客户端Socket获取InputStream传给另一个接受InputStream的构造器，分解字符串。
     * @param client 客户端的Socket链接
     * */
    public Request(Socket client) throws IOException {
        this(client.getInputStream());
    }


    /**
     * 根据构造器中接受的requestInfo进行分解字符串，并且存入全局变量中
     * */
    private void parseRequestInfo() {
        try {
            //------分解-------
            //---1、获取请求方式: 开头到第一个/------
            if (requestInfo.contains("/"))
                this.method = this.requestInfo.substring(0, this.requestInfo.indexOf("/")).toLowerCase().trim();//获取请求方法

            //---2、获取请求url: 第一个/ 到 HTTP/------
            //---可能包含请求参数? 前面的为url------
            //1)、获取/的位置
            int startIdx = this.requestInfo.indexOf("/") + 1;
            //2)、获取 HTTP/的位置
            int endIdx = this.requestInfo.indexOf("HTTP/");
            //3)、分割字符串
            this.url = this.requestInfo.substring(startIdx, endIdx).trim();//获取HTTP请求的URL地址
            //4)、获取？的位置
            int queryIdx = this.url.indexOf("?");
            if (queryIdx >= 0) {//表示存在请求参数
                String[] urlArray = this.url.split("\\?");
                this.url = urlArray[0];//获取HTTP请求的URL地址
                queryStr = urlArray[1];//存储HTTP请求的参数
            }

            //---3、获取请求参数:如果Get已经获取,如果是post可能在请求体中------

            if (method.equals("post")) {//如果是POST方式的HTTP请求，则URL中肯定有请求参数
                String qStr = this.requestInfo.substring(this.requestInfo.lastIndexOf(CRLF)).trim();
                if (null == queryStr) {
                    queryStr = qStr;
                } else {
                    queryStr += "&" + qStr;
                }
            }
            queryStr = null == queryStr ? "" : queryStr;//如果queryStr为NULL则存储为""不是则不变
            //转成Map fav=1&fav=2&uname=shsxt&age=18&others=
            convertMap();//把请求参数从queryStr中转换为map对象，存储在全局变量中的parameterMap中
        }catch (Exception e){
            System.out.println(e);
            System.out.println("有链接传输中断");
            isOK = false;
        }
    }


    /**
     * 处理本地变量queryStr存储的请求参数，转换为MAP对象存储在全局变量中的parameterMap中
     * */
    private void convertMap() {
        //1、分割字符串 &
        String[] keyValues =this.queryStr.split("&");
        for(String queryStr:keyValues) {
            //2、再次分割字符串  =
            String[] kv = queryStr.split("=");
            kv =Arrays.copyOf(kv, 2);
            //获取key和value
            String key = kv[0];
            String value = kv[1]==null?null:decode( kv[1],"utf-8");//使用UTF8方式解码浏览器的编码为中文
            //存储到map中
            if(!parameterMap.containsKey(key)) { //第一次
                parameterMap.put(key, new ArrayList<String>());
            }
            parameterMap.get(key).add(value);
        }
    }


    /**
     * 如果请求方式为POST请求，则发送过来的中文是经过浏览器编码，需要处理转换成为中文
     *
     * @param value 需要解码的字符串
     * @param enc 需要解码的解码方式
     * @return 从浏览器编码转换好的中文
     */
    private String decode(String value,String enc) {
        try {
            return java.net.URLDecoder.decode(value, enc);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 从全局变量parameterMap中获取参数对应的多个值
     * @param key 需要获取的参数key
     * @return 获取到的参数对应值的字符串数组
     */
    public String[] getParameterValues(String key) {
        List<String> values = this.parameterMap.get(key);
        if(null==values || values.size()<1) {
            return null;
        }
        return values.toArray(new String[0]);
    }



    /**
     * 调用getParameterValues，并且返回其中第一个参数
     *
     * @param key 需要获取的参数的key
     * @return 获取到的参数字符串
     */
    public String getParameter(String key) {
        String []  values = getParameterValues(key);
        return values == null ? null : values[0];
    }



    /*  GET和SET方法  */
    public String getMethod() {
        return method;
    }
    public String getUrl() {
        return url;
    }
    public String getQueryStr() {
        return queryStr;
    }


    public boolean getStatus() {
        return this.isOK;
    }
}
