/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.test.service;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.ksb.messaging.SOAPServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.security.soap.CXFWSS4JInInterceptor;
import org.kuali.rice.ksb.security.soap.CXFWSS4JOutInterceptor;

/**
 * This is a description of what this class does - jimt don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
class ServiceTestUtils {
	static int getConfigIntProp(String intPropKey) {
		return Integer.parseInt(getConfigProp(intPropKey));
	}

	static String getConfigProp(String propKey) {
		return ConfigContext.getCurrentContextConfig().getProperty(propKey);
	}
	
	/**
	 * This method tries to get a client proxy for the specified 
	 * remote KIM service
	 * 
	 * @param  clazz - service's class
	 * @return the proxy object
	 * @throws Exception 
	 */
	static Object getRemoteServiceProxy(Class<?> clazz) throws Exception {
		String serverHostStr = getConfigProp("kim.test.host");
		String serverPortStr = getConfigProp("kim.test.port");
		String serviceName = clazz.getSimpleName();
		
		// protocol will probably be configured eventually as well
		String svcAddr = "http://" + serverHostStr + 
							(null != serverPortStr ? ":" + Integer.parseInt(serverPortStr) : "") + "/" +
							getConfigProp("app.context.name") + "/" + "remoting/" + serviceName;

		ClientProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
		clientFactory.setServiceClass(clazz);
		clientFactory.setAddress(svcAddr);
		
		ServiceInfo svcInfo = new ServiceInfo();
		svcInfo.setEndpointUrl(svcAddr);
		svcInfo.setServiceDefinition(new SOAPServiceDefinition());
		//Set logging (not currently) and security interceptors
		// clientFactory.getOutInterceptors().add(new LoggingOutInterceptor());
		clientFactory.getOutInterceptors().add(new CXFWSS4JOutInterceptor(svcInfo));
		// clientFactory.getInInterceptors().add(new LoggingInInterceptor());
		clientFactory.getInInterceptors().add(new CXFWSS4JInInterceptor(svcInfo));
		
		return clientFactory.create();		
	}
}
