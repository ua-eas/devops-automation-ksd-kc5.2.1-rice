/**
 * Copyright 2005-2015 The Kuali Foundation
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Prerequisite constraints require that some other attribute be non-empty in order for the constraint to be valid. 
 * So, a 7-digit US phone number might have a prerequisite of an area code, or an address street2 might have a prerequisite
 * that street1 is non-empty. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PrerequisiteConstraint extends BaseConstraint {
	@XmlElement
    protected String propertyName;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

    @Override
    public String getLabelKey(){
        if(StringUtils.isBlank(this.labelKey)){
            return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "prerequisiteFallback";
        }
        else{
            return super.getLabelKey();
        }
    }

    @Override
    /**
     * @see BaseConstraint#getValidationMessageParams()
     * @return the validation message list if defined. If not defined,  return  the property name
     */
    public List<String> getValidationMessageParams() {
        if(super.getValidationMessageParams() == null) {
            ArrayList<String> params = new ArrayList<String>(1);
            params.add(getPropertyName());
            return params;
        } else {
            return super.getValidationMessageParams();
        }
    }
}
