package com.larryhsiao.nyx.view.settings

import android.os.Handler
import android.os.Looper
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.larryhsiao.nyx.R
import com.silverhetch.clotho.Action
import java.util.concurrent.Executors

/**
 * Bio auth simple wrapper, to make the implementation a little bit easier .
 *
 * @todo #aura-1 move this implementation to Aura
 */
class BioAuth(
    private val activity: FragmentActivity,
    private val success: () -> Unit = {},
    private val failed: (code: Int, error: String) -> Unit
) : Action, BiometricPrompt.AuthenticationCallback() {
    private val mainThread = Handler(Looper.getMainLooper())
    override fun fire() {
        BiometricPrompt(
            activity,
            Executors.newSingleThreadExecutor(),
            this
        ).authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.authorization))
                .setDescription(activity.getString(R.string.authorization))
                .setNegativeButtonText(activity.getString(R.string.cancel))
                .build()
        )
    }

    override fun onAuthenticationError(code: Int, err: CharSequence) {
        super.onAuthenticationError(code, err)
        mainThread.post { failed(code, err.toString()) }
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        mainThread.post(success)
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        /*
        Ignore auth failed, the promote UI will inform the failure to user.
        The error will triggered as canceled by user which is the only event user can trigger.
        */

    }
}