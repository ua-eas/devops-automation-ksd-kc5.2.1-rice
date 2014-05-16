/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.lang.ref.SoftReference;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PersonServiceImpl implements PersonService<PersonImpl> {

	private static Logger LOG = Logger.getLogger( PersonServiceImpl.class );
	protected static final String ENTITY_EXT_ID_PROPERTY_PREFIX = "externalIdentifiers.";
	protected static final String ENTITY_AFFILIATION_PROPERTY_PREFIX = "affiliations.";
	protected static final String ENTITY_TYPE_PROPERTY_PREFIX = "entityTypes.";
	protected static final String ENTITY_EMAIL_PROPERTY_PREFIX = "entityTypes.emailAddresses.";
	protected static final String ENTITY_PHONE_PROPERTY_PREFIX = "entityTypes.phoneNumbers.";
	protected static final String ENTITY_ADDRESS_PROPERTY_PREFIX = "entityTypes.addresses.";
	protected static final String ENTITY_NAME_PROPERTY_PREFIX = "names.";
	protected static final String PRINCIPAL_PROPERTY_PREFIX = "principals.";
	protected static final String ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX = "employmentInformation.";
	// KULRICE-4442 Special handling for extension objects
	protected static final String EXTENSION = "extension";
	
	private IdentityManagementService identityManagementService;	
	private RoleManagementService roleManagementService;
	private BusinessObjectMetaDataService businessObjectMetaDataService;
	private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

	// Max age defined in seconds
	protected int personCacheMaxSize = 3000;
	protected int personCacheMaxAgeSeconds = 3600;

	protected Map<String,MaxAgeSoftReference<PersonImpl>> personCache = Collections.synchronizedMap( new HashMap<String,MaxAgeSoftReference<PersonImpl>>( personCacheMaxSize ) );
	// PERSON/ENTITY RELATED METHODS

	protected List<String> personEntityTypeCodes = new ArrayList<String>( 4 );
	// String that can be passed to the lookup framework to create an type = X OR type = Y criteria
	private String personEntityTypeLookupCriteria = null;
    
	protected Map<String,String> baseLookupCriteria = new HashMap<String,String>();
	protected Map<String,String> criteriaConversion = new HashMap<String,String>();
	protected ArrayList<String> personCachePropertyNames = new ArrayList<String>();
	{
		// init the criteria which will need to be applied to every lookup against
		// the entity data tables
		baseLookupCriteria.put( KIMPropertyConstants.Person.ACTIVE, "Y" );
		baseLookupCriteria.put( ENTITY_TYPE_PROPERTY_PREFIX + KNSPropertyConstants.ACTIVE, "Y" );
		
		// create the field mappings between the Person object and the KimEntity object
		criteriaConversion.put( KIMPropertyConstants.Person.ENTITY_ID, KIMPropertyConstants.Person.ENTITY_ID );
		criteriaConversion.put( KIMPropertyConstants.Person.ACTIVE, PRINCIPAL_PROPERTY_PREFIX + KNSPropertyConstants.ACTIVE );
		criteriaConversion.put( KIMPropertyConstants.Person.PRINCIPAL_ID, PRINCIPAL_PROPERTY_PREFIX + KIMPropertyConstants.Person.PRINCIPAL_ID );
		criteriaConversion.put( KIMPropertyConstants.Person.PRINCIPAL_NAME, PRINCIPAL_PROPERTY_PREFIX + KIMPropertyConstants.Person.PRINCIPAL_NAME );
		criteriaConversion.put( KIMPropertyConstants.Person.FIRST_NAME, "names.firstName" );
		criteriaConversion.put( KIMPropertyConstants.Person.LAST_NAME, "names.lastName" );
		criteriaConversion.put( KIMPropertyConstants.Person.MIDDLE_NAME, "names.middleName" );
		criteriaConversion.put( KIMPropertyConstants.Person.EMAIL_ADDRESS, "entityTypes.emailAddresses.emailAddress" );
		criteriaConversion.put( KIMPropertyConstants.Person.PHONE_NUMBER, "entityTypes.phoneNumbers.phoneNumber" );
		criteriaConversion.put( KIMPropertyConstants.Person.ADDRESS_LINE_1, "entityTypes.addresses.line1" );
		criteriaConversion.put( KIMPropertyConstants.Person.ADDRESS_LINE_2, "entityTypes.addresses.line2" );
		criteriaConversion.put( KIMPropertyConstants.Person.ADDRESS_LINE_3, "entityTypes.addresses.line3" );
		criteriaConversion.put( KIMPropertyConstants.Person.CITY_NAME, "entityTypes.addresses.cityName" );
		criteriaConversion.put( KIMPropertyConstants.Person.STATE_CODE, "entityTypes.addresses.stateCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.POSTAL_CODE, "entityTypes.addresses.postalCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.COUNTRY_CODE, "entityTypes.addresses.countryCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.CAMPUS_CODE, "affiliations.campusCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.AFFILIATION_TYPE_CODE, "affiliations.affiliationTypeCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EXTERNAL_IDENTIFIER_TYPE_CODE, "externalIdentifiers.externalIdentifierTypeCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EXTERNAL_ID, "externalIdentifiers.externalId" );		
		criteriaConversion.put( KIMPropertyConstants.Person.EMPLOYEE_TYPE_CODE, "employmentInformation.employeeTypeCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EMPLOYEE_STATUS_CODE, "employmentInformation.employeeStatusCode" );
		criteriaConversion.put( KIMPropertyConstants.Person.EMPLOYEE_ID, "employmentInformation.employeeId" );
		criteriaConversion.put( KIMPropertyConstants.Person.BASE_SALARY_AMOUNT, "employmentInformation.baseSalaryAmount" );
		criteriaConversion.put( KIMPropertyConstants.Person.PRIMARY_DEPARTMENT_CODE, "employmentInformation.primaryDepartmentCode" );

		personCachePropertyNames.add( KIMPropertyConstants.Person.PRINCIPAL_ID );
		personCachePropertyNames.add( KIMPropertyConstants.Person.PRINCIPAL_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.ENTITY_ID );
		personCachePropertyNames.add( KIMPropertyConstants.Person.FIRST_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.LAST_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.MIDDLE_NAME );
		personCachePropertyNames.add( KIMPropertyConstants.Person.CAMPUS_CODE );
		personCachePropertyNames.add( KIMPropertyConstants.Person.EMPLOYEE_ID );
		personCachePropertyNames.add( KIMPropertyConstants.Person.PRIMARY_DEPARTMENT_CODE );
	}

	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#getPerson(java.lang.String)
	 */
	public PersonImpl getPerson(String principalId) {
		if ( StringUtils.isBlank(principalId) ) {
			return null;
		}
		PersonImpl person = null;
		// check the cache		
		person = getPersonImplFromPrincipalIdCache( principalId );
		if ( person != null ) {
			return person;
		}
		KimEntityDefaultInfo entity = null;
		// get the corresponding principal
		KimPrincipal principal = getIdentityManagementService().getPrincipal( principalId );
		// get the entity
		if ( principal != null ) {
			entity = getIdentityManagementService().getEntityDefaultInfo( principal.getEntityId() );
		}
		// convert the principal and entity to a Person
		// skip if the person was created from the DB cache
		if ( person == null && entity != null ) {
			person = convertEntityToPerson( entity, principal );
			addPersonImplToCache( person );
		}
		return person;
	}

	protected PersonImpl convertEntityToPerson( KimEntityDefaultInfo entity, KimPrincipal principal ) {
		try {
			// get the EntityEntityType for the EntityType corresponding to a Person
			for ( String entityTypeCode : personEntityTypeCodes ) {
				KimEntityEntityTypeDefaultInfo entType = entity.getEntityType( entityTypeCode );
				// if no "person" entity type present for the given principal, skip to the next type in the list
				if ( entType == null ) {
					continue;
				}
				// attach the principal and entity objects
				// PersonImpl has logic to pull the needed elements from the KimEntity-related classes
				return new PersonImpl( principal, entity, entityTypeCode );
			}
			return null;
		} catch ( Exception ex ) {
			// allow runtime exceptions to pass through
			if ( ex instanceof RuntimeException ) {
				throw (RuntimeException)ex;
			} else {
				throw new RuntimeException( "Problem building person object", ex );
			}
		}
	}
	
	protected PersonImpl getPersonImplFromPrincipalNameCache( String principalName ) {
		SoftReference<PersonImpl> personRef = personCache.get( "principalName="+principalName );
		if ( personRef != null ) {
			return personRef.get();
		}
		return null;
	}

	protected PersonImpl getPersonImplFromPrincipalIdCache( String principalId ) {
		SoftReference<PersonImpl> personRef = personCache.get( "principalId="+principalId );
		if ( personRef != null ) {
			return personRef.get();
		}
		return null;
	}
	
	protected PersonImpl getPersonImplFromEmployeeIdCache( String principalId ) {
		SoftReference<PersonImpl> personRef = personCache.get( "employeeId="+principalId );
		if ( personRef != null ) {
			return personRef.get();
		}
		return null;
	}
	
	protected void addPersonImplToCache( PersonImpl person ) {
		if ( person != null ) {
			synchronized (personCache) {
				personCache.put( "principalName="+person.getPrincipalName(), new MaxAgeSoftReference<PersonImpl>( personCacheMaxAgeSeconds, person ) );
				personCache.put( "principalId="+person.getPrincipalId(), new MaxAgeSoftReference<PersonImpl>( personCacheMaxAgeSeconds, person ) );
				personCache.put( "employeeId="+person.getEmployeeId(), new MaxAgeSoftReference<PersonImpl>( personCacheMaxAgeSeconds, person ) );
			}
		}
	}
	
	public void flushPersonCaches() {
	    personCache.clear();
	}
	
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#getPersonByPrincipalName(java.lang.String)
	 */
	public PersonImpl getPersonByPrincipalName(String principalName) {
		if ( StringUtils.isBlank(principalName) ) {
			return null;
		}
		PersonImpl person = null;
		// check the cache		
		person = getPersonImplFromPrincipalNameCache( principalName );
		if ( person != null ) {
			return person;
		}
		KimEntityDefaultInfo entity = null;
		// get the corresponding principal
		KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName( principalName );
		// get the entity
		if ( principal != null ) {
			entity = getIdentityManagementService().getEntityDefaultInfo( principal.getEntityId() );
		}
		// convert the principal and entity to a Person
		if ( entity != null ) {
			person = convertEntityToPerson( entity, principal );
		}
		addPersonImplToCache( person );
		return person;
	}

	public PersonImpl getPersonByEmployeeId(String employeeId) {
		if ( StringUtils.isBlank( employeeId  ) ) {
			return null;
		}
		
		PersonImpl person = getPersonImplFromEmployeeIdCache( employeeId );
		if ( person != null ) {
			return person;
		}
		
		Map<String,String> criteria = new HashMap<String,String>( 1 );
		criteria.put( KIMPropertyConstants.Person.EMPLOYEE_ID, employeeId );
		List<PersonImpl> people = findPeople( criteria ); 
		if ( !people.isEmpty() ) {
			person = people.get(0);
			addPersonImplToCache( person );
		}
		return person;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#findPeople(Map)
	 */
	public List<PersonImpl> findPeople(Map<String, String> criteria) {
		return findPeople(criteria, true);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#findPeople(java.util.Map, boolean)
	 */
	public List<PersonImpl> findPeople(Map<String, String> criteria, boolean unbounded) {
		List<PersonImpl> people = null;
		// protect from NPEs
		if ( criteria == null ) {
			criteria = Collections.emptyMap();
		}
		// make a copy so it can be modified safely in this method
		criteria = new HashMap<String, String>( criteria );
		
		// extract the role lookup parameters and then remove them since later code will not know what to do with them
		String roleName = criteria.get( "lookupRoleName" );
		String namespaceCode = criteria.get( "lookupRoleNamespaceCode" );
		criteria.remove("lookupRoleName");
		criteria.remove("lookupRoleNamespaceCode");
		if ( StringUtils.isNotBlank(namespaceCode) && StringUtils.isNotBlank(roleName) ) {
			int searchResultsLimit = LookupUtils.getSearchResultsLimit(PersonImpl.class);
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("Performing Person search including role filter: " + namespaceCode + "/" + roleName );
			}
			if ( criteria.size() == 1 && criteria.containsKey(KIMPropertyConstants.Person.ACTIVE) ) { // if only active is specified
				if ( LOG.isDebugEnabled() ) {
					LOG.debug( "Only active criteria specified, running role search first" );
				}
				// in this case, run the role lookup first and pass those results to the person lookup
				Collection<String> principalIds = getRoleManagementService().getRoleMemberPrincipalIds(namespaceCode, roleName, null);
				StringBuffer sb = new StringBuffer(principalIds.size()*15);
				Iterator<String> pi = principalIds.iterator();
				while ( pi.hasNext() ) {
					sb.append( pi.next() );
					if ( pi.hasNext() ) sb.append( '|' );
				}
				// add the list of principal IDs to the lookup so that only matching Person objects are returned
				criteria.put( KIMPropertyConstants.Person.PRINCIPAL_ID, sb.toString() );
				people = findPeopleInternal(criteria, false); // can allow internal method to filter here since no more filtering necessary				
			} else if ( !criteria.isEmpty() ) { // i.e., person criteria are specified
				if ( LOG.isDebugEnabled() ) {
					LOG.debug( "Person criteria also specified, running that search first" );
				}
				// run the person lookup first
				people = findPeopleInternal(criteria, true); // get all, since may need to be filtered
				// TODO - now check if these people have the given role
				// build a principal list
				List<String> principalIds = peopleToPrincipalIds( people );
				// get sublist of principals that have the given roles
				principalIds = getRoleManagementService().getPrincipalIdSubListWithRole(principalIds, namespaceCode, roleName, null);
				// re-convert into people objects, wrapping in CollectionIncomplete if needed
				if ( !unbounded && principalIds.size() > searchResultsLimit ) {
					int actualResultSize = principalIds.size();
					// trim the list down before converting to people
					principalIds = new ArrayList<String>(principalIds).subList(0, searchResultsLimit); // yes, this is a little wasteful
					people = getPeople(principalIds); // convert the results to people
					people = new CollectionIncomplete<PersonImpl>( people.subList(0, searchResultsLimit), new Long(actualResultSize) );
				} else {
					people = getPeople(principalIds);
				}
			} else { // only role criteria specified
				if ( LOG.isDebugEnabled() ) {
					LOG.debug( "No Person criteria specified - only using role service." );
				}
				// run the role criteria to get the principals with the role
				Collection<String> principalIds = getRoleManagementService().getRoleMemberPrincipalIds(namespaceCode, roleName, null);
				if ( !unbounded && principalIds.size() > searchResultsLimit ) {
					int actualResultSize = principalIds.size();
					// trim the list down before converting to people
					principalIds = new ArrayList<String>(principalIds).subList(0, searchResultsLimit); // yes, this is a little wasteful
					people = getPeople(principalIds); // convert the results to people
					people = new CollectionIncomplete<PersonImpl>( people.subList(0, searchResultsLimit), new Long(actualResultSize) );
				} else {
					people = getPeople(principalIds); // convert the results to people
				}
			}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "No Role criteria specified, running person lookup as normal." );
			}
			people = findPeopleInternal(criteria, unbounded);
		}
		return people;
	}
	
	@SuppressWarnings("unchecked")
	protected List<PersonImpl> findPeopleInternal(Map<String,String> criteria, boolean unbounded ) {
		// convert the criteria to a form that can be used by the ORM layer
		Map<String,String> entityCriteria = convertPersonPropertiesToEntityProperties( criteria );

		List<PersonImpl> people = new ArrayList<PersonImpl>(); 

		List<? extends KimEntityDefaultInfo> entities = getIdentityManagementService().lookupEntityDefaultInfo( entityCriteria, unbounded );

		for ( KimEntityDefaultInfo e : entities ) {
			// get to get all principals for the entity as well
			for ( KimPrincipal p : e.getPrincipals() ) {
				people.add( convertEntityToPerson( e, p ) );
			}
		}
		
		if ( entities instanceof CollectionIncomplete ) {
			return new CollectionIncomplete( people, ((CollectionIncomplete)entities).getActualSizeIfTruncated() );
		}
		return people;
	}

	public Map<String,String> convertPersonPropertiesToEntityProperties( Map<String,String> criteria ) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "convertPersonPropertiesToEntityProperties: " + criteria );
		}
		boolean nameCriteria = false;
		boolean addressCriteria = false;
		boolean externalIdentifierCriteria = false;
		boolean affiliationCriteria = false;
		boolean affiliationDefaultOnlyCriteria = false;
		boolean phoneCriteria = false;
		boolean emailCriteria = false;
		boolean employeeIdCriteria = false;
		// add base lookups for all person lookups
		HashMap<String,String> newCriteria = new HashMap<String,String>();
		newCriteria.putAll( baseLookupCriteria );
		newCriteria.put( "entityTypes.entityTypeCode", personEntityTypeLookupCriteria );
		if ( criteria != null ) {
			for ( String key : criteria.keySet() ) {
						
				//check active radio button
				if(key.equals(KIMPropertyConstants.Person.ACTIVE)) {
					newCriteria.put(KIMPropertyConstants.Person.ACTIVE, criteria.get(KIMPropertyConstants.Person.ACTIVE));
				}
			
				// if no value was passed, skip the entry in the Map
				if ( StringUtils.isEmpty( criteria.get(key) ) ) {
					continue;
				}
				// check if the value needs to be encrypted
				// handle encrypted external identifiers
				if ( key.equals( KIMPropertyConstants.Person.EXTERNAL_ID ) && StringUtils.isNotBlank(criteria.get(key)) ) {
					// look for a ext ID type property
					if ( criteria.containsKey( KIMPropertyConstants.Person.EXTERNAL_IDENTIFIER_TYPE_CODE ) ) {
						String extIdTypeCode = criteria.get(KIMPropertyConstants.Person.EXTERNAL_IDENTIFIER_TYPE_CODE);
						if ( StringUtils.isNotBlank(extIdTypeCode) ) {
							// if found, load that external ID Type via service
							ExternalIdentifierTypeInfo extIdType = getIdentityManagementService().getExternalIdentifierType(extIdTypeCode);
							// if that type needs to be encrypted, encrypt the value in the criteria map
							if ( extIdType != null && extIdType.isEncryptionRequired() ) {
								try {
									criteria.put(key, 
											KNSServiceLocator.getEncryptionService().encrypt(criteria.get(key))
											);
								} catch (GeneralSecurityException ex) {
									LOG.error("Unable to encrypt value for external ID search of type " + extIdTypeCode, ex );
								}								
							}
						}
					}
				}
				
				// convert the property to the Entity data model
				String entityProperty = criteriaConversion.get( key );
				if ( entityProperty != null ) {
					newCriteria.put( entityProperty, criteria.get( key ) );
				} else {
					entityProperty = key;
					// just pass it through if no translation present
					newCriteria.put( key, criteria.get( key ) );
				}
				// check if additional criteria are needed based on the types of properties specified
				if ( isNameEntityCriteria( entityProperty ) ) {
					nameCriteria = true;
				}
				if ( isExternalIdentifierEntityCriteria( entityProperty ) ) {
					externalIdentifierCriteria = true;
				}
				if ( isAffiliationEntityCriteria( entityProperty ) ) {
					affiliationCriteria = true;
				}
				if ( isAddressEntityCriteria( entityProperty ) ) {
					addressCriteria = true;
				}
				if ( isPhoneEntityCriteria( entityProperty ) ) {
					phoneCriteria = true;
				}
				if ( isEmailEntityCriteria( entityProperty ) ) {
					emailCriteria = true;
				}
				if ( isEmployeeIdEntityCriteria( entityProperty ) ) {
					employeeIdCriteria = true;
				}				
				// special handling for the campus code, since that forces the query to look
				// at the default affiliation record only
				if ( key.equals( "campusCode" ) ) {
					affiliationDefaultOnlyCriteria = true;
				}
			}		
			if ( nameCriteria ) {
				newCriteria.put( ENTITY_NAME_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_NAME_PROPERTY_PREFIX + "dflt", "Y" );
				//newCriteria.put(ENTITY_NAME_PROPERTY_PREFIX + "nameTypeCode", "PRFR");//so we only display 1 result
			}
			if ( addressCriteria ) {
				newCriteria.put( ENTITY_ADDRESS_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_ADDRESS_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( phoneCriteria ) {
				newCriteria.put( ENTITY_PHONE_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_PHONE_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( emailCriteria ) {
				newCriteria.put( ENTITY_EMAIL_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_EMAIL_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( employeeIdCriteria ) {
				newCriteria.put( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX + "active", "Y" );
				newCriteria.put( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX + "primary", "Y" );
			}
			if ( affiliationCriteria ) {
				newCriteria.put( ENTITY_AFFILIATION_PROPERTY_PREFIX + "active", "Y" );
			}
			if ( affiliationDefaultOnlyCriteria ) {
				newCriteria.put( ENTITY_AFFILIATION_PROPERTY_PREFIX + "dflt", "Y" );
			}
			if ( externalIdentifierCriteria ) {
				newCriteria.put( ENTITY_EXT_ID_PROPERTY_PREFIX + "active", "Y" );
			}
		}
		
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Converted: " + newCriteria );
		}
		return newCriteria;		
	}

	protected boolean isNameEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_NAME_PROPERTY_PREFIX );
	}
	protected boolean isAddressEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_ADDRESS_PROPERTY_PREFIX );
	}
	protected boolean isPhoneEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_PHONE_PROPERTY_PREFIX );
	}
	protected boolean isEmailEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_EMAIL_PROPERTY_PREFIX );
	}
	protected boolean isEmployeeIdEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_EMPLOYEE_ID_PROPERTY_PREFIX );
	}
	protected boolean isAffiliationEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_AFFILIATION_PROPERTY_PREFIX );
	}
	protected boolean isExternalIdentifierEntityCriteria( String propertyName ) {
		return propertyName.startsWith( ENTITY_EXT_ID_PROPERTY_PREFIX );
	}
	
	/**
	 * Get the entityTypeCode that can be associated with a Person.  This will determine
	 * where EntityType-related data is pulled from within the KimEntity object.  The codes
	 * in the list will be examined in the order present.
	 */
	public List<String> getPersonEntityTypeCodes() {
		return this.personEntityTypeCodes;
	}

	public void setPersonEntityTypeCodes(List<String> personEntityTypeCodes) {
		this.personEntityTypeCodes = personEntityTypeCodes;
		personEntityTypeLookupCriteria = null;
		for ( String entityTypeCode : personEntityTypeCodes ) {
			if ( personEntityTypeLookupCriteria == null ) {
				personEntityTypeLookupCriteria = entityTypeCode;
			} else {
				personEntityTypeLookupCriteria = personEntityTypeLookupCriteria + "|" + entityTypeCode;
			}
		}
	}

	
	protected List<PersonImpl> getPeople( Collection<String> principalIds ) {
		List<PersonImpl> people = new ArrayList<PersonImpl>( principalIds.size() );
		for ( String principalId : principalIds ) {
			people.add( getPerson(principalId) );
		}
		return people;
	}
	
	protected List<String> peopleToPrincipalIds( List<? extends Person> people ) {
		List<String> principalIds = new ArrayList<String>();
		
		for ( Person person : people ) {
			principalIds.add( person.getPrincipalId() );
		}
		
		return principalIds;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#getPersonByExternalIdentifier(java.lang.String, java.lang.String)
	 */
	public List<PersonImpl> getPersonByExternalIdentifier(String externalIdentifierTypeCode, String externalId) {
		if (StringUtils.isBlank( externalIdentifierTypeCode ) || StringUtils.isBlank( externalId ) ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put( KIMPropertyConstants.Person.EXTERNAL_IDENTIFIER_TYPE_CODE, externalIdentifierTypeCode );
		criteria.put( KIMPropertyConstants.Person.EXTERNAL_ID, externalId );
		return findPeople( criteria );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PersonService#updatePersonIfNecessary(java.lang.String, org.kuali.rice.kim.bo.Person)
	 */
    public Person updatePersonIfNecessary(String sourcePrincipalId, Person currentPerson ) {
        if (currentPerson  == null // no person set
                || !StringUtils.equals(sourcePrincipalId, currentPerson.getPrincipalId() ) // principal ID mismatch
                || currentPerson.getEntityId() == null ) { // syntheticially created Person object
            Person person = getPerson( sourcePrincipalId );
            // if a synthetically created person object is present, leave it - required for property derivation and the UI layer for
            // setting the principal name
            if ( person == null ) {
                if ( currentPerson != null && currentPerson.getEntityId() == null ) {
                    return currentPerson;
                }
            }
            // if both are null, create an empty object for property derivation
            if ( person == null && currentPerson == null ) {
            	try {
            		return new PersonImpl();
            	} catch ( Exception ex ) {
            		LOG.error( "unable to instantiate an object of type: " + getPersonImplementationClass() + " - returning null", ex );
            		return null;
            	}
            }
            return person;
        }
        // otherwise, no need to change the given object
        return currentPerson;
    }

    /**
     * Builds a map containing entries from the passed in Map that do NOT represent properties on an embedded
     * Person object.
     */
    private Map<String,String> getNonPersonSearchCriteria( BusinessObject bo, Map<String,String> fieldValues) {
        Map<String,String> nonUniversalUserSearchCriteria = new HashMap<String,String>();
        for ( String propertyName : fieldValues.keySet() ) {
            if (!isPersonProperty(bo, propertyName)) {
                nonUniversalUserSearchCriteria.put(propertyName, fieldValues.get(propertyName));
            }
        }
        return nonUniversalUserSearchCriteria;
    }


    private boolean isPersonProperty(BusinessObject bo, String propertyName) {
        try {
        	if ( ObjectUtils.isNestedAttribute( propertyName ) // is a nested property
            		&& !StringUtils.contains(propertyName, "add.") ) {// exclude add line properties (due to path parsing problems in PropertyUtils.getPropertyType)
        		Class<?> type = PropertyUtils.getPropertyType(bo, ObjectUtils.getNestedAttributePrefix( propertyName ));
        		// property type indicates a Person object
        		if ( type != null ) {
        			return Person.class.isAssignableFrom(type);
        		} else {
        			LOG.warn( "Unable to determine type of nested property: " + bo.getClass().getName() + " / " + propertyName );
        		}
        	}
        } catch (Exception ex) {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug("Unable to determine if property on " + bo.getClass().getName() + " to a person object: " + propertyName, ex );
        	}
        }
        return false;
    }
    
    /**
     * @see org.kuali.rice.kim.service.PersonService#hasPersonProperty(java.lang.Class, java.util.Map)
     */
    public boolean hasPersonProperty(Class<? extends BusinessObject> businessObjectClass, Map<String,String> fieldValues) {
    	if ( businessObjectClass == null || fieldValues == null ) {
    		return false;
    	}
    	try {
	    	BusinessObject bo = businessObjectClass.newInstance();
	        for ( String propertyName : fieldValues.keySet() ) {
	            if (isPersonProperty(bo, propertyName)) {
	            	return true;
	            }
	        }
    	} catch (Exception ex) {
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Error instantiating business object class passed into hasPersonProperty", ex );
    		}
			// do nothing
		}
        return false;
    }    

    /**
     * @see org.kuali.rice.kim.service.PersonService#resolvePrincipalNamesToPrincipalIds(org.kuali.rice.kns.bo.BusinessObject, java.util.Map)
     */
    @SuppressWarnings("unchecked")
	public Map<String,String> resolvePrincipalNamesToPrincipalIds(BusinessObject businessObject, Map<String,String> fieldValues) {
    	if ( fieldValues == null ) {
    		return null;
    	}
    	if ( businessObject == null ) {
    		return fieldValues;
    	}
    	StringBuffer resolvedPrincipalIdPropertyName = new StringBuffer();
    	// save off all criteria which are not references to Person properties
    	// leave person properties out so they can be resolved and replaced by this method
        Map<String,String> processedFieldValues = getNonPersonSearchCriteria(businessObject, fieldValues);
        for ( String propertyName : fieldValues.keySet() ) {        	
            if (	!StringUtils.isBlank(fieldValues.get(propertyName))  // property has a value
            		&& isPersonProperty(businessObject, propertyName) // is a property on a Person object
            		) {
            	// strip off the prefix on the property
                String personPropertyName = ObjectUtils.getNestedAttributePrimitive( propertyName );
                // special case - the user ID 
                if ( StringUtils.equals( KIMPropertyConstants.Person.PRINCIPAL_NAME, personPropertyName) ) {
                    Class targetBusinessObjectClass = null;
                    BusinessObject targetBusinessObject = null;
                    resolvedPrincipalIdPropertyName.setLength( 0 ); // clear the buffer without requiring a new object allocation on each iteration
                	// get the property name up until the ".principalName"
                	// this should be a reference to the Person object attached to the BusinessObject                	
                	String personReferenceObjectPropertyName = ObjectUtils.getNestedAttributePrefix( propertyName );
                	// check if the person was nested within another BO under the master BO.  If so, go up one more level
                	// otherwise, use the passed in BO class as the target class
                    if ( ObjectUtils.isNestedAttribute( personReferenceObjectPropertyName ) ) {
                        String targetBusinessObjectPropertyName = ObjectUtils.getNestedAttributePrefix( personReferenceObjectPropertyName );
                        targetBusinessObject = (BusinessObject)ObjectUtils.getPropertyValue( businessObject, targetBusinessObjectPropertyName );
                        if (targetBusinessObject != null) {
                            targetBusinessObjectClass = targetBusinessObject.getClass();
                            resolvedPrincipalIdPropertyName.append(targetBusinessObjectPropertyName).append(".");
                        } else {
                            LOG.error("Could not find target property '"+propertyName+"' in class "+businessObject.getClass().getName()+". Property value was null.");
                        }
                    } else { // not a nested Person property
                        targetBusinessObjectClass = businessObject.getClass();
                        targetBusinessObject = businessObject;
                    }
                    
                    if (targetBusinessObjectClass != null) {
                    	// use the relationship metadata in the KNS to determine the property on the
                    	// host business object to put back into the map now that the principal ID
                    	// (the value stored in application tables) has been resolved
                        String propName = ObjectUtils.getNestedAttributePrimitive( personReferenceObjectPropertyName );
                        BusinessObjectRelationship rel = getBusinessObjectMetaDataService().getBusinessObjectRelationship( targetBusinessObject, propName );
                        if ( rel != null ) {
                            String sourcePrimitivePropertyName = rel.getParentAttributeForChildAttribute(KIMPropertyConstants.Person.PRINCIPAL_ID);
                            resolvedPrincipalIdPropertyName.append(sourcePrimitivePropertyName);
                        	// get the principal - for translation of the principalName to principalId
                            String principalName = fieldValues.get( propertyName );
                        	KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName( principalName );
                            if (principal != null ) {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), principal.getPrincipalId());
                            } else {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                                try {
                                    // if the principalName is bad, then we need to clear out the Person object
                                    // and base principalId property
                                    // so that their values are no longer accidentally used or re-populate
                                    // the object
                                    ObjectUtils.setObjectProperty(targetBusinessObject, resolvedPrincipalIdPropertyName.toString(), null );
                                    ObjectUtils.setObjectProperty(targetBusinessObject, propName, null );
                                    ObjectUtils.setObjectProperty(targetBusinessObject, propName + ".principalName", principalName );
                                } catch ( Exception ex ) {
                                    LOG.error( "Unable to blank out the person object after finding that the person with the given principalName does not exist.", ex );
                                }
                            }
                        } else {
                        	LOG.error( "Missing relationship for " + propName + " on " + targetBusinessObjectClass.getName() );
                        }
                    } else { // no target BO class - the code below probably will not work
                        processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                    }
                }
            // if the property does not seem to match the definition of a Person property but it
            // does end in principalName then...
            // this is to handle the case where the user ID is on an ADD line - a case excluded from isPersonProperty()
            } else if (propertyName.endsWith("." + KIMPropertyConstants.Person.PRINCIPAL_NAME)){
                // if we're adding to a collection and we've got the principalName; let's populate universalUser
                String principalName = fieldValues.get(propertyName);
                if ( StringUtils.isNotEmpty( principalName ) ) {
                    String containerPropertyName = propertyName;
                    if (containerPropertyName.startsWith(KNSConstants.MAINTENANCE_ADD_PREFIX)) {
                        containerPropertyName = StringUtils.substringAfter( propertyName, KNSConstants.MAINTENANCE_ADD_PREFIX );
                    }
                    // get the class of the object that is referenced by the property name
                    // if this is not true then there's a principalName collection or primitive attribute 
                    // directly on the BO on the add line, so we just ignore that since something is wrong here
                    if ( ObjectUtils.isNestedAttribute( containerPropertyName ) ) {
                    	// the first part of the property is the collection name
                        String collectionName = StringUtils.substringBefore( containerPropertyName, "." );
                        // what is the class held by that collection?
                        // JHK: I don't like this.  This assumes that this method is only used by the maintenance
                        // document service.  If that will always be the case, this method should be moved over there.
                        Class<? extends BusinessObject> collectionBusinessObjectClass = getMaintenanceDocumentDictionaryService()
                        		.getCollectionBusinessObjectClass(
                        				getMaintenanceDocumentDictionaryService()
                        						.getDocumentTypeName(businessObject.getClass()), collectionName);
                        if (collectionBusinessObjectClass != null) {
                            // we are adding to a collection; get the relationships for that object; 
                        	// is there one for personUniversalIdentifier?
                            List<BusinessObjectRelationship> relationships = 
                            		getBusinessObjectMetaDataService().getBusinessObjectRelationships( collectionBusinessObjectClass );
                            // JHK: this seems like a hack - looking at all relationships for a BO does not guarantee that we get the right one
                            // JHK: why not inspect the objects like above?  Is it the property path problems because of the .add. portion?
                            for ( BusinessObjectRelationship rel : relationships ) {
                            	String parentAttribute = rel.getParentAttributeForChildAttribute( KIMPropertyConstants.Person.PRINCIPAL_ID );
                            	if ( parentAttribute == null ) {
                            		continue;
                            	}
                                // there is a relationship for personUserIdentifier; use that to find the universal user
                            	processedFieldValues.remove( propertyName );
                        		String fieldPrefix = StringUtils.substringBeforeLast( StringUtils.substringBeforeLast( propertyName, "." + KIMPropertyConstants.Person.PRINCIPAL_NAME ), "." );
                                String relatedPrincipalIdPropertyName = fieldPrefix + "." + parentAttribute;
                                // KR-683 Special handling for extension objects
                         	 	if(EXTENSION.equals(StringUtils.substringAfterLast(fieldPrefix, ".")) && EXTENSION.equals(StringUtils.substringBefore(parentAttribute, ".")))
                         	 	{
                         	 		relatedPrincipalIdPropertyName = fieldPrefix + "." + StringUtils.substringAfter(parentAttribute, ".");
                         	 	}
                                String currRelatedPersonPrincipalId = processedFieldValues.get(relatedPrincipalIdPropertyName);
                                if ( StringUtils.isBlank( currRelatedPersonPrincipalId ) ) {
                                	KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName( principalName );
                                	if ( principal != null ) {
                                		processedFieldValues.put(relatedPrincipalIdPropertyName, principal.getPrincipalId());
                                	} else {
                                		processedFieldValues.put(relatedPrincipalIdPropertyName, null);
                                	}
                                }
                            } // relationship loop
                        } else {
                        	if ( LOG.isDebugEnabled() ) {
                        		LOG.debug( "Unable to determine class for collection referenced as part of property: " + containerPropertyName + " on " + businessObject.getClass().getName() );
                        	}
                        }
                    } else {
                    	if ( LOG.isDebugEnabled() ) {
                    		LOG.debug( "Non-nested property ending with 'principalName': " + containerPropertyName + " on " + businessObject.getClass().getName() );
                    	}
                    }
                }
            }
        }
        return processedFieldValues;
    }
	
	// OTHER METHODS

	protected IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}

	protected RoleManagementService getRoleManagementService() {
		if ( roleManagementService == null ) {
			roleManagementService = KIMServiceLocator.getRoleManagementService();
		}
		return roleManagementService;
	}


	public Class<? extends Person> getPersonImplementationClass() {
		return PersonImpl.class;
	}
	
	protected BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
		if ( businessObjectMetaDataService == null ) {
			businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
		}
		return businessObjectMetaDataService;
	}

	protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if ( maintenanceDocumentDictionaryService == null ) {
			maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}

	public void setPersonCacheMaxSize(int personCacheMaxSize) {
		this.personCacheMaxSize = personCacheMaxSize;
	}

	public void setPersonCacheMaxAgeSeconds(int personCacheMaxAgeSeconds) {
		this.personCacheMaxAgeSeconds = personCacheMaxAgeSeconds;
	}
	
}