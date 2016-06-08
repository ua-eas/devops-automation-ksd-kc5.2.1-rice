/**
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.rice.core.api.util;


import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Helper class for comparing version strings
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class VersionHelper {

    // This pattern matches any non-digit string containing a version number of the form A.B.C.D followed by any non-digit string
    private static final Pattern extractVersion = Pattern.compile("(\\D*)((\\d*\\.?)+)(\\D*)");

    // Pattern used for splitting version number for comparison
    private static final Pattern splitVersion = Pattern.compile("\\.");

    /**
     * returns -1 if versionOne is less than versionTwo, 0 if the versions are equal, and 1 if versionOne is more recent than versionTwo
     *
     * @param versionOne string representation of a version
     * @param versionTwo string representation of a version
     * @return int
     */

    public static int compareVersion(String versionOne, String versionTwo) {
        try {
            return compareVersionInt(versionOne, versionTwo);
        } catch (IllegalArgumentException iae) {
               return -1;
        }

    }

    private static int compareVersionInt(String versionOne, String versionTwo) throws IllegalArgumentException {


        if (StringUtils.isBlank(versionOne) || StringUtils.isBlank(versionTwo)) {
            throw new IllegalArgumentException("Invalid argument");
        }

        Matcher m1 = extractVersion.matcher(versionOne);
        Matcher m2 = extractVersion.matcher(versionTwo);

        if (!m1.matches() || !m2.matches()) {
            throw new IllegalArgumentException("Unable to extract version number from string using regex");
        }

        //uses the matches generated by the extractVersion pattern to pull the version out of a string
        //  i.e. "2.1.3-SNAPSHOT" after sanitization is "2.1.3"

        String sanitizedVOne = versionOne.substring(m1.start(2), m1.end(2));
        String sanitizedVTwo = versionTwo.substring(m2.start(2), m2.end(2));

        //uses the splitPattern to break a string into an array of numeric strings
        // i.e. "2.1.3" becomes {"2", "1", "3"}

        String[] oneDigits = splitVersion.split(sanitizedVOne);
        String[] twoDigits = splitVersion.split(sanitizedVTwo);

        // if there are unequal arrays, iterate over arrays up to the length of the shorter array
        // have to be able to deal with the case "2.1" compared to "2.1.3"
        int length=0;
        if (oneDigits.length<twoDigits.length) {
            length=oneDigits.length;
        } else {
            length=twoDigits.length;
        }

        for(int i=0; i<length; i++) {
            Integer intOne = Integer.valueOf(oneDigits[i]);
            Integer intTwo = Integer.valueOf(twoDigits[i]);
            Integer compare = intOne.compareTo(intTwo);
            if (compare < 0) {
                return -1;
            }
            else if (compare > 0) {
                return 1;
            }
        }
        if (oneDigits.length<twoDigits.length) {
            return -1;
        }
        if (oneDigits.length==twoDigits.length) {
            return 0;
        }
        return 1;

    }


}


