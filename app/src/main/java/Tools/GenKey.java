package Tools;

public class GenKey {

    public String key(int str) {

        switch (str) {
            case 2://id
                return "id";
            case 1://id
                return "token";

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
