# Beta: WebView JavaScript interface diakses secara eksplisit oleh aplikasi.
-keepclassmembers class com.rekaciptaselera.generatorpo.MainActivity$AndroidBridge {
    @android.webkit.JavascriptInterface <methods>;
}
