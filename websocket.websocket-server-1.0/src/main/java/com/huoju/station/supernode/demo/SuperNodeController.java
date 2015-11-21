package com.huoju.station.supernode.demo;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SuperNodeController {
	private static final Log logger = LogFactory.getLog(SuperNodeController.class);
	
	private static List<ClientBean> clients=new ArrayList<ClientBean>();
	static{
		ClientBean o = new ClientBean();
		o.setName("111");
		ClientBean t = new ClientBean();
		t.setName("222");
		ClientBean th = new ClientBean();
		th.setName("333");
		clients.add(o);
		clients.add(t);
		clients.add(th);
	}
	
	
	@MessageMapping("/hello")
    @SendTo("/topic/greetings")
	 public String greeting(Principal bb) throws Exception {
        Thread.sleep(3000); // simulated delay
        return "Hello, " + bb.getName()+ "!";
    }
	
	
	/**
	 * 订阅监听client状态
	 * @param observer
	 * @return
	 * @throws Exception
	 */
//	@SubscribeMapping("/clients")
	@MessageMapping("/clients")
	public List<ClientBean> getPositions(Principal observer) throws Exception {
		logger.debug("{} subscribe clients " + observer.getName());
//		Portfolio portfolio = this.portfolioService.findPortfolio(principal.getName());
		return clients;
	}
	
	/**
	 * client登陆，然后发布到订阅了clients的观察者那
	 */
	@MessageMapping("/login") 
	@SendTo("/topic/clients")
	public List<ClientBean> login(){
		ClientBean clientBean = new ClientBean();
		clientBean.setName("new one");
		clients.add(clientBean);
		
		logger.debug("{} logged in " + clientBean.getName()+"clients size:"+clients.size()+clients);
		return clients;
	}
}
