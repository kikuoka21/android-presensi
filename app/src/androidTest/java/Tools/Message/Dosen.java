package Tools.Message;

public class Dosen {
    public String GetMessage(String str) {
        switch (str) {
            case "BAC0001" : return "Insert berita acara berhasil.";
            case "BAC0002" : return "Insert berita acara gagal.";
            case "BAC0003" : return "Tidak diizinkan menginput berita acara. Mata kuliah yang bersangkutan belum dimulai.";
            case "BAC0004" : return "Insert absen dosen berhasil.";
            case "BAC0005" : return "Insert absen dosen gagal.";
            case "BAC0006" : return "Update berita acara berhasil.";
            case "BAC0007" : return "Update berita acara gagal.  ";
            case "BAC0008" : return "Validasi gagal, tidak ada rencana absen tersedia.";
            case "IAM0001" : return "Berhasil insert absen mahasiswa.";
            case "IAM0002" : return "Gagal insert absen mahasiswa.";
            case "IAM0003" : return "Berhasil update absen mahasiswa.";
            case "IAM0004" : return "Gagal update absen mahasiswa.";
            case "IAM0005" : return "Tidak diizinkan menginput absen mahasiswa. Mata kuliah yang bersangkutan belum dimulai.";
            case "TGP0001" : return "Berhasil insert tanggal pertemuan.";
            case "TGP0002" : return "Gagal insert tanggal pertemuan.";
            case "TGP0003" : return "Berhasil update tanggal pertemuan.";
            case "TGP0004" : return "Gagal update tanggal pertemuan.";
            case "INM0001" : return "Berhasil update nilai mahasiswa.";
            case "INM0002" : return "Gagal update nilai mahasiswa.";
            case "INM0003" : return "Gagal update log update nilai.  ";
            case "UPL0001" : return "Berhasil upload foto profil dosen.";
            case "UPL0002" : return "Gagal upload foto profil dosen.";
            case "JDS0001" : return "Jadwal sidang belum diatur.";
            case "KSD0001" : return "Mata Kuliah tersebut bukan mata kuliah Anda.";
            default:
                return "";
        }
    }
}
