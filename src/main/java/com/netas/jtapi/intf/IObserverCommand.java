package com.netas.jtapi.intf;

public interface IObserverCommand<O, E> {
	
	void executeCommand(O observer, E event);
	
	void setNextObserverCommandInChain(IObserverCommand<O, E> nextObserverCommand);
	IObserverCommand<O, E> getNextObserverCommandInChain();

}
