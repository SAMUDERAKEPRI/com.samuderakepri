package com.samuderakepri

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.samuderakepri.data.local.NewsSeedData
import com.samuderakepri.utils.IntentUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string and package id from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Samudera Kepri", appName)
        assertEquals("com.samuderakepri", context.packageName)
    }

    @Test
    fun `verify splash activity creation`() {
        val controller = Robolectric.buildActivity(SplashActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }

    @Test
    fun `verify main activity creation and news portal url`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
        assertEquals("https://www.samuderakepri.co.id", MainActivity.NEWS_URL)
    }

    @Test
    fun `verify url routing helpers for whatsapp and internal domains`() {
        assertTrue(IntentUtils.isWhatsAppUrl("https://wa.me/6281234567890"))
        assertTrue(IntentUtils.isWhatsAppUrl("whatsapp://send?text=Halo"))
        assertFalse(IntentUtils.isWhatsAppUrl("https://www.samuderakepri.co.id"))

        assertTrue(IntentUtils.isInternalSamuderaUrl("https://www.samuderakepri.co.id/berita"))
        assertTrue(IntentUtils.isInternalSamuderaUrl("https://samuderakepri.co.id"))
        assertFalse(IntentUtils.isInternalSamuderaUrl("https://google.com"))
    }

    @Test
    fun `verify initial offline seed articles cover all key rubrics`() {
        val seed = NewsSeedData.getInitialCachedArticles()
        assertTrue(seed.isNotEmpty())
        assertTrue(seed.any { it.isHero })
        assertTrue(seed.any { it.isInvestigative })
        assertTrue(seed.any { it.category == "Politik & Pemerintahan" })
        assertTrue(seed.any { it.category == "Ekonomi & Bisnis" })
        assertTrue(seed.any { it.category == "Hukum & Kriminal" })
        assertTrue(seed.any { it.category == "Pendidikan" })
        assertTrue(seed.any { it.category == "Sosial & Masyarakat" })
        assertTrue(seed.any { it.category == "Maritim" })
    }
}
