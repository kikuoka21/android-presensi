package Tools.Message;

public class Input {
    public String GetMessage(String str) {
        switch (str) {
            case "INP0001" : return "Nomor induk mahasiswa harus di isi. ";
            case "INP0002" : return "Nomor induk mahasiswa yang Anda masukan salah.";
            case "INP0003" : return "Nomor induk mahasiswa yang Anda masukan tidak benar.";
            case "INP0004" : return "Panjang karakter nomor induk yang Anda masukan tidak sesuai.";
            case "INP0005" : return "Nomor induk hanya dapat di isi dengan angka saja sebanyak 10 karakter. ";
            case "INP0006" : return "Nomor induk pegawai harus di isi.";
            case "INP0007" : return "Nomor induk pegawai yang Anda masukan salah.  ";
            case "INP0008" : return "Nomor induk pegawai yang Anda masukan tidak benar.  ";
            case "INP0009" : return "Nomor induk mahasiswa atau kata sandi yang Anda masukan tidak sesuai.";
            case "INP0010" : return "Nomor induk hanya dapat di isi dengan angka saja sebanyak 10 karakter. ";
            case "INP0011" : return "Nomor induk mahasiswa yang anda kirimkan tidak terdaftar di dalam database.";
            case "INP0013" : return "Nomor induk pegawai yang anda kirimkan tidak terdaftar di dalam database.";
            case "INP0014" : return "Kata sandi harus di isi.";
            case "INP0015" : return "Kata sandi yang Anda masukan tidak benar.";
            case "INP0016" : return "Nomor induk mahasiswa yang anda masukan tidak valid.";
            case "INP0017" : return "Tahun ajaran yang Anda masukan tidak benar.";
            case "INP0018" : return "Semester yang Anda masukan tidak benar.";
            case "INP0019" : return "Maaf, data nomor induk pegawai yang anda kirimkan tidak valid.";
            case "INP0020" : return "Format tahun yang Anda kirimkan tidak sesuai.";
            case "INP0021" : return "Format semester yang Anda kirimkan tidak sesuai.";
            case "INP0022" : return "Jenjang harus di isi.";
            case "INP0023" : return "Jenjang yang Anda masukan tidak sesuai.";
            case "INP0024" : return "Fakultas harus di isi.";
            case "INP0025" : return "Fakultas yang Anda masukan tidak sesuai.";
            case "INP0012" : return "Universitas asal harus di isi.";
            case "INP0026" : return "Nama harus di isi.";
            case "INP0027" : return "Jenjang yang Anda masukan tidak terdaftar di dalam database.";
            case "INP0028" : return "Program studi harus di isi.";
            case "INP0029" : return "Program studi yang Anda masukan tidak terdaftar di dalam database.";
            case "INP0030" : return "Nomor Hp harus di isi.";
            case "INP0031" : return "Nomor Hp yang Anda masukan tidak sesuai.";
            case "INP0032" : return "Alamat email harus di isi.";
            case "INP0033" : return "Format email yang Anda masukan salah.";
            case "INP0034" : return "Asal SMA harus di isi.";
            case "INP0035" : return "SMA yang anda masukan tidak terdaftar di dalam database.";
            case "INP0036" : return "Email yang anda masukan sudah terdaftar di dalam database.";
            case "INP0037" : return "Nomor pendaftaran harus di isi.";
            case "INP0038" : return "Panjang nomor pendaftaran tidak benar.";
            case "INP0039" : return "Token harus di isi";
            case "INP0040" : return "token yang anda masukan tidak valid.";
            case "INP0041" : return "Nomor test harus di isi.";
            case "INP0042" : return "Panjang nomor test yang Anda masukan tidak sesuai.";
            case "INP0043" : return "Kode mata kuliah yang anda masukan tidak benar.";
            case "INP0044" : return "Kelompok yang anda masukan tidak benar.";
            case "INP0045" : return "Program Studi yang Anda masukan tidak sesuai.";
            case "INP0046" : return "Universitas tidak terdaftar.";
            case "INP0047" : return "Account tersebut tidak terdaftar.";
            case "INP0048" : return "Password lama yang Anda masukan tidak sesuai.";
            case "INP0049" : return "Password baru harus di isi.";
            case "INP0050" : return "Password konfirmasi tidak sesuai.";
            case "INP0051" : return "Password berhasil di update.";
            case "INP0052" : return "Panjang password baru minimal 6 karakter.";
            case "INP0053" : return "Password konfirmasi harus di isi.";
            case "INP0054" : return "Password konfirmasi tidak sesuai.";
            case "INP0055" : return "Nomor test atau Kata sandi yang Anda masukan tidak sesuai.";
            case "INP0056" : return "Nomor induk pegawai atau kata sandi yang Anda masukan tidak sesuai.";
            case "INP0057" : return "-";
            case "INP0058" : return "Form tidak valid.";
            case "INP0059" : return "Kata sandi yang lama harus di isi.";
            case "INP0060" : return "Kata sandi lama yang Anda masukan tidak sesuai.";
            case "INP0061" : return "Kata sandi yang baru harus di isi.";
            case "INP0062" : return "Kata sandi yang baru harus memili panjang minimal 6 karakter.";
            case "INP0063" : return "Konfirmasi kata sandi yang baru harus sama dengan kata sandi yang baru Anda masukan.";
            case "INP0064" : return "Periode harus di isi.";
            case "INP0065" : return "Periode yang Anda kirimkan tidak benar.";

            default:
                return "";
        }
    }
}