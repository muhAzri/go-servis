package com.zrifapps.goservice.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/** Public links used by the "Beri Rating" / "Bagikan App" actions. */
const val PLAY_STORE_PACKAGE = "com.zrifapps.goservice"
const val LANDING_PAGE_URL = "https://www.goservis.my.id/"

/**
 * Opens the Play Store listing so the user can leave a rating/review. Tries the
 * native Play Store app first, then falls back to the web listing in a browser.
 */
fun Context.openPlayStoreForRating() {
    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$PLAY_STORE_PACKAGE"),
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

    try {
        startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$PLAY_STORE_PACKAGE"),
            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
        )
    }
}

/** Shares the marketing landing page through the system share sheet. */
fun Context.shareApp() {
    val text = "Coba GoService — pengingat servis motor & mobil biar nggak telat ganti oli. " +
        "Info & download: $LANDING_PAGE_URL"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "GoService")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, "Bagikan GoService"))
}
