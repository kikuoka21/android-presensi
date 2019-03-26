package Tools.Message;

public class Lainnya {
    public String GetMessage(String str) {
        switch (str) {
            case "PLA0001" : return "Forbidden.";
            case "PLA0002" : return "Token yang Anda kirimkan tidak valid.";
            case "PLA0003" : return "Token yang Anda kirimkan sudah tidak valid.";
            case "PLA0004" : return "OK.";
            case "PLA0005" : return "Tidak ada data yang dapat ditampilkan.";
            case "PLA0006" : return "Tidak ada token yang dapat di validasi.";
            case "PLA0007" : return "Keyword harus di isi.";
            case "PLA0099" : return "Terjadi kesalahan. Silakan coba kembali.";
            case "PLA0008" : return "Tidak dapat menambahkan data.";
            case "PLA0009" : return "Data berhasil di tambahkan.";
            case "PLA0010" : return "Untuk saat ini aplikasi ini hanya dapat digunakan untuk mahasiswa reguler kampus pusat saja.";
            case "PLA0011" : return "Tidak dapat mengupdate data.";
            case "PLA0012" : return "Data yang anda kirimkan tidak valid (Code:JSN0x001).";
            case "PLA0013" : return "Data yang anda kirimkan tidak valid (Code:SCR0x001).";
            case "PLA0014" : return "Data yang anda kirimkan tidak valid (Code:SCR0x002).";
            case "PLA0015" : return "Data yang anda kirimkan tidak valid (Code:SCR0x003).";
            case "PLA0016" : return "Data yang anda kirimkan tidak valid (Code:SCR0x004).";
            case "PLA0017" : return "Data yang anda kirimkan tidak valid (Code:SCR0x005).";
            case "PLA0018" : return "Data yang anda kirimkan tidak valid (Code:SCR0x006).";
            case "PLA0019" : return "Data yang anda kirimkan tidak valid (Code:SCR0x007).";
            case "PLA0020" : return "Data yang anda kirimkan tidak valid (Code:SCR0x008).";
            case "PLA0021" : return "Kata sandi tidak ubah karena sama dengan kata sandi lama Anda.";
            case "PLA0022" : return "Data berhasil di update";
            case "PLA0023" : return "Maaf versi aplikasi yang anda gunakan tidak kami dukung. Silakan update aplikasi anda melalui Google Playstore.";

            default:
                return "";
        }
    }
}
