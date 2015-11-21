package com.huoju.station.supernode.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyStompMessageController {
	private SimpMessagingTemplate template;

	@Autowired
	public MyStompMessageController(SimpMessagingTemplate template) {
		this.template = template;
	}

	@MessageMapping(value="/greetings")
	public void greet(String greeting) {
		String text = "[" + new Date() + "]:" + greeting;
		System.out.println(text);
		this.template.convertAndSend("/topic/greetings", text);
	}

	@RequestMapping(value = "/test")
	public String test() {
		return "forward:/index.jsp";
	}
}
