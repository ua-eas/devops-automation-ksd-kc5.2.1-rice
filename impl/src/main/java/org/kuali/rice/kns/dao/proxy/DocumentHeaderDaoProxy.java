/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.dao.proxy;

import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.dao.DocumentHeaderDao;

public class DocumentHeaderDaoProxy implements DocumentHeaderDao {
    private DocumentHeaderDao documentHeaderDaoJpa;
    private DocumentHeaderDao documentHeaderDaoOjb;
	
    private DocumentHeaderDao getDao(Class clazz) {
    	return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? documentHeaderDaoJpa : documentHeaderDaoOjb; 
    }
    
    public DocumentHeader getByDocumentHeaderId(String id) {    	
    	return getDao(DocumentHeader.class).getByDocumentHeaderId(id);
    }

	public Class getDocumentHeaderBaseClass() {
		return getDao(DocumentHeader.class).getDocumentHeaderBaseClass();
	}

	public void setDocumentHeaderDaoJpa(DocumentHeaderDao documentHeaderDaoJpa) {
		this.documentHeaderDaoJpa = documentHeaderDaoJpa;
	}

	public void setDocumentHeaderDaoOjb(DocumentHeaderDao documentHeaderDaoOjb) {
		this.documentHeaderDaoOjb = documentHeaderDaoOjb;
	}

}