package com.starter.speech;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ding
 */
@ServerEndpoint("/speech")
@Component
public class SpeechSocket {
    private static ConcurrentHashMap<String, SpeechSocket> webSocketMap = new ConcurrentHashMap<>();

    private Session session;
    private String uid;

    public static void sendMessage(String msgStr) {
        webSocketMap.values().forEach(socket -> {
            try {
                socket.session.getAsyncRemote().sendText(msgStr);
            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        System.out.printf("Receive message: %s, %s, %s\n", uid, session.getId(), msg);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.uid = session.getId();

        webSocketMap.put(uid, this);
        System.out.printf("Online: %d, %s\n", webSocketMap.size(), uid);
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(uid);
        System.out.printf("Offline 1, %s, online: %d\n", uid, webSocketMap.size());
    }

    @OnError
    public void onError(Session session, Throwable e) {
        System.err.printf("Error: %s, %s, %s\n", uid, session.getId(), e.getMessage());
    }
}
