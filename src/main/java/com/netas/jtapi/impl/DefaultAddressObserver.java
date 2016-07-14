package com.netas.jtapi.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.telephony.Address;
import javax.telephony.AddressObserver;
import javax.telephony.CallObserver;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.events.AddrEv;
import javax.telephony.events.CallEv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.netas.jtapi.intf.IAddressObserverCommand;
import com.netas.jtapi.intf.ITerminalObserverCommand;

@Component
@Scope("prototype")
public class DefaultAddressObserver implements AddressObserver, CallObserver {

	public static NullPointerException AddressIsNullException = new NullPointerException("Address");
	
	@Autowired
	private DefaultProviderObserver providerObserver;
	@Autowired
	@Qualifier("DefaultAddressObserverCommand")
	private IAddressObserverCommand addressObserverCommand;
	
	private String addressName;
	private Address address;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultAddressObserver.class);
	 
	public DefaultAddressObserver(String addressName) {
		
		if (addressName == null)
			throw DefaultAddressObserver.AddressIsNullException;
		
		this.addressName = addressName;
		
	}
	
	public IAddressObserverCommand getAddresObserverCommand() {
		return addressObserverCommand;
	}
	
	public void setAddresObserverCommand(IAddressObserverCommand addressObserverCommand) {
		this.addressObserverCommand = addressObserverCommand;
	}
	
	@PostConstruct
	private void register() {
		
		if (providerObserver == null)
			throw DefaultProviderObserver.ProviderIsNullException;
		
		address = providerObserver.getAddress(addressName);
		
		if (address == null)
			throw DefaultAddressObserver.AddressIsNullException;
		
		try {
			address.addObserver(this);
			address.addCallObserver(this);
		} catch (ResourceUnavailableException e) {
			logger.error("register: Exception {}", e);
		} catch (MethodNotSupportedException e) {
			logger.error("register: Exception {}", e);
		}
	
	}
	
	@PreDestroy
	private void unregister() {
		
		address.removeObserver(this);
		
	}
	
	@Override
	public void addressChangedEvent(AddrEv[] addrEvents) {
		
		if (addrEvents == null)
			return;
		
		for(AddrEv addrEvent : addrEvents) {
			logger.info("addressChangedEvent: {} for ", addrEvent, addressName);
			
			addressObserverCommand.executeCommand(this, addrEvent);		
		}
		
	}

	public String getAddressName() {
		return addressName;
	}

	@Override
	public void callChangedEvent(CallEv[] callEvents) {
		if (callEvents == null)
			return;
		
		for(CallEv callEvent : callEvents) {
			logger.info("callChangedEvent: {} for {}", callEvent, addressName);
			
			addressObserverCommand.executeCommand(this, callEvent);		
		}
	}

}
