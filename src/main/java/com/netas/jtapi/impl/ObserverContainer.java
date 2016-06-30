package com.netas.jtapi.impl;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@Order(value=2)
public class ObserverContainer {

	private static ConcurrentHashMap<String, Object> container = new ConcurrentHashMap<String, Object>();
	private static Logger logger = LoggerFactory.getLogger(ObserverContainer.class); 
	
	@PreDestroy
	private void cleanUp() {
		
	}
	
	public void addAddressObserver(DefaultAddressObserver addressObserver) {
		
		if (addressObserver == null)
			throw DefaultAddressObserver.AddressIsNullException;
		
		container.put(addressObserver.getAddressName(), addressObserver);
		logger.debug("{} is added.", addressObserver.getAddressName());
		
	}
	
	public void removeAddressObserver(String addressName) {
		
		if (addressName == null)
			throw DefaultAddressObserver.AddressIsNullException;
		
		container.remove(addressName);
		logger.debug("{} is removed.", addressName);
		
	}
	
	public void addTerminalObserver(DefaultTerminalObserver terminalObserver) {
		
		if (terminalObserver == null)
			throw DefaultTerminalObserver.TerminalIsNullException;
		
		container.put(terminalObserver.getTerminalName(), terminalObserver);
		logger.debug("{} is added.", terminalObserver.getTerminalName());
		
	}
	
	public void removeTerminalObserver(String terminalName) {
		
		if (terminalName == null)
			throw DefaultTerminalObserver.TerminalIsNullException;
		
		container.remove(terminalName);
		logger.debug("{} is removed.", terminalName);
	}
	
}
