package com.fx.launcher

import android.app.Activity
import android.app.AlertDialog
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
    private val panel = Color.rgb(21, 24, 34)
    private val white = Color.rgb(245, 247, 255)
    private val muted = Color.rgb(151, 160, 180)
    private val accent = Color.rgb(132, 111, 255)
    private lateinit var clock: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            if (::clock.isInitialized) clock.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            handler.postDelayed(this, 1000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        showHome()
        handler.post(tick)
    }
    override fun onDestroy() { handler.removeCallbacks(tick); super.onDestroy() }
    private fun rounded(color: Int, radius: Float = 22f) = GradientDrawable().apply { setColor(color); cornerRadius = radius }
    private fun label(s: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        text=s; textSize=size; setTextColor(color); if (bold) setTypeface(typeface, Typeface.BOLD); gravity=Gravity.CENTER_VERTICAL
    }
    private fun showHome() {
        val outer = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(22,24,22,16); setBackgroundColor(bg) }
        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        scroll.addView(body); outer.addView(scroll, LinearLayout.LayoutParams(-1,0,1f))
        val brand = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        brand.addView(label("FX",25f,white,true)); brand.addView(label("  LAUNCHER",13f,muted,true))
        val spacer=View(this); brand.addView(spacer,LinearLayout.LayoutParams(0,1,1f))
        val menu=Button(this).apply { text="⋯"; textSize=22f; setTextColor(white); background=rounded(panel); setOnClickListener { settings() } }
        brand.addView(menu,LinearLayout.LayoutParams(54,48)); body.addView(brand)
        val hero=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(22,22,22,22); background=rounded(panel,28f) }
        hero.addView(label(SimpleDateFormat("EEEE, d MMMM",Locale.getDefault()).format(Date()).uppercase(),12f,muted,true))
        clock=label(SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date()),60f,white,true); hero.addView(clock)
        hero.addView(label("YOUR WORLD. YOUR WAY.",11f,accent,true))
        val hp=LinearLayout.LayoutParams(-1,-2); hp.topMargin=26; body.addView(hero,hp)
        val title=label("YOUR FX ECOSYSTEM",12f,muted,true); val tp=LinearLayout.LayoutParams(-1,48); tp.topMargin=18; body.addView(title,tp)
        val grid=GridLayout(this).apply { columnCount=3 }
        listOf(Triple("◷","FX CLOCK","clock"),Triple("⬡","FX SECURITY","security"),Triple("◎","FX VERSE","verse")).forEach { item ->
            val tile=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(5,12,5,12); background=rounded(panel); isClickable=true; setOnClickListener { openProduct(item.third) } }
            tile.addView(label(item.first,27f,accent,true).apply { gravity=Gravity.CENTER })
            tile.addView(label(item.second,10f,white,true).apply { gravity=Gravity.CENTER })
            val p=GridLayout.LayoutParams().apply { width=0; height=104; columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f); setMargins(4,4,4,4) }
            grid.addView(tile,p)
        }; body.addView(grid)
        val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        row.addView(label("APPS",12f,muted,true)); val gap=View(this); row.addView(gap,LinearLayout.LayoutParams(0,1,1f))
        row.addView(Button(this).apply { text="View all  →"; setTextColor(accent); background=rounded(bg); setOnClickListener { showDrawer() } })
        val rp=LinearLayout.LayoutParams(-1,52); rp.topMargin=12; body.addView(row,rp)
        body.addView(Button(this).apply { text="⌕    Search and open apps"; textSize=15f; setTextColor(muted); background=rounded(panel); setOnClickListener { showDrawer() } },LinearLayout.LayoutParams(-1,56))
        outer.addView(label("FX • BUILT FOR YOUR EVERYDAY",10f,muted,true).apply { gravity=Gravity.CENTER },LinearLayout.LayoutParams(-1,38))
        setContentView(outer)
    }
    private data class AppItem(val name:String,val pkg:String)
    private fun apps(): List<AppItem> = packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),PackageManager.MATCH_ALL)
        .mapNotNull { val n=it.loadLabel(packageManager)?.toString() ?: return@mapNotNull null; AppItem(n,it.activityInfo.packageName) }
        .distinctBy { it.pkg }.filter { it.pkg != packageName }.sortedBy { it.name.lowercase(Locale.getDefault()) }
    private fun launch(pkg:String) { packageManager.getLaunchIntentForPackage(pkg)?.let { try { startActivity(it) } catch (_:Exception) {} } }
    private fun openProduct(key:String) {
        val words=when(key) { "clock"->listOf("fx clock","fx-clock"); "security"->listOf("fx security","security"); else->listOf("fx verse","verse") }
        val found=apps().firstOrNull { a -> words.any { a.name.contains(it,true) } }
        if(found!=null) launch(found.pkg) else AlertDialog.Builder(this).setTitle("FX product").setMessage("Install the matching FX app first. You can also find it in the app drawer if it has a different name.").setPositiveButton("Open app drawer") { _,_->showDrawer() }.setNegativeButton("OK",null).show()
    }
    private fun showDrawer() {
        val sc=ScrollView(this); val list=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(22,22,22,22); setBackgroundColor(bg) }
        list.addView(label("ALL APPS",22f,white,true)); list.addView(label("${apps().size} installed apps",12f,muted))
        apps().forEach { a ->
            val b=Button(this).apply { text="▣     ${a.name}"; gravity=Gravity.CENTER_VERTICAL; setTextColor(white); background=rounded(panel); setOnClickListener { launch(a.pkg) } }
            val p=LinearLayout.LayoutParams(-1,54); p.setMargins(0,4,0,4); list.addView(b,p)
        }
        list.addView(Button(this).apply { text="← Back to FX home"; setTextColor(white); background=rounded(accent); setOnClickListener { showHome() } },LinearLayout.LayoutParams(-1,52))
        sc.addView(list); setContentView(sc)
    }
    private fun settings() {
        AlertDialog.Builder(this).setTitle("FX Launcher").setItems(arrayOf("Android home settings","About FX Launcher")) { _,which ->
            if(which==0) try { startActivity(Intent(android.provider.Settings.ACTION_HOME_SETTINGS)) } catch (_:Exception) { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
            else AlertDialog.Builder(this).setTitle("FX Launcher v1.0").setMessage("A first step toward FX Phone.").setPositiveButton("OK",null).show()
        }.show()
    }
}