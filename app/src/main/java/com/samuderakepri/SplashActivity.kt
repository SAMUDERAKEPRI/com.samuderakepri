package com.samuderakepri

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityOptionsCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SplashActivity untuk PT Media Samudera Kepri.
 *
 * Mengimplementasikan:
 * 1. Dukungan resmi Android 12+ SplashScreen API (androidx.core:core-splashscreen).
 * 2. Tampilan logo resmi PT Media Samudera Kepri di tengah layar dengan latar belakang gelap elegan.
 * 3. Menahan tampilan tepat selama 2 detik (2000 ms).
 * 4. Animasi transisi yang sangat mulus (Smooth Transition) ke MainActivity.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    companion object {
        private const val SPLASH_DURATION_MS = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashContainer)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startEntranceAnimation()

        lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            navigateToMainActivity()
        }
    }

    private fun startEntranceAnimation() {
        val imgLogo = findViewById<ImageView>(R.id.imgLogo)
        val tvCompanyName = findViewById<TextView>(R.id.tvCompanyName)
        val tvTagline = findViewById<TextView>(R.id.tvTagline)

        imgLogo.alpha = 0f
        imgLogo.scaleX = 0.85f
        imgLogo.scaleY = 0.85f
        tvCompanyName.alpha = 0f
        tvTagline.alpha = 0f

        imgLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(700L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        tvCompanyName.animate()
            .alpha(1f)
            .setStartDelay(200L)
            .setDuration(600L)
            .start()

        tvTagline.animate()
            .alpha(1f)
            .setStartDelay(350L)
            .setDuration(600L)
            .start()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this@SplashActivity,
                R.anim.fade_in,
                R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        finish()
    }
}
