package Tools;

public class appKey {
    static {
        System.loadLibrary("strkey-lib");
        System.loadLibrary("strurl-lib");
    }

    private native String GetUrl(int a);

    private native String GetKey(int a);

    private native String CreateKey(String a);

    private native String encodeStr(String a);

    private native String decodeStr(String a);

    public String key(int a) {
        return GetKey(a);
    }

    public String url(int a) {
        return GetUrl(a);
    }

    public String key2(String a) {
        return CreateKey(a);
    }

    public String encode(String a) {
        return encodeStr(a);
    }

    public String decode(String a) {
        return decodeStr(a);
    }
}
