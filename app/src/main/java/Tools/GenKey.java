package Tools;

import java.security.MessageDigest;

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
        web = "192.168.0.17";
        port = ":8000";

        switch (str) {
            case 1:
                return "http://" + web + port + "/api/auth/login";
            case 2:
                return "http://" + web + port + "/api/auth/check-token";


            case 101:
                return "http://" + web + port + "/api/siswa/presensi/buat";
            case 102:
                return "http://" + web + port + "/api/siswa/presensi/isi";
            case 100:
                return "http://" + web + port + "/api/siswa/dashboard";

            default:
                return "";
        }
    }

    public String gen_pass(String pass) {

        return md5("%" + md5(pass) + " secret keynya ad@l@h al-kamal!");
    }

    private String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
