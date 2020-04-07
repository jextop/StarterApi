package com.starter.kitchen;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Socket server for admin page
 *
 * @author ding
 */
@ServerEndpoint("/kitchen")
@Component
public class KitchenSocket {
    static Map<String, KitchenSocket> webSocketMap;

    private Session session;
    private String uid;

    static {
        webSocketMap = new ConcurrentHashMap<>();
    }

    public void sendMessage(String msgStr) {
        webSocketMap.values().forEach(socket -> {
            try {
                socket.session.getAsyncRemote().sendText(msgStr);
            } catch (IllegalStateException e) {
            }
        });
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.uid = session.getId();

        webSocketMap.put(uid, this);
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(uid);
    }
}
