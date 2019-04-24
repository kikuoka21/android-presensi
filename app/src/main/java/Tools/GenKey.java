package Tools;

public class GenKey {

    public String key(int str) {

        switch (str) {
            case 55:
                return "sharepref";



            case 60:
                return "id";
            case 61:
                return "token";
            case 63:
                return "status";

            default:
                return "";
        }
    }

    public String url(int str) {

        switch (str) {
            case 99:
                return "";

            default:
                return "";
        }
    }
}
