/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.rice.kns.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyLabelPair;

/**
 * This class returns list containg A = Active, I = Inactive and B = Show All
 * 
 * The labels (values) in the List returned correspond to what is displayed next
 * to the radio buttons for the Active Indicator form field
 */
public class ActiveInactiveValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("A", "Active"));
        keyValues.add(new KeyLabelPair("I", "Inactive"));
        keyValues.add(new KeyLabelPair("B", "Show All"));
        return keyValues;
    }

}