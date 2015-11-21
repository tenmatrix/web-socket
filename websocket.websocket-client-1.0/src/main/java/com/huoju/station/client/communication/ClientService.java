package com.huoju.station.client.communication;

public class ClientService {

	private WebSocketClient wc;
	
	
	public WebSocketClient getWc() {
		return wc;
	}


	public void setWc(WebSocketClient wc) {
		this.wc = wc;
	}


	public void login(){System.out.println("service 发送消息");
		wc.sendMsg("login msg");
	}
}
