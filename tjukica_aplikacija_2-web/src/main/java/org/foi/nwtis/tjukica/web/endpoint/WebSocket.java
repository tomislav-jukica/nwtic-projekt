/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.tjukica.web.endpoint;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Stalker
 */
@ServerEndpoint("/endpoint")
public class WebSocket {
     

    @OnOpen
    public void onOpen(Session session){
        System.out.println("Spojilo se");

    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage::From=" + session.getId() + " Message=" + message);
        
        try {
            session.getBasicRemote().sendText("Hello Client " + session.getId() + "!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @OnClose
    public void closedConnection(Session session) {
    
        System.out.println("Zatvorena veza.");
    }
    
    @OnError
    public void error(Session session, Throwable t) {

        System.out.println("Zatvorena veza.");
    }
}
