package org.dhis2.usescases.venoapp
import kotlinx.coroutines.delay



// enter your package name here
const val clientPackageName="org.dhis2"

// If this function is to be asynchronous
suspend fun storePseudonym(pseudonym: String): Boolean {
    // This could be a network call or database operation
    delay(1000)
    return true
}

fun storeAuthenticationToken(token:String){
    println("Authentication token stored $token")
}

