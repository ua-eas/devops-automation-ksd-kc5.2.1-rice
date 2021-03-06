/*
 * Copyright 2014 The Kuali Foundation
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

package org.kuali.rice.kim.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;

// **AZ UPGRADE 3.0-5.3** - add findEntityDefaults(Map<String, String> criteria, boolean unbounded) method to base IdentityService interface
public interface LdapIdentityService extends IdentityService {
    public List<EntityDefault> findEntityDefaults(Map<String, String> criteria, boolean unbounded);
    // UAF-6 - Performance improvements to improve user experience for AWS deployment
    public List<EntityDefault> getEntityDefaultsByPrincipalIds(Collection<String> principalIds);
}
