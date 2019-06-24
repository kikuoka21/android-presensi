package tech.opsign.kkp.absensi.admin.Presensi.tool_semester;

public class Model_laporan_smes {
    public String nis, nama, sakit, izin, alpha, hadir;

    public Model_laporan_smes(String nis, String nama, String sakit, String izin, String alpha, String hadir) {
        this.nis = nis;
        this.nama = nama;
        this.sakit = sakit;
        this.izin = izin;
        this.alpha = alpha;
        this.hadir = hadir;
    }
}
