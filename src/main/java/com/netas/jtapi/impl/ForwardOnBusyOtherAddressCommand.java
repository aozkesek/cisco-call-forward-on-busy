package com.netas.jtapi.impl;

import javax.telephony.Address;
import javax.telephony.Connection;
import javax.telephony.InvalidPartyException;
import javax.telephony.InvalidStateException;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.PrivilegeViolationException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.events.CallEv;
import javax.telephony.events.ConnAlertingEv;
import javax.telephony.events.Ev;
import javax.telephony.events.TermEv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cisco.jtapi.ConnAlertingEvImpl;
import com.cisco.jtapi.extensions.CiscoAddress;
import com.cisco.jtapi.extensions.CiscoCall;
import com.cisco.jtapi.extensions.CiscoConnection;
import com.cisco.jtapi.extensions.CiscoTerminal;
import com.netas.jtapi.intf.ITerminalObserverCommand;

@Component("ForwardOnBusyOtherAddressCommand")
public class ForwardOnBusyOtherAddressCommand implements ITerminalObserverCommand {

	@Value("${jtapi.forwardonbusy.dn}")
	private String forwardOnBusyDn;
	
	private static Logger logger = LoggerFactory.getLogger(ForwardOnBusyOtherAddressCommand.class);
	
	@Override
	public void executeCommand(DefaultTerminalObserver observer, Ev event) {
		if (observer == null) 
			return;
		
		if (event == null)
			return;
		
		logger.debug("executeCommand: {} for {}", event, observer.getTerminalName());
		
		// is call event?
		if (event instanceof CallEv) {
			// is alerting ?
			if (event instanceof ConnAlertingEv) {
				ConnAlertingEvImpl cce = (ConnAlertingEvImpl)event;
				CiscoCall cCall = (CiscoCall)cce.getCall();
				CiscoTerminal cTerminal = (CiscoTerminal)observer.getTerminal();
				Address[] addresses = cTerminal.getAddresses();
				
				CiscoAddress cCalled = (CiscoAddress)cCall.getCalledAddress();
				CiscoAddress cCalling = (CiscoAddress)cCall.getCallingAddress();
				
				logger.debug("executeCommand: {} has {} address(es). Called Address={}, Calling Address={}", 
						cTerminal.getName(), addresses.length, cCalled.getName(), cCalling.getName());
				
				// has second or more line ?
				if (addresses.length < 2)
					return;
				
				boolean amiCalled = false;
				// check if somebody is calling me
				for (int i = 0; i < addresses.length; i++) 
					if (addresses[i].getName().equals(cCalled.getName())) {
						amiCalled = true;
						break;
					}
				
				logger.debug("executeCommand: Am I called ? {}", amiCalled ? "YES" : "NO");
				
				if (amiCalled) {
					boolean amiTalkingAlready = false;
					// check if I am already in talking at my other line?
					for (int i = 0; i < addresses.length; i++)
						if (!addresses[i].getName().equals(cCalled.getName()))
							if (addresses[i].getConnections() != null) {
								amiTalkingAlready = true;
								break;
							}
					
					logger.debug("executeCommand: Am I talking already ? {}", amiTalkingAlready ? "YES" : "NO");
						
					if (amiTalkingAlready) {
						Connection[] cConns = cCall.getConnections();
						for (int i = 0; i < cConns.length; i++) {
							if (cConns[i].getState() == Connection.ALERTING) {
								logger.debug("executeCommand: connection state {} means ALERTING.  so being redirected to {}", 
										cConns[i].getState(), forwardOnBusyDn);
								try {
									((CiscoConnection)cConns[i]).redirect(forwardOnBusyDn);
								} catch (InvalidStateException | InvalidPartyException | MethodNotSupportedException
										| PrivilegeViolationException | ResourceUnavailableException e) {
									logger.error("executeCommand: redirect exception {}", e);
								}
							}
						}
					}
					
				}
				
			}
			
		} else if (event instanceof TermEv) {
			
		}
		
		
	}
	
	

}
