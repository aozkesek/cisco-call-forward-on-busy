package com.netas.jtapi.intf;

import javax.telephony.events.Ev;
import org.springframework.stereotype.Component;

import com.netas.jtapi.impl.DefaultTerminalObserver;

@Component
public interface ITerminalObserverCommand extends IObserverCommand<DefaultTerminalObserver, Ev> {

}
