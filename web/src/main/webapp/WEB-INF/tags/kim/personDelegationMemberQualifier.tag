<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ tag body-content="empty" %>
<%@ attribute name="delegationMemberIdx" required="true" %>

<c:set var="delegationMember" value="${KualiForm.document.delegationMembers[delegationMemberIdx]}"/>
<c:set var="delegationMemberAttributes" value="${DataDictionary.RoleDocumentDelegationMember.attributes}" />
<c:set var="roleDocumentDelegationMemberQualifier" value="${DataDictionary.RoleDocumentDelegationMemberQualifier.attributes}" />

<kul:subtab lookedUpCollectionName="delegationMemberQualifier" width="${tableWidth}" subTabTitle="Delegation Member Qualifier" useCurrentTabIndexAsKey="true">      
    <table cellpadding="0" cellspacing="0" summary="">
        <tr>
        	<th width="5%" rowspan="20" style="border-style:none">&nbsp;</th>
			<th>&nbsp;</th> 
		    <c:choose>
	            <c:when test="${!empty delegationMember.attributesHelper.definitions and fn:length(delegationMember.attributesHelper.definitions) > 0}" >
					<c:forEach var="attrDefn" items="${delegationMember.attributesHelper.definitions}" varStatus="status1">
						<c:set var="attr" value="${attrDefn.value}" />
						<c:set var="fieldName" value="${attr.name}" />
						<c:set var="attrEntry" value="${delegationMember.attributesHelper.attributeEntry[fieldName]}" />
	         		    <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
					</c:forEach>	
 		        </c:when>
		     </c:choose>
      		</tr>         
        <tr>
         		<th>&nbsp;</th> 
		    <c:choose>
	            <c:when test="${!empty delegationMember.attributesHelper.definitions and fn:length(delegationMember.attributesHelper.definitions) > 0}" >
					<c:forEach var="attrDefn" items="${delegationMember.attributesHelper.definitions}" varStatus="status1">
						<c:set var="attr" value="${attrDefn.value}" />
						<c:set var="fieldName" value="${attr.name}" />
						<c:set var="attrEntry" value="${delegationMember.attributesHelper.attributeEntry[fieldName]}" />
				       	<td align="left" valign="middle">
				       		<div align="center"> 
				      		   <kul:htmlControlAttribute property="document.delegationMembers[${delegationMemberIdx}].qualifiers[${status1.index}].attrVal"  attributeEntry="${attrEntry}" readOnly="${readOnly}" />
				       		   <c:if test="${!empty attr.lookupBoClass and not readOnly}">
				       		       <kim:attributeLookup attributeDefinitions="${delegationMember.attributesHelper.definitions}" pathPrefix="document.delegationMembers[${delegationMemberIdx}]" attr="${attr}" />
				          	   </c:if>
				  			</div>
						</td>
					</c:forEach>	
 		        </c:when>
		     </c:choose>
      	</tr>
    </table>
</kul:subtab>