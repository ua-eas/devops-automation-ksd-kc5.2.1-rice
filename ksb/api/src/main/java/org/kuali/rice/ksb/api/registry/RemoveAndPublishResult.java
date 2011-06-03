package org.kuali.rice.ksb.api.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

/**
 * Wraps information resulting from a "removeAndPublish" operation on the
 * registry.  Effectively contains a list of {@link ServiceEndpoint} instances
 * for services that were published and those that were removed
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = RemoveAndPublishResult.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemoveAndPublishResult.Constants.TYPE_NAME, propOrder = {
		RemoveAndPublishResult.Elements.SERVICES_REMOVED,
		RemoveAndPublishResult.Elements.SERVICES_PUBLISHED,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class RemoveAndPublishResult implements ModelObjectComplete {

	private static final long serialVersionUID = 4279564869510247725L;

	@XmlElementWrapper(name = Elements.SERVICES_REMOVED, required = false)
	@XmlElement(name = Elements.SERVICE_REMOVED, required = false)
	private final List<ServiceEndpoint> servicesRemoved;
	
	@XmlElementWrapper(name = Elements.SERVICES_PUBLISHED, required = false)
	@XmlElement(name = Elements.SERVICE_PUBLISHED, required = false)
	private final List<ServiceEndpoint> servicesPublished;
	
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	private RemoveAndPublishResult() {
		this.servicesRemoved = null;
		this.servicesPublished = null;
	}
	
	private RemoveAndPublishResult(List<ServiceEndpoint> servicesRemoved, List<ServiceEndpoint> servicesPublished) {
		this.servicesRemoved = servicesRemoved;
		this.servicesPublished = servicesPublished;
	}
	
	public static RemoveAndPublishResult create(List<ServiceEndpoint> servicesRemoved, List<ServiceEndpoint> servicesPublished) {
		return new RemoveAndPublishResult(new ArrayList<ServiceEndpoint>(servicesRemoved),
				new ArrayList<ServiceEndpoint>(servicesPublished));
	}

	public List<ServiceEndpoint> getServicesRemoved() {
		return Collections.unmodifiableList(servicesRemoved);
	}

	public List<ServiceEndpoint> getServicesPublished() {
		return Collections.unmodifiableList(servicesPublished);
	}
	
	@Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {

    	final static String ROOT_ELEMENT_NAME = "removeAndPublishResult";
        final static String TYPE_NAME = "RemoveAndPublishResultType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };
    
    }
	
	/**
     * Exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {

        final static String SERVICES_REMOVED = "servicesRemoved";
        final static String SERVICE_REMOVED = "serviceRemoved";
        final static String SERVICES_PUBLISHED = "servicesPublished";
        final static String SERVICE_PUBLISHED = "servicePublished";

    }
    
}
