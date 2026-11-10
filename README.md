# Generator PO — Beta Android 0.1

**Proyek APK Android untuk pemakaian internal**

## Catatan QA beta

- Uji restore master via JSON di HP sebelum mengandalkan data yang tersimpan.
- Uji penyimpanan backup file termasuk skenario batal.
- Uji cetak PDF empat tabel untuk satu supplier dan dua supplier pada halaman berbeda.
- Uji Android Back, orientasi layar, keyboard, dan copy-paste Qty panjang.
- Uji alokasi stok BPA dan pembatasan supplier ON/OFF.

## Struktur

```
GeneratorPOBeta/
  app/src/main/assets/index.html                 # App V6 yang diadaptasi untuk Android
  app/src/main/java/.../MainActivity.java         # WebView + jembatan native
  app/src/main/AndroidManifest.xml
  app/build.gradle, build.gradle, settings.gradle
  .github/workflows/build-beta-apk.yml           # Build APK debug otomatis
```

Tidak perlu memasang library pihak ketiga pada runtime; ini adalah **beta Android WebView native**. Arsitektur Capacitor dapat digunakan pada tahap berikutnya setelah uji API file/print stabil.
