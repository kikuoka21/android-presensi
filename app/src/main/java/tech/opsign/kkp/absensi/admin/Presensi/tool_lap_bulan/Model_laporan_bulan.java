package tech.opsign.kkp.absensi.admin.Presensi.tool_lap_bulan;

public class Model_laporan_bulan {
    public String nis, nama, sakit, izin, alpha, hadir, detil;

    public Model_laporan_bulan(String nis, String nama, String sakit, String izin, String alpha, String hadir, String detil) {
        this.nis = nis;
        this.nama = nama;
        this.sakit = sakit;
        this.izin = izin;
        this.alpha = alpha;
        this.hadir = hadir;
        this.detil = detil;
    }
}
