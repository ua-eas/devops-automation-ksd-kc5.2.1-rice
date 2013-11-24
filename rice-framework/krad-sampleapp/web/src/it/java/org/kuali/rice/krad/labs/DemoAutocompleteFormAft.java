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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoAutocompleteFormAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Lab-NativeAutocomplete-DisableForm
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=Lab-NativeAutocomplete-DisableForm";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Native Autocomplete Form Disabled");
    }

    protected void testDemoAutocompleteForm() throws InterruptedException {
    	waitForElementPresentByXpath("//form[@autocomplete='off']");
    }

    @Test
    public void testDemoAutocompleteFormBookmark() throws Exception {
    	testDemoAutocompleteForm();
        passed();
    }

    @Test
    public void testDemoAutocompleteFormNav() throws Exception {
    	testDemoAutocompleteForm();
        passed();
    }
}
