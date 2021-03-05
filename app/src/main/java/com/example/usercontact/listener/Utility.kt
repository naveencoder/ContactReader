package com.example.usercontact.listener

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

object Utility {

    fun doMessage(context: Context, phoneNumber: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:".plus(phoneNumber)))
        startActivity(context, smsIntent, null)
    }
}