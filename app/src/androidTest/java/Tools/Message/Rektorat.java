package Tools.Message;

public class Rektorat {
    public String GetMessage(String str) {
        switch (str) {
            case "REK0001" : return "Maaf, nomor induk pegawai yang Anda masukan tidak aktif. Silakan hubungi DTI.";
            case "REK0002" : return "Maaf, Anda tidak mempunyai hak untuk mengakses aplikasi ini.";
            default: return "";
        }
    }
}
