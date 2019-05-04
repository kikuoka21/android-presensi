package tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard;

public class Model {
        public String nis,nama, kelas, alasan;
        public boolean type;

    public Model(String nis, String nama, String kelas, String alasan, boolean type) {
        this.nis = nis;
        this.nama = nama;
        this.kelas = kelas;
        this.alasan = alasan;
        this.type = type;
    }
}
