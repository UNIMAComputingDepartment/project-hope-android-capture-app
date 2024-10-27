// IPseudoAssociatedCallback.aidl
package org.dhis2;

// Declare any non-default types here with import statements

interface IPseudoAssociatedCallback {
    void onPseudonymAssociatedResult(boolean isAssociated,String message);
}