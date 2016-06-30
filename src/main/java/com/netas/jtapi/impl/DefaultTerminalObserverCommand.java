package com.netas.jtapi.impl;

import javax.telephony.events.Ev;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netas.jtapi.intf.ITerminalObserverCommand;

@Component("DefaultTerminalObserverCommand")
public class DefaultTerminalObserverCommand implements ITerminalObserverCommand {

	private static Logger logger = LoggerFactory.getLogger(DefaultTerminalObserverCommand.class);
	
	@Override
	public void executeCommand(DefaultTerminalObserver terminalObserver, Ev terminalEvent) {
		
		logger.debug("executeCommand: {} for {}", terminalEvent, terminalObserver.getTerminalName());
		//define here default behavior or implement a new and specific command class
	}

}
