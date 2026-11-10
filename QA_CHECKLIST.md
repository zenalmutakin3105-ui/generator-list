# Generator PO Beta — Pemeriksaan sebelum uji operasional

## Lolos uji di Chromium (simulasi jembatan Android)

- [x] Halaman utama muncul tanpa JavaScript exception.
- [x] Nama aplikasi menampilkan **Generator PO Beta**.
- [x] Tombol **Muat Contoh** menghasilkan empat tabel PO.
- [x] Data dioperkan ke fungsi `AndroidBridge.saveState()`.
- [x] Backup JSON memanggil `AndroidBridge.saveFile()` dengan format `PO_Ayam_YYYY-MM-DD.json`.
- [x] Cetak memanggil `AndroidBridge.printPo()`.
- [x] Salin Tabel memanggil `AndroidBridge.copyHtml()`.
- [x] Tombol Bersihkan Data Input menampilkan konfirmasi dan mengosongkan hasil tabel.
- [x] Sintaks JavaScript dan file XML Android diperiksa.

## Perlu diuji setelah APK berhasil dibuild dan dipasang di HP Android

- [ ] APK dibuild tanpa error di Android Studio / GitHub Actions.
- [ ] Splash, status bar, layar kecil, orientasi, dan keyboard tidak mengganggu input.
- [ ] Data Master dan stok tetap ada setelah aplikasi dipaksa tutup dan dibuka lagi.
- [ ] Backup JSON bisa disimpan, ditemukan, dibuka, dan dipulihkan.
- [ ] Pembulatan supplier dan konversi PCS → Pack sesuai uji operasional.
- [ ] BPA Gudang Bandung/Jakarta tidak menggunakan stok lintas rute.
- [ ] A4 Landscape PDF: satu supplier per halaman, maksimum empat tabel per halaman.
- [ ] Berbagi hasil dengan WhatsApp/Drive sesuai alur kerja.

**Status:** beta source Android siap untuk build, APK belum dibangun pada lingkungan ini.
