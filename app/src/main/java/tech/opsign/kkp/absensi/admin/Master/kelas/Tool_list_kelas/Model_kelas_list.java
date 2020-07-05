package tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_kelas;

public class Model_kelas_list {
    public String kd_kelas, kelas, wali_nama, ketua_nama;

    public Model_kelas_list(String kd_kelas, String kelas, String wali_nama, String ketua_nama) {
        this.kd_kelas = kd_kelas;
        this.kelas = kelas;
        this.wali_nama = wali_nama;
        this.ketua_nama = ketua_nama;
    }

    public String getKd_kelas() {
        return kd_kelas;
    }

    public String getKelas() {
        return kelas;
    }

    public String getWali_nama() {
        return wali_nama;
    }

    public String getKetua_nama() {
        return ketua_nama;
    }
}
