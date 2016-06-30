package com.netas.jtapi.impl;

import javax.telephony.events.CallEv;
import javax.telephony.events.Ev;
import javax.telephony.events.TermEv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netas.jtapi.intf.ITerminalObserverCommand;

@Component("ForwardOnBusyOtherAddressCommand")
public class ForwardOnBusyOtherAddressCommand implements ITerminalObserverCommand {

	private static Logger logger = LoggerFactory.getLogger(ForwardOnBusyOtherAddressCommand.class);
	
	@Override
	public void executeCommand(DefaultTerminalObserver observer, Ev event) {
		if (observer == null) 
			return;
		
		if (event == null)
			return;
		
		logger.debug("executeCommand: {} for {}", event, observer.getTerminalName());
		
		if (event instanceof CallEv) {
			
			
		} else if (event instanceof TermEv) {
			
		}
		
		
	}
	
	

}
