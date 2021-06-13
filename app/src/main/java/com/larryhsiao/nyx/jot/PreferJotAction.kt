package com.larryhsiao.nyx.jot

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import com.larryhsiao.clotho.Action
import com.larryhsiao.nyx.R
import com.larryhsiao.nyx.core.jots.Jot
import java.util.concurrent.Executors

/**
 * Prefer jot action.
 */
class PreferJotAction(
        private val fragment: Fragment,
        private val jot: Jot,
        private val success: (jot: Jot) -> Unit
) : Action {
    override fun fire() {
        if (jot.privateLock()) {
            BiometricPrompt(
                    fragment,
                    Executors.newSingleThreadExecutor(),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Handler(Looper.getMainLooper()).post {
                                success(jot)
                            }
                        }

                        override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            val keyguardManager: KeyguardManager = fragment.requireContext().getSystemService(
                                    Context.KEYGUARD_SERVICE
                            ) as KeyguardManager //api 16+
                            val isSecured = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                keyguardManager.isDeviceSecure
                            } else {
                                keyguardManager.isKeyguardSecure
                            }
                            if (!isSecured) {
                                Handler(Looper.getMainLooper()).post {
                                    success(jot)
                                }
                            }
                        }
                    }
            ).authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                            .setTitle(fragment.getString(R.string.Private_content))
                            .setSubtitle(fragment.getString(R.string.Unlock_for_the_private_content))
                            .setDeviceCredentialAllowed(true)
                            .setConfirmationRequired(true)
                            .build()
            )
        } else {
            success(jot)
        }
    }
}