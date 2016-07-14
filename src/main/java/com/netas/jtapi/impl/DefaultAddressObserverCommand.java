package com.netas.jtapi.impl;

import javax.telephony.events.Ev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netas.jtapi.intf.IAddressObserverCommand;
import com.netas.jtapi.intf.IObserverCommand;

@Component("DefaultAddressObserverCommand")
public class DefaultAddressObserverCommand implements IAddressObserverCommand {

	protected static Logger logger = LoggerFactory.getLogger(DefaultAddressObserverCommand.class);
	protected IObserverCommand<DefaultAddressObserver, Ev> nextObserverCommand = null;
	
	@Override
	public void executeCommand(DefaultAddressObserver addressObserver, Ev event) {
		
		if (addressObserver == null) 
			return;
		
		if (event == null)
			return;
		
		logger.debug("executeCommand: {} for {}", event, addressObserver.getAddressName());
		// default is doing nothing

		// call next one in chain if exists
		if (getNextObserverCommandInChain() != null)
			getNextObserverCommandInChain().executeCommand(addressObserver, event);
	}

	@Override
	public IObserverCommand<DefaultAddressObserver, Ev> getNextObserverCommandInChain() {
		return nextObserverCommand;
	}

	@Override
	public void setNextObserverCommandInChain(IObserverCommand<DefaultAddressObserver, Ev> nextObserverCommand) {
		this.nextObserverCommand = nextObserverCommand;
	}

}
