package cn.swipeblade.assistgeo.rockdemo.websocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Created by GOT.hodor on 2017/12/20.
 */

@Controller(value ="wsTestController" )
@MessageMapping(value = "/test")
public class TestController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping(value = "/welcome")
    @SendTo(value = "/topic/web/welcome")
    public String welcome(String clientMessage) {
        return "Welcome " + clientMessage;
    }

}
