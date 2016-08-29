package org.mao.jtapi.impl;

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

import org.cisco.jtapi.ConnAlertingEvImpl;
import org.cisco.jtapi.extensions.CiscoAddress;
import org.cisco.jtapi.extensions.CiscoCall;
import org.cisco.jtapi.extensions.CiscoConnection;
import org.cisco.jtapi.extensions.CiscoTerminal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("ForwardOnBusyOtherAddressCommand")
public class ForwardOnBusyOtherAddressCommand extends DefaultTerminalObserverCommand {

	@Value("${jtapi.forwardonbusy.dn}")
	private String forwardOnBusyDn;
	
	@Override
	public void executeCommand(DefaultTerminalObserver observer, Ev event) {
		if (observer == null) 
			return;
		
		if (event == null)
			return;
		
		logger.debug("executeCommand: {} for {}", event, observer.getTerminalName());
		
		forwardOnBusy(observer, event);
	
		//super.executeCommand(observer, event);
	}

	/*
	 * 
	 */
	private void forwardOnBusy(DefaultTerminalObserver observer, Ev event) {
		
		// is call event?
		if (!(event instanceof CallEv))
			return;
		
		// is alerting ?
		if (!(event instanceof ConnAlertingEv))
			return;
		
		ConnAlertingEvImpl cce = (ConnAlertingEvImpl)event;
		CiscoCall call = (CiscoCall)cce.getCall();
		CiscoTerminal terminal = (CiscoTerminal)observer.getTerminal();
		Address[] addresses = terminal.getAddresses();
		
		CiscoAddress called = (CiscoAddress)call.getCalledAddress();
		CiscoAddress calling = (CiscoAddress)call.getCallingAddress();
		
		logger.debug("forwardOnBusy: {} has {} address(es). Called Address={}, Calling Address={}", 
				terminal.getName(), addresses.length, called.getName(), calling.getName());

		// check addresses first if minimum 2 addresses are there
		if (addresses.length > 1)
			if (checkRedirectByAddresses(addresses, called, calling)) {
				redirect(call.getConnections());
				return;
			}

		// only one address is there, so what about connections
		if (checkRedirectByConnections(addresses[0])) 
			redirect(addresses[0].getConnections());
			
	
	}

	private boolean checkRedirectByAddresses(Address[] addresses, CiscoAddress called, CiscoAddress calling) {
		
		boolean amiCalled = false;
		boolean amiCallingMyself = false;
		// check if somebody -not me/myself- is calling me
		for (int i = 0; i < addresses.length; i++) { 
			
			if (addresses[i].getName().equals(called.getName()))
				amiCalled = true;
			
			if (addresses[i].getName().equals(calling.getName()))
				amiCallingMyself = true;
			
			if (amiCalled && amiCallingMyself)
				break;
		}
			
		
		logger.info("checkRedirectByAddresses: Am I calling myself ? {}", amiCallingMyself ? "YES" : "NO");
		logger.info("checkRedirectByAddresses: Am I called ? {}", amiCalled ? "YES" : "NO");
		
		if (!amiCalled || amiCallingMyself)
			return false;
		
		// one of called or calling is not me, go on
		
		boolean amiTalkingAlready = false;
		// check if I am already in talking at my other line?
		for (int i = 0; i < addresses.length; i++)
			if (!addresses[i].getName().equals(called.getName())) {
				if (addresses[i].getConnections() != null) {
					amiTalkingAlready = true;
					break;
				}
			} 
				
		
		logger.info("checkRedirectByAddresses: Am I talking already ? {}", amiTalkingAlready ? "YES" : "NO");
			
		if (!amiTalkingAlready)
			return false;
		
		return true;
	}

	private void redirect(Connection[] connections) {
		
		for (int i = 0; i < connections.length; i++) {
			if (connections[i].getState() == Connection.ALERTING) {
				logger.warn("redirect: connection state {} means ALERTING.  so being redirected to {}", 
						connections[i].getState(), forwardOnBusyDn);
				try {
					((CiscoConnection)connections[i]).redirect(forwardOnBusyDn);
					return;
				} catch (InvalidStateException | InvalidPartyException | MethodNotSupportedException
						| PrivilegeViolationException | ResourceUnavailableException e) {
					logger.error("redirect: redirect exception {}", e);
				}
			}
		}
	
	}

	private boolean checkRedirectByConnections(Address address) {
		
		
		Connection[] connections = address.getConnections();
		
		// has second call ?
		if (connections.length < 2)
			return false;
		
		boolean amiCalled = false;
		boolean amiTalkingAlready = false;
		// check if somebody -not me/myself- is calling me
		for (int i = 0; i < connections.length; i++) { 
			
			if (connections[i].getState() == Connection.ALERTING)
				amiCalled = true;
			
			if (connections[i].getState() == Connection.CONNECTED)
				amiTalkingAlready = true;
				
			if (amiCalled && amiTalkingAlready)
				break;
		}
		
		logger.info("checkRedirectByConnections: Am I called ? {}", amiCalled ? "YES" : "NO");
		logger.info("checkRedirectByConnections: Am I talking already ? {}", amiTalkingAlready ? "YES" : "NO");
		
		if (amiCalled && amiTalkingAlready)
			return true;
		
		return false;
	}
	
	
}
