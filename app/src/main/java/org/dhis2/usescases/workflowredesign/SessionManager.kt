package org.dhis2.usescases.workflowredesign

// SessionManager.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val disposables = CompositeDisposable()
    private val sessionTimeout = 1L // 5 minutes

    private var sessionOwner: String = ""

    private val _session = MutableLiveData(false to sessionOwner)
    val session: LiveData<Pair<Boolean, String>> get() = _session

    /**
     * Starts the session timer. If a timer is already running, it will be reset.
     */
    fun startSessionTimer(teiUid: String) {
        disposables.clear()
        sessionOwner = teiUid
        _session.value = true to sessionOwner

        val disposable = Single.timer(sessionTimeout, TimeUnit.MINUTES)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _session.postValue(false to sessionOwner)
                Timber.d("Session expired after $sessionTimeout minutes.")
            }, { error ->
                Timber.e(error, "Error in session timer.")
            })

        disposables.add(disposable)
    }

    /**
     * Resets the session timer by restarting it.
     */
    fun resetSessionTimer() {
        startSessionTimer(sessionOwner)
        Timber.d("Session timer reset.")
    }

    /**
     * Clears all disposables to prevent memory leaks.
     */
    fun clear() {
        disposables.clear()
        Timber.d("Session timer cleared.")
    }
}
