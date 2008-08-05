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
package edu.iu.uis.eden.testclient1;


import java.io.Serializable;

import org.kuali.rice.ksb.messaging.KEWJavaService;

import edu.iu.uis.eden.messaging.ClientAppServiceSharedPayloadObj;


/**
 * A service that is registered as a queue for both the client apps.  Used to test queue 
 * call scenarios.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ClientApp1SharedQueue implements KEWJavaService {
	
	public void invoke(Serializable payLoad) {
		
		
		ClientAppServiceSharedPayloadObj sharedPayload = (ClientAppServiceSharedPayloadObj) payLoad;
		if (sharedPayload.isThrowException()) {
			throw new RuntimeException("ClientAppSharedQueue throwing exception.");
		}
	}
}