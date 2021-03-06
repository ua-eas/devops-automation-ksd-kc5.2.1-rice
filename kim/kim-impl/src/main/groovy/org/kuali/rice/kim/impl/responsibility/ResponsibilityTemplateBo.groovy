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
package org.kuali.rice.kim.impl.responsibility;


import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.common.template.Template
import org.kuali.rice.kim.api.common.template.TemplateContract
import org.kuali.rice.kim.impl.common.template.TemplateBo

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_RSP_TMPL_T")
public class ResponsibilityTemplateBo  extends TemplateBo implements TemplateContract {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "RSP_TMPL_ID")
    String id

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Template to(ResponsibilityTemplateBo bo) {
        if (bo == null) {
            return null
        }

        return Template.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static ResponsibilityTemplateBo from(Template im) {
        if (im == null) {
            return null
        }

        ResponsibilityTemplateBo bo = new ResponsibilityTemplateBo()
        bo.id = im.id
        bo.namespaceCode = im.namespaceCode
        bo.name = im.name
        bo.description = im.description
        bo.active = im.active
        bo.kimTypeId = im.kimTypeId
        bo.versionNumber = im.versionNumber
        bo.objectId = im.objectId;

        return bo
    }
}
