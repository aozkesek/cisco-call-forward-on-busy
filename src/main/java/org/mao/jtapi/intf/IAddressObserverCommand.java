package org.mao.jtapi.intf;

import javax.telephony.events.Ev;

import org.mao.jtapi.impl.DefaultAddressObserver;
import org.springframework.stereotype.Component;

@Component
public interface IAddressObserverCommand extends IObserverCommand<DefaultAddressObserver, Ev> {

}
