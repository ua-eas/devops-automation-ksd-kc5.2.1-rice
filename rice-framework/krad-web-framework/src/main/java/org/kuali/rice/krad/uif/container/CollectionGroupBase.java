/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DelayedCopy;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePrototype;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Group that holds a collection of objects and configuration for presenting the
 * collection in the UI. Supports functionality such as add line, line actions,
 * and nested collections.
 *
 * <p>
 * Note the standard header/footer can be used to give a header to the
 * collection as a whole, or to provide actions that apply to the entire
 * collection
 * </p>
 *
 * <p>
 * For binding purposes the binding path of each row field is indexed. The name
 * property inherited from <code>ComponentBase</code> is used as the collection
 * name. The collectionObjectClass property is used to lookup attributes from
 * the data dictionary.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "collectionGroup-bean", parent = "Uif-CollectionGroupBase"),
        @BeanTag(name = "stackedCollectionGroup-bean", parent = "Uif-StackedCollectionGroup"),
        @BeanTag(name = "stackedCollectionSection-bean", parent = "Uif-StackedCollectionSection"),
        @BeanTag(name = "stackedCollectionSubSection-bean", parent = "Uif-StackedCollectionSubSection"),
        @BeanTag(name = "stackedSubCollection-withinSection-bean", parent = "Uif-StackedSubCollection-WithinSection"),
        @BeanTag(name = "stackedSubCollection-withinSubSection-bean",
                parent = "Uif-StackedSubCollection-WithinSubSection"),
        @BeanTag(name = "disclosure-stackedCollectionSection-bean", parent = "Uif-Disclosure-StackedCollectionSection"),
        @BeanTag(name = "disclosure-stackedCollectionSubSection-bean",
                parent = "Uif-Disclosure-StackedCollectionSubSection"),
        @BeanTag(name = "disclosure-stackedSubCollection-withinSection-bean",
                parent = "Uif-Disclosure-StackedSubCollection-WithinSection"),
        @BeanTag(name = "disclosure-stackedSubCollection-withinSubSection-bean",
                parent = "Uif-Disclosure-StackedSubCollection-WithinSubSection"),
        @BeanTag(name = "tableCollectionGroup-bean", parent = "Uif-TableCollectionGroup"),
        @BeanTag(name = "tableCollectionSection-bean", parent = "Uif-TableCollectionSection"),
        @BeanTag(name = "tableCollectionSubSection-bean", parent = "Uif-TableCollectionSubSection"),
        @BeanTag(name = "tableSubCollection-withinSection-bean", parent = "Uif-TableSubCollection-WithinSection"),
        @BeanTag(name = "tableSubCollection-withinSubSection-bean", parent = "Uif-TableSubCollection-WithinSubSection"),
        @BeanTag(name = "disclosure-tableCollectionSection-bean", parent = "Uif-Disclosure-TableCollectionSection"),
        @BeanTag(name = "disclosure-tableCollectionSubSection-bean",
                parent = "Uif-Disclosure-TableCollectionSubSection"),
        @BeanTag(name = "disclosure-tableSubCollection-withinSection-bean",
                parent = "Uif-Disclosure-TableSubCollection-WithinSection"),
        @BeanTag(name = "disclosure-tableSubCollection-withinSubSection-bean",
                parent = "Uif-Disclosure-TableSubCollection-WithinSubSection"),
        @BeanTag(name = "listCollectionGroup-bean", parent = "Uif-ListCollectionGroup"),
        @BeanTag(name = "listCollectionSection-bean", parent = "Uif-ListCollectionSection"),
        @BeanTag(name = "listCollectionSubSection-bean", parent = "Uif-ListCollectionSubSection"),
        @BeanTag(name = "documentNotesSection-bean", parent = "Uif-DocumentNotesSection"),
        @BeanTag(name = "lookupResultsCollectionSection-bean", parent = "Uif-LookupResultsCollectionSection"),
        @BeanTag(name = "maintenanceStackedCollectionSection-bean", parent = "Uif-MaintenanceStackedCollectionSection"),
        @BeanTag(name = "maintenanceStackedSubCollection-withinSection-bean",
                parent = "Uif-MaintenanceStackedSubCollection-WithinSection"),
        @BeanTag(name = "maintenanceTableCollectionSection-bean", parent = "Uif-MaintenanceTableCollectionSection"),
        @BeanTag(name = "maintenanceTableSubCollection-withinSection-bean",
                parent = "Uif-MaintenanceTableSubCollection-WithinSection")})
