package Tools.Message;

public class Sim {
    public String GetMessage(String str) {
        switch (str) {
            case "SIM0001" : return "Maaf, fasilitas cetak slip gaji hanya berlaku sejak bulan Mei 2011.";
            default:
                return "";
        }
    }
}
