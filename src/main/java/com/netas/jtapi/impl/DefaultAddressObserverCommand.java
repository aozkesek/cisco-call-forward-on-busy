package com.netas.jtapi.impl;

import javax.telephony.events.Ev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netas.jtapi.intf.IAddressObserverCommand;

@Component("DefaultAddressObserverCommand")
public class DefaultAddressObserverCommand implements IAddressObserverCommand {

	private static Logger logger = LoggerFactory.getLogger(DefaultAddressObserverCommand.class);
	
	@Override
	public void executeCommand(DefaultAddressObserver addressObserver, Ev event) {
		
		if (addressObserver == null) 
			return;
		
		if (event == null)
			return;
		
		logger.debug("executeCommand: {} for {}", event, addressObserver.getAddressName());
	}

}
