package com.netas.jtapi.intf;

public interface IObserverCommand<O, E> {
	
	void executeCommand(O observer, E event);

}
