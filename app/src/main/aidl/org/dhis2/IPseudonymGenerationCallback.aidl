// IPseudonymGenerationCallback.aidl
package org.dhis2;

// Declare any non-default types here with import statements

interface IPseudonymGenerationCallback {
    void onPseudonymGenerated(String pseudonym);
    void onGenerateError(String error);
}