// IPseudonymService.aidl
package org.dhis2;

// Declare any non-default types here with import statements

import org.dhis2.IPseudonymGenerationCallback;
import org.dhis2.IPseudonymDeleteCallback;
import org.dhis2.IPseudoAssociatedCallback;

interface IPseudonymService {

    void requestPseudonym(IPseudonymGenerationCallback callback);

    void deletePseudonym(String pseudonym, IPseudonymDeleteCallback callback);

    void isPseudoAssociated(String pseudonym, IPseudoAssociatedCallback callback );
}