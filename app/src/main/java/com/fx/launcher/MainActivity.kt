package com.fx.launcher

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(8, 10, 15)
    private val panel = Color.rgb(19, 22, 31)
    private val white = Color.rgb(245, 247, 255)
    private val muted = Color.rgb(151, 160, 180)
    private val accent = Color.rgb(126, 108, 255)
    private lateinit var root: LinearLayout
    private lateinit var clock: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            if (::clock.isInitialized) {
                clock.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        showHome()
        handler.post(ticker)
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        super.onDestroy()
    }

    private fun rounded(color: Int, radius: Float = 24f): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = radius }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
        }

    private fun showHome() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 24, 22, 18)
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        top.addView(text("FX", 23f, white, true))
        top.addView(text("  LAUNCHER", 13f, muted, true))
        val spacer = View(this)
        top.addView(spacer, LinearLayout.LayoutParams(0, 1, 1f))
        val settings = Button(this).apply {
            text = "⋯"
            textSize = 20f
            setTextColor(white)
            background = rounded(panel, 18f)
            setOnClickListener { showSettings() }
        }
        top.addView(settings, LinearLayout.LayoutParams(54, 48))
        content.addView(top)

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 22, 22, 22)
            background = rounded(panel, 28f)
        }
        val day = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())
        hero.addView(text(day.uppercase(Locale.getDefault()), 12f, muted, true))
        clock = text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()), 58f, white, true)
        hero.addView(clock)
        hero.addView(text("YOUR WORLD. YOUR WAY.", 11f, accent, true))
        val heroParams = LinearLayout.LayoutParams(-1, -2)
        heroParams.topMargin = 28
        content.addView(hero, heroParams)

        val quickTitle = text("YOUR FX ECOSYSTEM", 12f, muted, true)
        val quickParams = LinearLayout.LayoutParams(-1, 46)
        quickParams.topMargin = 24
        content.addView(quickTitle, quickParams)
        val quick = GridLayout(this).apply { columnCount = 3 }
        listOf(
            Triple("◷", "FX CLOCK", "FX CLOCK"),
            Triple("⬡", "SECURITY", "FX SECURITY"),
            Triple("◎", "FX VERSE", "FX VERSE")
        ).forEach { item ->
            val tile = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(6, 15, 6, 15)
                background = rounded(panel, 22f)
                isClickable = true
                isFocusable = true
                setOnClickListener { openProduct(item.third) }
            }
            tile.addView(text(item.first, 26f, accent, true).apply { gravity = Gravity.CENTER })
            tile.addView(text(item.second, 10f, white, true).apply { gravity = Gravity.CENTER })
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 104
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(4, 4, 4, 4)
            }
            quick.addView(tile, params)
        }
        content.addView(quick)

        val appsHeader = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        appsHeader.addView(text("APPS", 12f, muted, true))
        val gap = View(this)
        appsHeader.addView(gap, LinearLayout.LayoutParams(0, 1, 1f))
        val allApps = Button(this).apply {
            text = "View all  →"
            setTextColor(accent)
            background = rounded(bg, 10f)
            setOnClickListener { showAppDrawer() }
        }
        appsHeader.addView(allApps)
        val headerParams = LinearLayout.LayoutParams(-1, 54)
        headerParams.topMargin = 14
        content.addView(appsHeader, headerParams)

        val drawerPreview = Button(this).apply {
            text = "⌕     Search and open apps"
            textSize = 15f
            gravity = Gravity.CENTER_VERTICAL
            setPadding(18, 0, 18, 0)
            setTextColor(muted)
            background = rounded(panel, 18f)
            setOnClickListener { showAppDrawer() }
        }
        content.addView(drawerPreview, LinearLayout.LayoutParams(-1, 58))

        val footer = text("FX • BUILT FOR YOUR EVERYDAY", 10f, muted, true).apply { gravity = Gravity.CENTER }
        root.addView(footer, LinearLayout.LayoutParams(-1, 40))
        setContentView(root)
    }

    private fun openProduct(label: String) {
        val query = when (label) {
            "FX CLOCK" -> listOf("clock")
            "FX SECURITY" -> listOf("security", "fx")
            else -> listOf("verse", "fx")
        }
        val apps = getLaunchableApps().filter { app -> query.any { app.label.contains(it, true) } }
        if (apps.isNotEmpty()) {
            launch(apps.first().packageName)
        } else {
            AlertDialog.Builder(this)
                .setTitle(label)
                .setMessage("Install $label on this tablet, then it can be opened from here. For now, you can find it in the app drawer if its name differs.")
                .setPositiveButton("Open app drawer") { _, _ -> showAppDrawer() }
                .setNegativeButton("OK", null)
                .show()
        }
    }

    private data class LaunchApp(val label: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

    private fun getLaunchableApps(): List<LaunchApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .mapNotNull { info ->
                val label = info.loadLabel(packageManager)?.toString() ?: return@mapNotNull null
                LaunchApp(label, info.activityInfo.packageName, info.loadIcon(packageManager))
            }
            .distinctBy { it.packageName }
            .filter { it.packageName != packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    private fun showAppDrawer() {
        val apps = getLaunchableApps()
        val body = ScrollView(this)
        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 18, 24, 18)
            setBackgroundColor(bg)
        }
        list.addView(text("ALL APPS", 20f, white, true))
        list.addView(text("${apps.size} apps installed", 12f, muted).apply {
            val p = LinearLayout.LayoutParams(-1, 36)
            addView(this, p)
        })
        apps.forEach { app ->
            val row = TextView(this).apply {
                text = "▣     ${app.label}"
                textSize = 16f
                setTextColor(white)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(14, 0, 14, 0)
                background = rounded(panel, 16f)
                isClickable = true
                isFocusable = true
                setOnClickListener { launch(app.packageName) }
            }
            val p = LinearLayout.LayoutParams(-1, 54)
            p.setMargins(0, 4, 0, 4)
            list.addView(row, p)
        }
        val back = Button(this).apply {
            text = "← Back to FX home"
            setTextColor(white)
            background = rounded(accent, 18f)
            setOnClickListener { showHome() }
        }
        list.addView(back, LinearLayout.LayoutParams(-1, 52))
        body.addView(list)
        setContentView(body)
    }

    private fun launch(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            try { startActivity(intent) } catch (_: Exception) {
                AlertDialog.Builder(this).setMessage("Couldn't open this app.").setPositiveButton("OK", null).show()
            }
        }
    }

    private fun showSettings() {
        val options = arrayOf("Choose wallpaper", "Android home settings", "About FX Launcher")
        AlertDialog.Builder(this)
            .setTitle("FX Launcher settings")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        try { startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)) }
                        catch (_: Exception) {
                            try { startActivity(Intent(Intent.ACTION_SET_WALLPAPER)) } catch (_: Exception) {}
                        }
                    }
                    1 -> {
                        try { startActivity(Intent(android.provider.Settings.ACTION_HOME_SETTINGS)) }
                        catch (_: Exception) { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
                    }
                    2 -> AlertDialog.Builder(this).setTitle("FX Launcher").setMessage("Version 1.0\nA first step toward FX Phone.").setPositiveButton("Nice", null).show()
                }
            }
            .show()
    }
}