public class CollectionGroupBase extends GroupBase implements CollectionGroup {
    private static final long serialVersionUID = -6496712566071542452L;

    private Class<?> collectionObjectClass;

    private String propertyName;
    private BindingInfo bindingInfo;

    private boolean renderAddLine;
    private String addLinePropertyName;
    private BindingInfo addLineBindingInfo;

    private Message addLineLabel;
    @DelayedCopy
    private List<? extends Component> addLineItems;
    private List<? extends Component> addLineActions;

    private boolean renderLineActions;
    private List<? extends Component> lineActions;
    
    private boolean includeLineSelectionField;
    private String lineSelectPropertyName;

    private QuickFinder collectionLookup;

    private boolean renderInactiveToggleButton;
    @ClientSideState(variableName = "inactive")
    private boolean showInactiveLines;
    private CollectionFilter activeCollectionFilter;
    private List<CollectionFilter> filters;

    private List<String> duplicateLinePropertyNames;

    private List<BindingInfo> unauthorizedLineBindingInfos;

    @DelayedCopy
    private List<CollectionGroup> subCollections;
    private String subCollectionSuffix;

    private CollectionGroupBuilder collectionGroupBuilder;

    private int displayCollectionSize = -1;

    private boolean highlightNewItems;
    private boolean highlightAddItem;
    private String newItemsCssClass;
    private String addItemCssClass;

    private boolean renderAddBlankLineButton;
    private Action addBlankLineAction;
    private String addLinePlacement;

    private boolean renderSaveLineActions;
    private boolean addViaLightBox;
    private Action addViaLightBoxAction;

    private boolean useServerPaging = false;
    private int pageSize;
    private int displayStart = -1;
    private int displayLength = -1;
    private int filteredCollectionSize = -1;

    private List<String> totalColumns;

    public CollectionGroupBase() {
        renderAddLine = true;
        renderLineActions = true;
        renderInactiveToggleButton = true;
        highlightNewItems = true;
        highlightAddItem = true;
        addLinePlacement = "TOP";

        filters = Collections.emptyList();
        duplicateLinePropertyNames = Collections.emptyList();
        lineActions = Collections.emptyList();
        addLineItems = Collections.emptyList();
        addLineActions = Collections.emptyList();
        subCollections = Collections.emptyList();
    }

    /**
     * Do not process remote field holders for collections. Collection items will be processed as
     * the lines are built
     * 
     * @see org.kuali.rice.krad.uif.container.ContainerBase#isProcessRemoteFieldHolders()
     */
    @Override
    public boolean isProcessRemoteFieldHolders() {
        return false;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        setFieldBindingObjectPath(getBindingInfo().getBindingObjectPath());

        super.performInitialization(model);

        View view = ViewLifecycle.getView();
        
        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }

        if (addLineBindingInfo != null) {
            // add line binds to model property
            if (StringUtils.isNotBlank(addLinePropertyName)) {
                addLineBindingInfo.setDefaults(view, getPropertyName());
                addLineBindingInfo.setBindingName(addLinePropertyName);
                if (StringUtils.isNotBlank(getFieldBindByNamePrefix())) {
                    addLineBindingInfo.setBindByNamePrefix(getFieldBindByNamePrefix());
                }
            }
        }

        for (Component item : getItems()) {
            if (item instanceof DataField) {
                DataField field = (DataField) item;

                if (StringUtils.isBlank(field.getDictionaryObjectEntry())) {
                    field.setDictionaryObjectEntry(collectionObjectClass.getName());
                }
            }
        }

        if ((addLineItems == null) || addLineItems.isEmpty()) {
            addLineItems = getItems();
        } else {
            for (Component addLineField : addLineItems) {
                if (!(addLineField instanceof DataField)) {
                    continue;
                }

                DataField field = (DataField) addLineField;

                if (StringUtils.isBlank(field.getDictionaryObjectEntry())) {
                    field.setDictionaryObjectEntry(collectionObjectClass.getName());
                }
            }
        }

