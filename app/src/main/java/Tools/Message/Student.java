package Tools.Message;

public class Student {
    public String GetStatus(String str) {
        switch (str) {
            case "MHS0001" : return "Maaf, anda tidak dapat login karena status anda sedang tidak aktif.";
            case "MHS0002" : return "-";
            case "MHS0003" : return "Maaf, Anda tidak dapat login karena status Anda sedang di cekal";
            case "MHS0004" : return "Maaf, Anda tidak dapat login karena status Anda Tanpa Keterangan";
            case "MHS0005" : return "Maaf, Anda tidak dapat login karena status Anda sudah Lulus";
            case "MHS0006" : return "Maaf, Anda tidak dapat login karena status Anda sudah Droup Out";
            case "MHS0007" : return "Maaf, Anda tidak dapat login karena status Anda sudah Undur Diri/Keluar";
            case "MHS0008" : return "Maaf, Anda tidak dapat login karena status Anda sudah Sudah Cetak Ijazah";
            case "MHS0009" : return "Surat persetujuan biodata SUDAH Anda setujui! Biodata tidak dapat di ubah lagi. Jika terdapat data yang tidak valid silakan hubungi BAAK.";
            case "MHS0010" : return "Anda tidak dapat melakukan persetujuan pada Surat Persetujuan Biodata Mahasiswa, karena Anda belum melengkapi berkas ijazah SLTA.";
            case "MHS0011" : return "Maaf, untuk saat ini data dosen pembimbing tugas akhir Anda tidak tersedia.";
            case "MHS0012" : return "Maaf, Anda tidak dapat menggunakan sistem ini secara online karena jadwal ujian belum tersedia. ";
            case "MHS0013" : return "Maaf, Anda tidak dapat menggunakan sistem ini secara online karena jadwal ujian sudah selesai.";
            case "MHS0014" : return "Maaf, Anda tidak dapat menggunakan sistem ini secara online karena jadwal ujian belum di atur oleh BAAK.";
            case "MHS0015" : return "Maaf, Untuk saat ini aplikasi yang Anda gunakan belum tersedia untuk jurusan Anda.";
            case "MHS0016" : return "Maaf, Anda tidak dapat login karena status anda : Drop out / Mengundurkan diri.";

            default: return "";
        }
    }

    public String GetMessage(String str) {
        switch (str) {
            case "STM0001": return "Aktif";
            case "STM0002": return "Cuti";
            case "STM0003": return "Cekal";
            case "STM0004": return "Tanpa Keterangan";
            case "STM0005": return "Lulus";
            case "STM0006": return "Droup Out";
            case "STM0007": return "Undur Diri/Keluar";
            case "STM0009": return "Sudah Cetak Ijazah";
            default: return "";
        }
    }
}
