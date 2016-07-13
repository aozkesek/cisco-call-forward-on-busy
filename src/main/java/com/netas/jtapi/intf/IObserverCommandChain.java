package com.netas.jtapi.intf;

public interface IObserverCommandChain<O, E> {

	void addObserverCommand(IObserverCommand<O, E> observerCommand);
	void removeObserverCommand(IObserverCommand<O, E> observerCommand);
	void getNextObserverCommandInChain(IObserverCommand<O, E> observerCommand);
	
}
