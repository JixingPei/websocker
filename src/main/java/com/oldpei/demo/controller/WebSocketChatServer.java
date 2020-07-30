package com.oldpei.demo.controller;


import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@ServerEndpoint("/chat")//标记此类为服务端
public class WebSocketChatServer {
    //定义一个全局变量集合sockets,用户存放每个登录用户的通信管道
    private static Set<WebSocketChatServer> sockets = new HashSet<WebSocketChatServer>();
    //定义一个全局变量Session,用于存放登录用户
    private Session session;

    /**
     *@OnOpen注解
     *注解下的方法会在连接建立时运行
     */
    @OnOpen
    public void open(Session session){
        System.out.println("建立了一个socket通道" + session.getId());
        this.session = session;
        //将当前连接上的用户session信息全部存到scokets中
        sockets.add(this);
    }

    /**
     *@OnMessage注解
     *注解下的方法会在前台传来消息时触发
     */
    @OnMessage
    public void getmes(Session session,String jsonmsg){

        broadcast(sockets,jsonmsg);

    }

    /**
     *@OnClose注解
     *注解下的方法会在连接关闭时运行
     */
    @OnClose
    public void close(Session session){
        //移除退出登录用户的通信管道
        sockets.remove(this);
        System.out.println(session.getId()+"退出了会话！");

    }


    /**
     *广播消息
     */
    public void broadcast(Set<WebSocketChatServer> sockets , String msg){
        //遍历当前所有的连接管道，将通知信息发送给每一个管道
        for(WebSocketChatServer socket : sockets){
            try {
                //通过session发送信息
                System.out.println("发送给管道"+socket.session.getId());
                socket.session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
