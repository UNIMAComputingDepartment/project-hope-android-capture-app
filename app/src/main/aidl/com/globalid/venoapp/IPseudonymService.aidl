// IPseudonymService.aidl
package com.globalid.venoapp;

// Declare any non-default types here with import statements

import com.globalid.venoapp.IPseudonymGenerationCallback;
import com.globalid.venoapp.IPseudonymDeleteCallback;
import com.globalid.venoapp.IPseudoAssociatedCallback;

interface IPseudonymService {

    void requestPseudonym(IPseudonymGenerationCallback callback);

    void deletePseudonym(String pseudonym, IPseudonymDeleteCallback callback);

    void isPseudoAssociated(String pseudonym, IPseudoAssociatedCallback callback );
}