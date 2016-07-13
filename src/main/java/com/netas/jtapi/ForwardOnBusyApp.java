package com.netas.jtapi;

import javax.telephony.Address;
import javax.telephony.Terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.netas.jtapi.impl.DefaultAddressObserver;
import com.netas.jtapi.impl.DefaultProviderObserver;
import com.netas.jtapi.impl.DefaultTerminalObserver;
import com.netas.jtapi.impl.ForwardOnBusyOtherAddressCommand;
import com.netas.jtapi.impl.ObserverContainer;

@SpringBootApplication
public class ForwardOnBusyApp {
	
	public static ApplicationContext JTAPIAppContext;	
	public static Logger logger = LoggerFactory.getLogger(ForwardOnBusyApp.class); 
	
	public static void main(String[] args) {
		
		System.setProperty("spring.devtools.restart.enabled", "false");
		
		JTAPIAppContext = SpringApplication.run(ForwardOnBusyApp.class, args);
		
		startTerminalObservers();
		//startAddressObservers();
		
	}

	public static void startTerminalObservers() {
		Terminal[] terms = JTAPIAppContext.getBean(DefaultProviderObserver.class).getTerminals();
		
		if (terms == null) {
			logger.error("This user has not Terminal(s)");
			return;
		}
		
		ObserverContainer observerContainer = JTAPIAppContext.getBean(ObserverContainer.class);
		
		for (Terminal term : terms) {
			String terminalName = term.getName();
			
			if (terminalName == null)
				continue;
			
			try {
				// if you implement terminal observer different than default, you get and use it
				DefaultTerminalObserver termObserver = ForwardOnBusyApp.JTAPIAppContext.getBean(
						DefaultTerminalObserver.class, terminalName);
				
				// set fwd on busy instead of default 
				termObserver.setTerminalObserverCommand(ForwardOnBusyApp.JTAPIAppContext.getBean(
						ForwardOnBusyOtherAddressCommand.class, "ForwardOnBusyOtherAddressCommand"
						));
				
				// make sure only one exists
				observerContainer.addTerminalObserver(termObserver);
				
			}
			catch(Exception ex) {
				logger.error("startTerminalObservers: Exception {}", ex);
			}
			
		}
		
		
	}
	
	public static void startAddressObservers() {
		Address[] addrs = JTAPIAppContext.getBean(DefaultProviderObserver.class).getAddresses();
		
		if (addrs == null) {
			logger.error("This user has not Address(es)");
			return;
		}
		
		ObserverContainer observerContainer = JTAPIAppContext.getBean(ObserverContainer.class);
		
		for (Address addr : addrs) {
			String addressName = addr.getName();
			
			if (addressName == null)
				continue;
			
			try {
				//if you implement address observer different than default, you get and use it
				DefaultAddressObserver addrObserver = ForwardOnBusyApp.JTAPIAppContext.getBean(
						DefaultAddressObserver.class, addressName);
				
				observerContainer.addAddressObserver(addrObserver);
				
			}
			catch(Exception ex) {
				logger.error("startAddressObservers: Exception {}", ex);
			}
			
		}
		
		
	}

}
