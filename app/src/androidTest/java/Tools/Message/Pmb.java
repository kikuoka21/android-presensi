package Tools.Message;

public class Pmb {
    public String GetMessage(String str) {
        switch (str) {
            case "PMB0001": return "Tidak dapat melakukan pendaftaran dikarenakan sekarang bukan waktu untuk mendaftar.";
            case "PMB0002": return "Nomor pendaftaran belum di setting.";
            case "PMB0003": return "Kartu pendaftar tersebut tidak di temukan.";
            case "PMB0004": return "Panduan tidak ditemukan.";
            case "PMB0005": return "Tidak dapat melakukan pendaftaran (Kode:PMB0005).";
            case "PMB0006": return "Tidak dapat melakukan pendaftaran (Kode:PMB0006).";
            case "PMB0007": return "Tidak dapat melakukan pendaftaran (Kode:PMB0007).";
            case "PMB0008": return "Tidak dapat login dikarenakan nomor pendaftaran Anda tidak valid.";
            default:
                return "";
        }
    }
}
