package tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard;

public class Model {
        public String nis,nama, kelas, alasan;

    public Model(String nis, String nama, String kelas, String alasan) {
        this.nis = nis;
        this.nama = nama;
        this.kelas = kelas;
        this.alasan = alasan;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }
}
