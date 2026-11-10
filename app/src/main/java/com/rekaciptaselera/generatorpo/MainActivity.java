package com.rekaciptaselera.generatorpo;

import android.app.Activity;
import android.print.PrintManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.widget.Toast;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Generator PO Beta (internal SCM): offline WebView wrapper untuk mesin V6.
 * Tidak memerlukan Internet / permission storage: backup memakai Android SAF.
 */
public final class MainActivity extends Activity {
    private static final int REQ_SAVE_FILE = 1701;
    private static final int REQ_PICK_JSON = 1702;
    private static final String PREFS = "generator_po_beta_storage";
    private static final String STATE_KEY = "po_state_v6";
    private WebView webView;
    private ValueCallback<Uri[]> pendingFileChooser;
    private String pendingFileContent;
    private String pendingFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        webView.setBackgroundColor(0xFFF3F6FB);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true); // hanya untuk file:///android_asset/index.html
        settings.setAllowContentAccess(true); // diperlukan untuk Restore JSON via SAF
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                if ("file".equals(url.getScheme()) &&
                        url.toString().startsWith("file:///android_asset/")) return false;
                // Jangan muat situs pihak ketiga ke dalam WebView dengan bridge aktif.
                try { startActivity(new Intent(Intent.ACTION_VIEW, url)); }
                catch (Exception ignored) { notifyUser("Tidak dapat membuka tautan."); }
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback,
                                             FileChooserParams params) {
                if (pendingFileChooser != null) pendingFileChooser.onReceiveValue(null);
                pendingFileChooser = callback;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_MIME_TYPES,
                        new String[]{"application/json", "text/plain", "application/octet-stream"});
                try { startActivityForResult(intent, REQ_PICK_JSON); }
                catch (Exception e) {
                    pendingFileChooser.onReceiveValue(null);
                    pendingFileChooser = null;
                    notifyUser("Tidak tersedia pemilih file JSON.");
                }
                return true;
            }
        });
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void notifyUser(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }

    public final class AndroidBridge {
        @JavascriptInterface
        public void saveState(String json) {
            getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(STATE_KEY, json).apply();
        }

        @JavascriptInterface
        public String getState() {
            SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return prefs.getString(STATE_KEY, "");
        }

        @JavascriptInterface
        public void saveFile(String name, String mime, String content) {
            runOnUiThread(() -> {
                if (pendingFileContent != null) {
                    notifyUser("Selesaikan penyimpanan sebelumnya terlebih dahulu.");
                    return;
                }
                pendingFileName = (name == null ? "PO_Ayam.json" : name)
                    .replaceAll("[^a-zA-Z0-9._-]", "_");
                pendingFileContent = content;
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(mime == null ? "application/json" : mime.split(";")[0]);
                intent.putExtra(Intent.EXTRA_TITLE, pendingFileName);
                try { startActivityForResult(intent, REQ_SAVE_FILE); }
                catch (Exception e) {
                    pendingFileContent = null;
                    notifyUser("Tidak dapat membuka pilihan simpan file.");
                }
            });
        }

        @JavascriptInterface
        public void copyHtml(String html, String text) {
            runOnUiThread(() -> {
                ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (cb != null) {
                    cb.setPrimaryClip(ClipData.newHtmlText("Generator PO", text, html));
                    notifyUser("Tabel PO disalin.");
                }
            });
        }

        @JavascriptInterface
        public void printPo() {
            runOnUiThread(() -> {
                PrintManager manager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                if (manager == null) { notifyUser("Layanan cetak tidak tersedia."); return; }
                PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Generator_PO");
                PrintAttributes attrs = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();
                manager.print("Generator PO", adapter, attrs);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQ_PICK_JSON) {
            if (pendingFileChooser != null) {
                Uri chosen = resultCode == RESULT_OK && result != null ? result.getData() : null;
                pendingFileChooser.onReceiveValue(chosen == null ? null : new Uri[]{chosen});
                pendingFileChooser = null;
            }
            return;
        }
        if (requestCode == REQ_SAVE_FILE) {
            try {
                Uri destination = resultCode == RESULT_OK && result != null ? result.getData() : null;
                if (destination != null && pendingFileContent != null) {
                    try (OutputStream out = getContentResolver().openOutputStream(destination)) {
                        if (out == null) throw new Exception("Gagal membuka penyimpanan");
                        out.write(pendingFileContent.getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    }
                    notifyUser("File tersimpan: " + pendingFileName);
                } else if (destination == null) {
                    notifyUser("Penyimpanan dibatalkan.");
                }
            } catch (Exception e) {
                notifyUser("Gagal menyimpan file: " + e.getMessage());
            } finally {
                pendingFileContent = null;
                pendingFileName = null;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (pendingFileChooser != null) {
            pendingFileChooser.onReceiveValue(null);
            pendingFileChooser = null;
        }
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
