/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.mainmenu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * TODO Administrator don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesBlanketAppIT {
    private Selenium selenium;
    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    public void testUntitled() throws Exception {
        selenium.open(System.getProperty("remote.public.url"));
        assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Routing Rules");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//img[@alt='create new']");
        selenium.waitForPageToLoad("30000");
        selenium.click("methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:documentTypeName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "RoutingRuleDocument");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        selenium.click("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.bo.RuleTemplateBo!!).(((name:ruleTemplateName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "RuleRoutingTemplate");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        selenium.click("methodToCall.createRule");
        selenium.waitForPageToLoad("30000");
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        assertTrue(selenium.isElementPresent("methodToCall.cancel"));
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Test Routing Rule");
        selenium.click("//input[@id='document.newMaintainableObject.forceAction']");
        selenium.type("//textarea[@id='document.newMaintainableObject.description']", "Test Routing Rule1");
        selenium.type("//input[@id='document.newMaintainableObject.fieldValues(1321:docTypeFullName)']", "DocumentTypeDocument");
        selenium.click("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalName:document.newMaintainableObject.add.personResponsibilities.principalName,))).((`document.newMaintainableObject.add.personResponsibilities.principalName:principalName,`)).((<>)).(([])).((**)).((^^)).((&&)).((/personImpl/)).((~~)).(::::;" + System.getProperty("remote.public.url") + "kr/lookup.do;::::).anchor15");
        selenium.waitForPageToLoad("30000");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        selenium.select("//select[@id='document.newMaintainableObject.add.personResponsibilities.actionRequestedCd']", "label=ACKNOWLEDGE");
        selenium.type("//input[@id='document.newMaintainableObject.add.personResponsibilities.priority']", "1");
        selenium.click("methodToCall.addLine.personResponsibilities.(!!org.kuali.rice.kew.rule.PersonRuleResponsibility!!)");
        selenium.waitForPageToLoad("30000");
        selenium.click("methodToCall.blanketApprove");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        selenium.click("//img[@alt='doc search']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
       
        docId= "link=" + docId;
        assertTrue(selenium.isElementPresent(docId));       
        if(selenium.isElementPresent(docId)){            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, selenium.getText("//table[@id='row']/tbody/tr[1]/td[1]"));            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
