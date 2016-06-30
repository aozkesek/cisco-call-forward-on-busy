package com.netas.jtapi.intf;

import javax.telephony.events.Ev;

import org.springframework.stereotype.Component;

import com.netas.jtapi.impl.DefaultAddressObserver;

@Component
public interface IAddressObserverCommand extends IObserverCommand<DefaultAddressObserver, Ev> {

}
