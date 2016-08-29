package org.mao.jtapi.intf;

import javax.telephony.events.Ev;

import org.mao.jtapi.impl.DefaultTerminalObserver;
import org.springframework.stereotype.Component;

@Component
public interface ITerminalObserverCommand extends IObserverCommand<DefaultTerminalObserver, Ev> {

}
