# Generator PO — Beta Android 0.1

**Proyek APK Android untuk pemakaian internal PT Reka Cipta Selera / Crisbar.**

Basis: **Aplikasi Alokasi PO Crisbar V6 — Bersihkan Data Input** (source HTML lengkap, tidak ditulis ulang dari nol). Nama app yang terlihat di Android: **Generator PO Beta**. Package ID: `com.rekaciptaselera.generatorpo.beta.debug` untuk APK debug.

## Fitur yang dipertahankan

- 8 supplier termasuk MBU; Master Harga; pembatasan supplier/outlet ON/OFF.
- Stok BPA Gudang Bandung / Gudang Jakarta terpisah.
- 29 outlet dengan Rute Bandung/Jakarta; alokasi dan pending.
- Input Ayam Dada & Fillet dalam PCS lalu ÷10 menjadi Pack, kemudian pembulatan per supplier; Ayam Utuh tetap Ekor.
- Copy/paste Qty, angka 0, collapse/expand seluruh panel.
- Noted per SKU dan supplier, pengecualian outlet, warna tabel SKU, outlet A–Z.
- Satu supplier per halaman cetak; maksimal empat tabel dan A4 landscape.
- Ringkasan Qty, rupiah, dan backup JSON `PO_Ayam_YYYY-MM-DD.json`.
- Bersihkan transaksi tanpa menghapus master.

## Build APK (Android Studio)

1. Instal **Android Studio** dan SDK Platform **Android API 35**, Build Tools **35.x**.
2. Buka folder `GeneratorPOBeta` di Android Studio.
3. Proyek memakai **Android Gradle Plugin 8.9.2**, **Gradle 8.12**, dan **JDK 17**. Jika Gradle wrapper belum tersedia, buat dari Gradle terinstal: `gradle wrapper --gradle-version 8.12` (sebelum Sync), atau manfaatkan workflow GitHub Actions yang tersedia.
4. Tunggu `Gradle Sync` dan instal komponen Android SDK yang diminta.
5. Klik **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`.
7. Transfer APK ke Android, aktifkan izin instal aplikasi dari sumber tersebut saat diminta oleh perangkat, lalu pasang.

> **PENTING:** ZIP yang dibagikan adalah **source project siap build, BUKAN APK**. APK belum dapat dikompilasi dalam sesi pembuatan ini karena Android SDK dan Gradle tidak tersedia dan jaringan perangkat build dibatasi. Kode Java belum diuji pada perangkat Android nyata.

## Build via GitHub Actions (alternatif paling mudah jika tidak punya Android Studio)

1. Buat repository GitHub baru dan unggah **isi** folder `GeneratorPOBeta` (sehingga folder `.github/` berada di root repository).
2. Masuk **Actions** → pilih **Build Generator PO Beta APK** → **Run workflow**.
3. Setelah workflow berhasil, buka run → bagian **Artifacts** → unduh `Generator-PO-Beta-Debug-APK`.
4. Di ZIP artifact akan ada `app-debug.apk` yang bisa diinstal untuk uji internal.

Workflow membutuhkan internet pada runner GitHub untuk Android SDK, Gradle, dan plugin Android. Ini merupakan build debug (bukan aplikasi siap Play Store).

## Android-specific behavior

- **Data kerja/master** disimpan otomatis pada SharedPreferences privat aplikasi lewat `AndroidBridge`. Data ini tidak bisa dilihat aplikasi lain secara langsung.
- **Backup JSON / ekspor HTML** memakai pemilih lokasi simpan bawaan Android (Storage Access Framework, tanpa izin akses seluruh storage).
- **Muat Backup JSON** memakai pemilih file Android.
- **Salin tabel** memakai clipboard Android termasuk representasi HTML.
- **Cetak/PDF** memakai Android Print Framework, secara default A4 landscape. **Harus diuji** ketepatan page break pada printer/perangkat sebelum digunakan secara operasional.
- **Offline**: tidak ada koneksi API; file HTML dikemas dalam APK, tidak meminta permission internet.
- Data yang masih tersimpan pada HTML V6 di browser PC **tidak otomatis pindah ke APK**: lakukan Backup JSON di V6 browser, lalu Restore di app Android.
- APK ini untuk pengujian internal; belum menyediakan sinkronisasi data lintas HP/login.

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
