package com.larryhsiao.nyx.drive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.larryhsiao.clotho.Source

/**
 * Source to build [Drive] instance for Android platform.
 */
class AndroidClient(
    private val context: Context,
    private val appName: String,
    private val account: GoogleSignInAccount,
    private val scopes: Collection<String>
) : Source<Drive> {
    override fun value(): Drive {
        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential(account)
        ).setApplicationName(appName).build()
    }

    private fun credential(account: GoogleSignInAccount): GoogleAccountCredential? {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            scopes
        )
        credential.selectedAccount = account.account
        return credential
    }
}