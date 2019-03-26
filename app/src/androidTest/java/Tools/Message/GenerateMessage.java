package Tools.Message;

public class GenerateMessage {
    public String getMessage(String str) {
        if (str.trim().isEmpty())
            return "";

        switch (str.substring(0, 3)) {
            case "PLA":
                return (new Lainnya()).GetMessage(str.trim());
            case "DOS":
            case "BAC":
            case "IAM":
            case "TGP":
                return (new Dosen()).GetMessage(str.trim());
            case "PAR":
                return (new Parent()).GetMessage(str.trim());
            case "PMB":
                return (new Pmb()).GetMessage(str.trim());
            case "REK":
                return (new Rektorat()).GetMessage(str.trim());
            case "STD":
                return (new Student()).GetMessage(str.trim());
            case "INP":
                return (new Input()).GetMessage(str.trim());
            case "MHS":
                return (new Student()).GetStatus(str.trim());
            case "SIM":
                return (new Sim().GetMessage(str.trim()));
            default:
                return "";
        }
    }
}
