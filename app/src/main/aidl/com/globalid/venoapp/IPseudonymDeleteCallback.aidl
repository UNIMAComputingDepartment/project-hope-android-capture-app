// IPseudonymDeleteCallback.aidl
package com.globalid.venoapp;

// Declare any non-default types here with import statements

interface IPseudonymDeleteCallback {
    void onPseudonymDeleted(boolean isDeleted, String message);
}