        // if active collection filter not set use default
        if (this.activeCollectionFilter == null) {
            activeCollectionFilter = new ActiveCollectionFilter();
        }

        // set static collection path on items
        String collectionPath = "";
        if (StringUtils.isNotBlank(getBindingInfo().getCollectionPath())) {
            collectionPath += getBindingInfo().getCollectionPath() + ".";
        }
        if (StringUtils.isNotBlank(getBindingInfo().getBindByNamePrefix())) {
            collectionPath += getBindingInfo().getBindByNamePrefix() + ".";
        }
        collectionPath += getBindingInfo().getBindingName();

        List<DataField> collectionFields = ViewLifecycleUtils.getElementsOfTypeDeep(getItems(), DataField.class);
        for (DataField collectionField : collectionFields) {
            collectionField.getBindingInfo().setCollectionPath(collectionPath);
        }

        List<DataField> addLineCollectionFields = ViewLifecycleUtils.getElementsOfTypeDeep(addLineItems, DataField.class);
        for (DataField collectionField : addLineCollectionFields) {
            collectionField.getBindingInfo().setCollectionPath(collectionPath);
        }

        for (CollectionGroup collectionGroup : getSubCollections()) {
            collectionGroup.getBindingInfo().setCollectionPath(collectionPath);
        }

