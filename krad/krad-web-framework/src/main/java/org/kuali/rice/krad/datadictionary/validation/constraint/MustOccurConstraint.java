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
import java.util.List;

/**
 * Must occur constraints are constraints that indicate some range of acceptable valid results. So a must occur constraint
 * might indicate that between 1 and 3 prequisite constraints must be valid. For example, on a person object, it might be
 * that one of three fields must be filled in:
 * 
 * 1. username
 * 2. email
 * 3. phone number
 * 
 * By imposing a must occur constraint on the person object iself, and setting three prequisite constraints below it, with a min of 1 
 * and a max of 3, this requirement can be enforced. 
 * 
 * A more complicated example might be that a US address is only valid if it provides either:
 * (a) a city and state, or
 * (b) a postal code
 * 
 * To enforce this, a single must occur constraint would have two children: (1) a prequisite constraint on postal code, and (2) a must occur constraint
 * with two child prequisite constraints, on city and state, respectively. By setting min=1/max=2 at the top must occur constraint, 
 * and min=2/max=2 at the leaf constraint, this requirement can be enforced.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MustOccurConstraint extends BaseConstraint {
	
    @XmlElement
    private List<PrerequisiteConstraint> prerequisiteConstraints;
	@XmlElement
    private List<MustOccurConstraint> mustOccurConstraints;
	@XmlElement
	private Integer min;
	@XmlElement
	private Integer max;

	public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
		return prerequisiteConstraints;
	}

	public void setPrerequisiteConstraints(List<PrerequisiteConstraint> prerequisiteConstraints) {
		this.prerequisiteConstraints = prerequisiteConstraints;
	}

	public List<MustOccurConstraint> getMustOccurConstraints() {
		return mustOccurConstraints;
	}

	public void setMustOccurConstraints(List<MustOccurConstraint> occurs) {
		this.mustOccurConstraints = occurs;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

    @Override
    public String getLabelKey(){
        if(StringUtils.isBlank(this.labelKey)){
            return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "mustoccursFallback";
        }
        else{
            return super.getLabelKey();
        }
    }
}
