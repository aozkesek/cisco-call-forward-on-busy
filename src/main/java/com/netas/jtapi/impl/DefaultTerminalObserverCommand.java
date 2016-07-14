package com.netas.jtapi.impl;

import javax.telephony.events.Ev;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netas.jtapi.intf.IObserverCommand;
import com.netas.jtapi.intf.ITerminalObserverCommand;

@Component("DefaultTerminalObserverCommand")
public class DefaultTerminalObserverCommand implements ITerminalObserverCommand {

	protected static Logger logger = LoggerFactory.getLogger(DefaultTerminalObserverCommand.class);
	protected IObserverCommand<DefaultTerminalObserver, Ev> nextObserverCommand = null;
	
	@Override
	public void executeCommand(DefaultTerminalObserver terminalObserver, Ev event) {
		
		if (terminalObserver == null) 
			return;
		
		if (event == null)
			return;
		
		logger.debug("executeCommand: {} for {}", event, terminalObserver.getTerminalName());
		// define here default behavior or implement a new and specific command class
		
		// call next one in chain if exists
		if (getNextObserverCommandInChain() != null)
			getNextObserverCommandInChain().executeCommand(terminalObserver, event);
	}

	@Override
	public IObserverCommand<DefaultTerminalObserver, Ev> getNextObserverCommandInChain() {
		return nextObserverCommand;
	}

	@Override
	public void setNextObserverCommandInChain(IObserverCommand<DefaultTerminalObserver, Ev> nextObserverCommand) {
		this.nextObserverCommand = nextObserverCommand;
	}


}
