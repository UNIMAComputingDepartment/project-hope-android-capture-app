// IPseudonymDeleteCallback.aidl
package org.dhis2;

// Declare any non-default types here with import statements

interface IPseudonymDeleteCallback {
    void onPseudonymDeleted(boolean isDeleted, String message);
}