/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ModuleService;

/**
 * A DataDictionaryMapper that simply consults the statically initialized
 * DataDictionaryIndex mappings
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataDictionaryIndexMapper implements DataDictionaryMapper {
	private static final Logger LOG = Logger.getLogger(DataDictionaryIndexMapper.class);

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getAllInactivationBlockingMetadatas(org.kuali.rice.kns.datadictionary.DataDictionaryIndex, java.lang.Class)
	 */
	public Set<InactivationBlockingMetadata> getAllInactivationBlockingMetadatas(DataDictionaryIndex index, Class blockedClass) {
        return index.getInactivationBlockersForClass().get(blockedClass);
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getBusinessObjectClassNames(org.kuali.rice.kns.datadictionary.DataDictionaryIndex)
	 */
	public List<String> getBusinessObjectClassNames(DataDictionaryIndex index) {
		List classNames = new ArrayList();
		classNames.addAll(index.getBusinessObjectEntries().keySet());

		return Collections.unmodifiableList(classNames);
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getBusinessObjectEntries(org.kuali.rice.kns.datadictionary.DataDictionaryIndex)
	 */
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries(DataDictionaryIndex index) {
		return index.getBusinessObjectEntries();
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getBusinessObjectEntryForConcreteClass(java.lang.String)
	 */
	public BusinessObjectEntry getBusinessObjectEntryForConcreteClass(DataDictionaryIndex ddIndex, String className) {
		if (StringUtils.isBlank(className)) {
			throw new IllegalArgumentException("invalid (blank) className");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getBusinessObjectEntry '" + className + "'");
		}
		int index = className.indexOf("$$");
		if (index >= 0) {
			className = className.substring(0, index);
		}
		return ddIndex.getBusinessObjectEntries().get(className);
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getDictionaryObjectEntry(org.kuali.rice.kns.datadictionary.DataDictionaryIndex, java.lang.String)
	 */
	public DataDictionaryEntry getDictionaryObjectEntry(DataDictionaryIndex ddIndex, String className) {
		if (StringUtils.isBlank(className)) {
			throw new IllegalArgumentException("invalid (blank) className");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getDictionaryObjectEntry '" + className + "'");
		}
		int index = className.indexOf("$$");
		if (index >= 0) {
			className = className.substring(0, index);
		}

		// look in the JSTL key cache
		DataDictionaryEntry entry = ddIndex.getEntriesByJstlKey().get(className);
		// check the BO list
		if ( entry == null ) {
		    entry = getBusinessObjectEntry(ddIndex, className);
		}
		// check the document list
		if ( entry == null ) {
		    entry = getDocumentEntry(ddIndex, className);
		}
		return entry;
	}

	public BusinessObjectEntry getBusinessObjectEntry(DataDictionaryIndex index, String className ) {
		BusinessObjectEntry entry = getBusinessObjectEntryForConcreteClass(index, className);
		if (entry == null) {
			Class boClass = null;
			try{
				boClass = Class.forName(className);
				ModuleService responsibleModuleService = KNSServiceLocator.getKualiModuleService().getResponsibleModuleService(boClass);
				if(responsibleModuleService!=null && responsibleModuleService.isExternalizable(boClass)) {
					return responsibleModuleService.getExternalizableBusinessObjectDictionaryEntry(boClass);
				}
			} catch(ClassNotFoundException cnfex){
			}
			return null;
		}
		else {
			return entry;
		}
	}
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getDocumentEntries(org.kuali.rice.kns.datadictionary.DataDictionaryIndex)
	 */
	public Map<String, DocumentEntry> getDocumentEntries(DataDictionaryIndex index) {
		return Collections.unmodifiableMap(index.getDocumentEntries());
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getDocumentEntry(org.kuali.rice.kns.datadictionary.DataDictionaryIndex, java.lang.String)
	 */
	public DocumentEntry getDocumentEntry(DataDictionaryIndex index, String documentTypeDDKey) {

		if (StringUtils.isBlank(documentTypeDDKey)) {
			throw new IllegalArgumentException("invalid (blank) documentTypeName");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getDocumentEntry by documentTypeName '" + documentTypeDDKey + "'");
		}

		DocumentEntry de = index.getDocumentEntries().get(documentTypeDDKey);	
		
		if ( de == null ) {
		    try {
    		    Class clazz = Class.forName( documentTypeDDKey );
    		    de = index.getDocumentEntriesByBusinessObjectClass().get(clazz);
    		    if ( de == null ) {
    		        de = index.getDocumentEntriesByMaintainableClass().get(clazz);
    		    }
		    } catch ( ClassNotFoundException ex ) {
		        LOG.warn( "Unable to find document entry for key: " + documentTypeDDKey );
		    }
		}
		
        return de;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getDocumentTypeName(org.kuali.rice.kns.datadictionary.DataDictionaryIndex, java.lang.String)
	 */
	public String getDocumentTypeName(DataDictionaryIndex index,
			String documentTypeName) {
		// TODO arh14 - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getMaintenanceDocumentEntryForBusinessObjectClass(org.kuali.rice.kns.datadictionary.DataDictionaryIndex, java.lang.Class)
	 */
	public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(DataDictionaryIndex index, Class businessObjectClass) {
		if (businessObjectClass == null) {
			throw new IllegalArgumentException("invalid (null) businessObjectClass");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getDocumentEntry by businessObjectClass '" + businessObjectClass + "'");
		}

		return (MaintenanceDocumentEntry) index.getDocumentEntriesByBusinessObjectClass().get(businessObjectClass);
	}
}