        // add collection entry to abstract classes
        if (!view.getObjectPathToConcreteClassMapping().containsKey(collectionPath)) {
            view.getObjectPathToConcreteClassMapping().put(collectionPath, getCollectionObjectClass());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        // If we are using server paging, determine if a displayStart value has been set for this collection
        // and used that value as the displayStart
        if (model instanceof UifFormBase && this.isUseServerPaging()) {
            Object displayStart = ((UifFormBase) model).getExtensionData().get(
                    this.getId() + UifConstants.PageRequest.DISPLAY_START_PROP);

            if (displayStart != null) {
                this.setDisplayStart(((Integer) displayStart).intValue());
            }
        }

        View view = ViewLifecycle.getView();
        
        // adds the script to the add line buttons to keep collection on the same page
        if (this.renderAddBlankLineButton) {
            if (this.addBlankLineAction == null) {
                this.addBlankLineAction = (Action) ComponentFactory.getNewComponentInstance(
                        ComponentFactory.ADD_BLANK_LINE_ACTION);
            }

            if (addLinePlacement.equals(UifConstants.Position.BOTTOM.name())) {
                this.addBlankLineAction.setOnClickScript("writeCurrentPageToSession(this, 'last');");
            } else {
                this.addBlankLineAction.setOnClickScript("writeCurrentPageToSession(this, 'first');");
            }
        } else if (this.addViaLightBox) {
            if (this.addViaLightBoxAction == null) {
                this.addViaLightBoxAction = (Action) ComponentFactory.getNewComponentInstance(
                        ComponentFactory.ADD_VIA_LIGHTBOX_ACTION);
            }

            if (this.addLinePlacement.equals(UifConstants.Position.BOTTOM.name())) {
                this.addViaLightBoxAction.setOnClickScript("writeCurrentPageToSession(this, 'last');");
            } else {
                this.addViaLightBoxAction.setOnClickScript("writeCurrentPageToSession(this, 'first');");
            }
        }

        pushCollectionGroupToReference();

        // if rendering the collection group, build out the lines
        if (isRender()) {
            getCollectionGroupBuilder().build(view, model, this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        boolean hasBindingPath = getBindingInfo() != null && getBindingInfo().getBindingPath() != null;
        if (hasBindingPath) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(
                    this, UifConstants.PostMetadata.BINDING_PATH, getBindingInfo().getBindingPath());
        }

        ViewLifecycle.getViewPostMetadata().addComponentPostData(
                this, UifConstants.PostMetadata.COLL_DISPLAY_START, getDisplayStart());
        ViewLifecycle.getViewPostMetadata().addComponentPostData(
                this, UifConstants.PostMetadata.COLL_DISPLAY_LENGTH, getDisplayLength());
    }
    
    /**
     * Sets a reference in the context map for all nested components to the collection group
     * instance, and sets name as parameter for an action fields in the group
     */
    public void pushCollectionGroupToReference() {
        Collection<LifecycleElement> components = ViewLifecycleUtils.getElementsForLifecycle(this).values();
        ComponentUtils.pushObjectToContext(components,
                UifConstants.ContextVariableNames.COLLECTION_GROUP, this);

        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(components, Action.class);
        for (Action action : actions) {
            action.addActionParameter(UifParameters.SELLECTED_COLLECTION_PATH, this.getBindingInfo().getBindingPath());
        }
    }

    /**
     * New collection lines are handled in the framework by maintaining a map on
     * the form. The map contains as a key the collection name, and as value an
     * instance of the collection type. An entry is created here for the
     * collection represented by the <code>CollectionGroup</code> if an instance
     * is not available (clearExistingLine will force a new instance). The given
     * model must be a subclass of <code>UifFormBase</code> in order to find the
     * Map.
     *
     * @param model Model instance that contains the new collection lines Map
     * @param clearExistingLine boolean that indicates whether the line should be set to a
     * new instance if it already exists
     */
    @Override
    public void initializeNewCollectionLine(View view, Object model, CollectionGroup collectionGroup,
            boolean clearExistingLine) {
        getCollectionGroupBuilder().initializeNewCollectionLine(view, model, collectionGroup, clearExistingLine);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @ViewLifecyclePrototype
    public List<? extends Component> getItems() {
        return super.getItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "collectionObjectClass")
    public Class<?> getCollectionObjectClass() {
        return this.collectionObjectClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollectionObjectClass(Class<?> collectionObjectClass) {
        this.collectionObjectClass = collectionObjectClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "propertyName")
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "bindingInfo", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "lineActions", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<? extends Component> getLineActions() {
        return this.lineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineActions(List<? extends Component> lineActions) {
        this.lineActions = lineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "renderLineActions")
    public boolean isRenderLineActions() {
        return this.renderLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderLineActions(boolean renderLineActions) {
        this.renderLineActions = renderLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "renderAddLine")
    public boolean isRenderAddLine() {
        return this.renderAddLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderAddLine(boolean renderAddLine) {
        this.renderAddLine = renderAddLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddLabel() {
        if (getAddLineLabel() != null) {
            return getAddLineLabel().getMessageText();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLabel(String addLabelText) {
        if (getAddLineLabel() != null) {
            getAddLineLabel().setMessageText(addLabelText);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addLineLabel", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Message getAddLineLabel() {
        return this.addLineLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineLabel(Message addLineLabel) {
        this.addLineLabel = addLineLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addLinePropertyName")
    public String getAddLinePropertyName() {
        return this.addLinePropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLinePropertyName(String addLinePropertyName) {
        this.addLinePropertyName = addLinePropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addLineBindingInfo", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public BindingInfo getAddLineBindingInfo() {
        return this.addLineBindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineBindingInfo(BindingInfo addLineBindingInfo) {
        this.addLineBindingInfo = addLineBindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction
    @BeanTagAttribute(name = "addLineItems", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<? extends Component> getAddLineItems() {
        return this.addLineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineItems(List<? extends Component> addLineItems) {
        this.addLineItems = addLineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "addLineActions", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<? extends Component> getAddLineActions() {
        return this.addLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineActions(List<? extends Component> addLineActions) {
        this.addLineActions = addLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "includeLineSelectionField")
    public boolean isIncludeLineSelectionField() {
        return includeLineSelectionField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIncludeLineSelectionField(boolean includeLineSelectionField) {
        this.includeLineSelectionField = includeLineSelectionField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "lineSelectPropertyName")
    public String getLineSelectPropertyName() {
        return lineSelectPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineSelectPropertyName(String lineSelectPropertyName) {
        this.lineSelectPropertyName = lineSelectPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "collectionLookup", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public QuickFinder getCollectionLookup() {
        return collectionLookup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollectionLookup(QuickFinder collectionLookup) {
        this.collectionLookup = collectionLookup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "showInactiveLines")
    public boolean isShowInactiveLines() {
        return showInactiveLines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShowInactiveLines(boolean showInactiveLines) {
        this.showInactiveLines = showInactiveLines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "activeCollectionFilter", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CollectionFilter getActiveCollectionFilter() {
        return activeCollectionFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveCollectionFilter(CollectionFilter activeCollectionFilter) {
        this.activeCollectionFilter = activeCollectionFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "filters", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<CollectionFilter> getFilters() {
        return filters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFilters(List<CollectionFilter> filters) {
        this.filters = filters;
    }

    /**
     *  List of property names that should be checked for duplicates in the collection.
     */
    @BeanTagAttribute(name = "duplicateLinePropertyNames", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getDuplicateLinePropertyNames() {
        return this.duplicateLinePropertyNames;
    }

    /**
     * @see CollectionGroup#getDuplicateLinePropertyNames()
     */
    public void setDuplicateLinePropertyNames(List<String> duplicateLinePropertyNames) {
        this.duplicateLinePropertyNames = duplicateLinePropertyNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BindingInfo> getUnauthorizedLineBindingInfos() {
        return this.unauthorizedLineBindingInfos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnauthorizedLineBindingInfos(List<BindingInfo> unauthorizedLineBindingInfos) {
        this.unauthorizedLineBindingInfos = unauthorizedLineBindingInfos;
    }

    /**
     * List of <code>CollectionGroup</code> instances that are sub-collections
     * of the collection represented by this collection group
     *
     * @return sub collections
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "subCollections", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<CollectionGroup> getSubCollections() {
        return this.subCollections;
    }

    /**
     * Setter for the sub collection list
     *
     * @param subCollections
     */
    @Override
    public void setSubCollections(List<CollectionGroup> subCollections) {
        this.subCollections = subCollections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubCollectionSuffix() {
        return subCollectionSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSubCollectionSuffix(String subCollectionSuffix) {
        this.subCollectionSuffix = subCollectionSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionGroupSecurity getCollectionGroupSecurity() {
        return (CollectionGroupSecurity) super.getComponentSecurity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if ((componentSecurity != null) && !(componentSecurity instanceof CollectionGroupSecurity)) {
            throw new RiceRuntimeException(
                    "Component security for CollectionGroup should be instance of CollectionGroupSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComponentSecurity() {
        if (getComponentSecurity() == null) {
            setComponentSecurity(DataObjectUtils.newInstance(CollectionGroupSecurity.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditLineAuthz() {
        initializeComponentSecurity();

        return getCollectionGroupSecurity().isEditLineAuthz();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditLineAuthz(boolean editLineAuthz) {
        initializeComponentSecurity();

        getCollectionGroupSecurity().setEditLineAuthz(editLineAuthz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isViewLineAuthz() {
        initializeComponentSecurity();

        return getCollectionGroupSecurity().isViewLineAuthz();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewLineAuthz(boolean viewLineAuthz) {
        initializeComponentSecurity();

        getCollectionGroupSecurity().setViewLineAuthz(viewLineAuthz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "collectionGroupBuilder", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CollectionGroupBuilder getCollectionGroupBuilder() {
        if (this.collectionGroupBuilder == null) {
            this.collectionGroupBuilder = new CollectionGroupBuilder();
        }
        return this.collectionGroupBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollectionGroupBuilder(CollectionGroupBuilder collectionGroupBuilder) {
        this.collectionGroupBuilder = collectionGroupBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderInactiveToggleButton(boolean renderInactiveToggleButton) {
        this.renderInactiveToggleButton = renderInactiveToggleButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "renderInactiveToggleButton")
    public boolean isRenderInactiveToggleButton() {
        return renderInactiveToggleButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "displayCollectionSize")
    public int getDisplayCollectionSize() {
        return this.displayCollectionSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayCollectionSize(int displayCollectionSize) {
        this.displayCollectionSize = displayCollectionSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "highlightNewItems")
    public boolean isHighlightNewItems() {
        return highlightNewItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHighlightNewItems(boolean highlightNewItems) {
        this.highlightNewItems = highlightNewItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "newItemsCssClass")
    public String getNewItemsCssClass() {
        return newItemsCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNewItemsCssClass(String newItemsCssClass) {
        this.newItemsCssClass = newItemsCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addItemCssClass")
    public String getAddItemCssClass() {
        return addItemCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddItemCssClass(String addItemCssClass) {
        this.addItemCssClass = addItemCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "highlightAddItem")
    public boolean isHighlightAddItem() {
        return highlightAddItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHighlightAddItem(boolean highlightAddItem) {
        this.highlightAddItem = highlightAddItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "renderAddBlankLineButton")
    public boolean isRenderAddBlankLineButton() {
        return renderAddBlankLineButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderAddBlankLineButton(boolean renderAddBlankLineButton) {
        this.renderAddBlankLineButton = renderAddBlankLineButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addBlankLineAction", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Action getAddBlankLineAction() {
        return addBlankLineAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddBlankLineAction(Action addBlankLineAction) {
        this.addBlankLineAction = addBlankLineAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addLinePlacement")
    public String getAddLinePlacement() {
        return addLinePlacement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLinePlacement(String addLinePlacement) {
        this.addLinePlacement = addLinePlacement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "renderSaveLineActions")
    public boolean isRenderSaveLineActions() {
        return renderSaveLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderSaveLineActions(boolean renderSaveLineActions) {
        this.renderSaveLineActions = renderSaveLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addViaLightBox")
    public boolean isAddViaLightBox() {
        return addViaLightBox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddViaLightBox(boolean addViaLightBox) {
        this.addViaLightBox = addViaLightBox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "addViaLightBoxAction", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Action getAddViaLightBoxAction() {
        return addViaLightBoxAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddViaLightBoxAction(Action addViaLightBoxAction) {
        this.addViaLightBoxAction = addViaLightBoxAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "useServerPaging")
    public boolean isUseServerPaging() {
        return useServerPaging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUseServerPaging(boolean useServerPaging) {
        this.useServerPaging = useServerPaging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDisplayStart() {
        return displayStart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayStart(int displayStart) {
        this.displayStart = displayStart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDisplayLength() {
        return displayLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayLength(int displayLength) {
        this.displayLength = displayLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFilteredCollectionSize() {
        return filteredCollectionSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFilteredCollectionSize(int filteredCollectionSize) {
        this.filteredCollectionSize = filteredCollectionSize;
    }

    /**
     * @return list of total columns
     */
    @BeanTagAttribute(name = "addTotalColumns")
    protected List<String> getTotalColumns() {
        return totalColumns;
    }

    /**
     * Setter for the total columns
     *
     * @param totalColumns
     */
    protected void setTotalColumns(List<String> totalColumns) {
        this.totalColumns = totalColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        CollectionGroupBase collectionGroupCopy = (CollectionGroupBase) component;

        collectionGroupCopy.setDisplayCollectionSize(this.displayCollectionSize);
        collectionGroupCopy.setActiveCollectionFilter(this.activeCollectionFilter);

        if (this.addBlankLineAction != null) {
            collectionGroupCopy.setAddBlankLineAction((Action) this.addBlankLineAction.copy());
        }

        collectionGroupCopy.setAddItemCssClass(this.addItemCssClass);

        if (addLineItems != null && !addLineItems.isEmpty()) {
            List<Component> addLineItemsCopy = ComponentUtils.copy(new ArrayList<Component>(addLineItems));
            collectionGroupCopy.setAddLineItems(addLineItemsCopy);
        }

        if (addLineActions != null && !addLineActions.isEmpty()) {
            List<? extends Component> addLineActionsCopy = ComponentUtils.copy(addLineActions);
            collectionGroupCopy.setAddLineActions(addLineActionsCopy);
        }

        if (this.addLineBindingInfo != null) {
            collectionGroupCopy.setAddLineBindingInfo((BindingInfo) this.addLineBindingInfo.copy());
        }

        if (this.addLineLabel != null) {
            collectionGroupCopy.setAddLineLabel((Message) this.addLineLabel.copy());
        }

        collectionGroupCopy.setAddLinePlacement(this.addLinePlacement);
        collectionGroupCopy.setAddLinePropertyName(this.addLinePropertyName);
        collectionGroupCopy.setAddViaLightBox(this.addViaLightBox);

        if (this.addViaLightBoxAction != null) {
            collectionGroupCopy.setAddViaLightBoxAction((Action) this.addViaLightBoxAction.copy());
        }

        if (this.bindingInfo != null) {
            collectionGroupCopy.setBindingInfo((BindingInfo) this.bindingInfo.copy());
        }

        if (this.collectionLookup != null) {
            collectionGroupCopy.setCollectionLookup((QuickFinder) this.collectionLookup.copy());
        }

        collectionGroupCopy.setCollectionObjectClass(this.collectionObjectClass);
        
        if (this.filters != null && !this.filters.isEmpty()) {
            collectionGroupCopy.setFilters(new ArrayList<CollectionFilter>(this.filters));
        }
        
        if (this.duplicateLinePropertyNames != null && !this.duplicateLinePropertyNames.isEmpty()) {
            collectionGroupCopy.setDuplicateLinePropertyNames(new ArrayList<String>(this.duplicateLinePropertyNames));
        }
        
        collectionGroupCopy.setHighlightAddItem(this.highlightAddItem);
        collectionGroupCopy.setHighlightNewItems(this.highlightNewItems);
        collectionGroupCopy.setIncludeLineSelectionField(this.includeLineSelectionField);
        collectionGroupCopy.setUseServerPaging(this.useServerPaging);
        collectionGroupCopy.setPageSize(this.pageSize);
        collectionGroupCopy.setDisplayStart(this.displayStart);
        collectionGroupCopy.setDisplayLength(this.displayLength);

        if (lineActions != null && !lineActions.isEmpty()) {
            List<? extends Component> lineActionsCopy = ComponentUtils.copy(lineActions);
            collectionGroupCopy.setLineActions(lineActionsCopy);
        }
        
        collectionGroupCopy.setLineSelectPropertyName(this.lineSelectPropertyName);
        collectionGroupCopy.setNewItemsCssClass(this.newItemsCssClass);
        collectionGroupCopy.setPropertyName(this.propertyName);
        collectionGroupCopy.setRenderAddBlankLineButton(this.renderAddBlankLineButton);
        collectionGroupCopy.setRenderAddLine(this.renderAddLine);
        collectionGroupCopy.setRenderInactiveToggleButton(this.renderInactiveToggleButton);
        collectionGroupCopy.setActiveCollectionFilter(this.activeCollectionFilter);
        collectionGroupCopy.setFilters(this.filters);
        collectionGroupCopy.setDuplicateLinePropertyNames(this.duplicateLinePropertyNames);

        collectionGroupCopy.setRenderLineActions(this.renderLineActions);
        collectionGroupCopy.setRenderSaveLineActions(this.renderSaveLineActions);
        collectionGroupCopy.setShowInactiveLines(this.showInactiveLines);

        if (this.unauthorizedLineBindingInfos != null && !this.unauthorizedLineBindingInfos.isEmpty()) {
            List<BindingInfo> unauthorizedLineBindingInfosCopy = new ArrayList<BindingInfo>();

            for (BindingInfo bindingInfo : this.unauthorizedLineBindingInfos) {
                unauthorizedLineBindingInfosCopy.add((BindingInfo) bindingInfo.copy());
            }

            collectionGroupCopy.setUnauthorizedLineBindingInfos(unauthorizedLineBindingInfosCopy);
        }

        if (subCollections != null && !subCollections.isEmpty()) {
            List<CollectionGroup> subCollectionsCopy = ComponentUtils.copy(subCollections);
            collectionGroupCopy.setSubCollections(subCollectionsCopy);
        }
        collectionGroupCopy.setSubCollectionSuffix(this.subCollectionSuffix);

        if (this.totalColumns != null) {
            collectionGroupCopy.setTotalColumns(new ArrayList<String>(this.totalColumns));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checking if collectionObjectClass is set
        if (getCollectionObjectClass() == null) {
            if (Validator.checkExpressions(this, "collectionObjectClass")) {
                String currentValues[] = {"collectionObjectClass = " + getCollectionObjectClass()};
                tracer.createWarning("CollectionObjectClass is not set (disregard if part of an abstract)",
                        currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
    
}