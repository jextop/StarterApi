package com.starter.kitchen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KitchenSocketTest {
    @Autowired
    KitchenSocket kitchenSocket;

    @Mock
    Session session;

    @BeforeEach
    public void initMock() {
        MockitoAnnotations.initMocks(this);
        when(session.getId()).thenReturn("test");
    }

    @Test
    public void testSendMessage() throws IOException {
        Async async = mock(Async.class);
        when(session.getAsyncRemote()).thenReturn(async);

        // Send message
        kitchenSocket.sendMessage("test");
        verify(async, never()).sendText("test");

        // Connect
        kitchenSocket.onOpen(session);
        kitchenSocket.sendMessage("test");
        verify(async, times(1)).sendText(any());

        // Exception
        doThrow(new IllegalStateException("unit testing mock")).when(async).sendText(any());
        kitchenSocket.sendMessage("test");
        verify(async, times(2)).sendText(any());

        // Close
        kitchenSocket.onClose();
    }

    @Test
    public void testOnOpen() {
        int count = KitchenSocket.webSocketMap.size();
        kitchenSocket.onOpen(session);
        verify(session, times(1)).getId();
        Assertions.assertEquals(count + 1, KitchenSocket.webSocketMap.size());

        // Close
        kitchenSocket.onClose();
    }

    @Test
    public void testOnClose() {
        int count = KitchenSocket.webSocketMap.size();
        kitchenSocket.onOpen(session);
        kitchenSocket.onClose();
        Assertions.assertEquals(count, KitchenSocket.webSocketMap.size());
    }
}
