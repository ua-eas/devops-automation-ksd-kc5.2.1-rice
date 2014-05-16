/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.rules.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.rule.event.ui.AddResponsibilityEvent;
import org.kuali.rice.kim.rule.ui.AddResponsibilityRule;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimDocumentResponsibilityRule extends DocumentRuleBase implements AddResponsibilityRule {

	public static final String ERROR_PATH = "document.responsibility.responsibilityId";

	public boolean processAddResponsibility(AddResponsibilityEvent addResponsibilityEvent) {
		KimDocumentRoleResponsibility newResponsibility = addResponsibilityEvent.getResponsibility();
		if(newResponsibility==null){
			GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Responsibility"});
			return false;
		}

		KimResponsibilityImpl kimResponsibilityImpl = newResponsibility.getKimResponsibility();
		if(kimResponsibilityImpl==null){
			GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Responsibility"});
			return false;
		}

		IdentityManagementRoleDocument document = (IdentityManagementRoleDocument)addResponsibilityEvent.getDocument();		
	    boolean rulePassed = true;
		if (!hasPermissionToGrantResponsibility(kimResponsibilityImpl.toSimpleInfo(), document)) {
            GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_ASSIGN_RESPONSIBILITY, 
            		new String[] {kimResponsibilityImpl.getNamespaceCode(), kimResponsibilityImpl.getTemplate().getName()});
            return false;
		}
		
		if (newResponsibility == null || StringUtils.isBlank(newResponsibility.getResponsibilityId())) {
            rulePassed = false;
            GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Responsibility"});
        } else {
        	int i = 0;
		    for (KimDocumentRoleResponsibility responsibility: document.getResponsibilities()) {
		    	if (responsibility.getResponsibilityId().equals(newResponsibility.getResponsibilityId())) {
		            rulePassed = false;
		            GlobalVariables.getMessageMap().putError("document.responsibilities["+i+"].responsibilityId", RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Responsibility"});
		    	}
		    	i++;
		    }
        }
		return rulePassed;
	} 

	public boolean hasPermissionToGrantResponsibility(KimResponsibilityInfo kimResponsibilityInfo, IdentityManagementRoleDocument document){
		Map<String,String> responsibilityDetails = new HashMap<String,String>();
		responsibilityDetails.put(KimAttributes.NAMESPACE_CODE, kimResponsibilityInfo.getNamespaceCode());
		responsibilityDetails.put(KimAttributes.RESPONSIBILITY_NAME, kimResponsibilityInfo.getName());
		if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document, 
				KimConstants.NAMESPACE_CODE, 
				KimConstants.PermissionTemplateNames.GRANT_RESPONSIBILITY, 
				GlobalVariables.getUserSession().getPerson().getPrincipalId(), 
				responsibilityDetails, null)) {
            return false;
		}
		return true;
	}
}