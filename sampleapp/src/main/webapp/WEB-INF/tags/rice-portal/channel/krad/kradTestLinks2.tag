<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="KRAD Testing - KS L&F" />
<div class="body">
  <strong>Screen Element Testing</strong>
  <ul class="chan">
  	 <li><portal:portalLink displayTitle="true" title="Uif Layout Test" url="${ConfigProperties.application.url}/kr-krad/uilayouttest?viewId=UifLayoutView&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Standard Layout Demo" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-StandardLayout&methodToCall=start" /></li>
  	 <li><portal:portalLink displayTitle="true" title="Uif Components (Kitchen Sink)" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91" /></li>
     <li><portal:portalLink displayTitle="true" title="ServerSide Validation Demo" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-ValidationServerSide&methodToCall=start" /></li>
	   <li><portal:portalLink displayTitle="true" title="Test View 1" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView1&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Test View 2" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Incident Report" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2&methodToCall=foo" /></li>
   </ul>
   <br/>

  <strong>Demo Views</strong>
  <ul class="chan">
    <li><a class="portal_link" href="${ConfigProperties.application.url}/kr-krad/courseOffering?viewId=CourseOfferingView&methodToCall=start" title="Course Offering" target="_blank">Course Offering</a></li>
    <li><a class="portal_link" href="${ConfigProperties.application.url}/kr-krad/registration?viewId=RegistrationView&methodToCall=start" title="Registration" target="_blank">Registration</a></li>
  </ul>
</div>
<channel:portalChannelBottom />
