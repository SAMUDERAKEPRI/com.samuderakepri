package com.samuderakepri.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.samuderakepri.R

object IntentUtils {

    const val REDAKSI_WHATSAPP_URL =
        "https://wa.me/6281234567890?text=Halo%20Redaksi%20Samudera%20Kepri%2C%20saya%20ingin%20berbagi%20informasi%20atau%20bertanya%20terkait%20pemberitaan."

    fun openWhatsApp(context: Context, url: String = REDAKSI_WHATSAPP_URL) {
        val uri = Uri.parse(url)
        val waIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(waIntent)
        } catch (_: ActivityNotFoundException) {
            val w4bIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp.w4b")
            }
            try {
                context.startActivity(w4bIntent)
            } catch (_: ActivityNotFoundException) {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } catch (_: Exception) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_whatsapp_not_installed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun shareArticle(context: Context, title: String, url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\nBaca selengkapnya di Samudera Kepri:\n$url")
        }
        context.startActivity(
            Intent.createChooser(shareIntent, context.getString(R.string.menu_share))
        )
    }

    fun openExternalBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Tidak dapat membuka tautan eksternal", Toast.LENGTH_SHORT).show()
        }
    }

    fun isWhatsAppUrl(url: String): Boolean {
        val uri = Uri.parse(url)
        val scheme = uri.scheme?.lowercase() ?: ""
        val host = uri.host?.lowercase() ?: ""
        return scheme == "whatsapp" ||
            host == "wa.me" ||
            host == "api.whatsapp.com" ||
            host == "chat.whatsapp.com" ||
            url.contains("whatsapp.com", ignoreCase = true)
    }

    fun isInternalSamuderaUrl(url: String): Boolean {
        val host = Uri.parse(url).host?.lowercase() ?: ""
        return host == "samuderakepri.co.id" ||
            host == "www.samuderakepri.co.id" ||
            host.endsWith(".samuderakepri.co.id")
    }
}
