package org.mao.jtapi.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.telephony.Address;
import javax.telephony.InvalidArgumentException;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.JtapiPeerUnavailableException;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.Provider;
import javax.telephony.ProviderObserver;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.events.ProvEv;
import javax.telephony.events.ProvInServiceEv;
import javax.telephony.events.ProvOutOfServiceEv;

import com.cisco.cti.util.Condition;
import com.cisco.jtapi.extensions.CiscoJtapiPeer;
import com.cisco.jtapi.extensions.CiscoJtapiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@Order(value=1)
public class DefaultProviderObserver implements ProviderObserver {
	
	public static NullPointerException ProviderIsNullException = new NullPointerException("Provider");
	
	@Value("${jtapi.name.primary}")
	private String primaryName;
	@Value("${jtapi.name.secondary}")
	private String secondaryName;
	@Value("${jtapi.login}")
	private String login;
	@Value("${jtapi.password}")
	private String password;
	@Value("${jtapi.appinfo}")
	private String appInfo;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultProviderObserver.class);
	
	private Provider provider = null;
	private Condition conditionInService = new Condition();
	
	public Address[] getAddresses() {
		
		try {
			return provider.getAddresses();
		} catch (ResourceUnavailableException e) {
			logger.error("Exception {}", e);
			return null;
		}
	
	}
	
	public Address getAddress(String addressName) {
		
		try {
			return provider.getAddress(addressName);
		} catch (InvalidArgumentException e) {
			logger.error("Exception {}", e);
			return null;
		}
		
	}
	
	public Terminal[] getTerminals() {
		
		try {
			return provider.getTerminals();
		} catch (ResourceUnavailableException e) {
			logger.error("Exception {}", e);
			return null; 
		}
		
	}
	
	public Terminal getTerminal(String terminalName) {
		
		try {
			return provider.getTerminal(terminalName);
		} catch (InvalidArgumentException e) {
			logger.error("Exception {}", e);
			return null; 
		}
		
	}

	@Override
	public void providerChangedEvent(ProvEv[] provEvents) {
		
		if (provEvents == null)
			return;
		
		for(ProvEv provEvent : provEvents) {
			
			logger.warn("providerChangedEvent: {}", provEvent);
			
			if (provEvent instanceof ProvInServiceEv)
			{
				
				conditionInService.set();
				for(Terminal term : getTerminals())
					logger.debug("Terminal {} is found.", term.getName());
				
			} else if (provEvent instanceof ProvOutOfServiceEv) {
				//try to connect to secondary cucm
				
			}
				
				
		}
		
	}
	
	@PreDestroy
	public void closeSession() {
		
		if (provider != null) {
			
			if (provider.getState() == Provider.IN_SERVICE)
				provider.shutdown();
			
			provider.removeObserver(this);
			
		}
		
	}

	@PostConstruct
	private void openSession() {
		
		try {
			CiscoJtapiPeer jtapiPeer = (CiscoJtapiPeer)JtapiPeerFactory.getJtapiPeer(null);
			CiscoJtapiProperties jtapiProp = jtapiPeer.getJtapiProperties();
	
			jtapiProp.setTraceDirectory("./logs/");
			
			logger.info("Connecting to [{},{}] as {} with user {}...", primaryName, secondaryName, appInfo, login);
			
			provider = jtapiPeer.getProvider(String.format("%1$s,%2$s;login=%3$s;passwd=%4$s;appinfo=%5$s", primaryName, secondaryName, login, password,  appInfo));
			provider.addObserver(this);
			conditionInService.waitTrue();
			
			
		} catch (JtapiPeerUnavailableException e) {
			logger.error("Exception {}", e);
		} catch (ResourceUnavailableException e) {
			logger.error("Exception {}", e);
		} catch (MethodNotSupportedException e) {
			logger.error("Exception {}", e);
		}
		
	}
	
}
