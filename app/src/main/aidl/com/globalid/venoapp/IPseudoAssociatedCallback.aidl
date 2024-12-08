// IPseudoAssociatedCallback.aidl
package com.globalid.venoapp;

// Declare any non-default types here with import statements

interface IPseudoAssociatedCallback {
    void onPseudonymAssociatedResult(boolean isAssociated,String message);
}