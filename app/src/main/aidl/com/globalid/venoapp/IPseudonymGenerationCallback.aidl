// IPseudonymGenerationCallback.aidl
package com.globalid.venoapp;

// Declare any non-default types here with import statements

interface IPseudonymGenerationCallback {
    void onPseudonymGenerated(String pseudonym);
    void onGenerateError(String error);
}