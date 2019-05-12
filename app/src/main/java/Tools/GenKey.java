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
        String web, port;
        web = "127.0.0.1";
        port = ":8000";

        switch (str) {
            case 1:
                return "http://"+web+port+"/api/auth/login";
            case 2:
                return "http://"+web+port+"/api/auth/check-token";

            default:
                return "";
        }
    }
}
