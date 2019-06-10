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
        String web, port, head;
        web = "192.168.0.17";
        port = ":8000";
        head = "http://" + web + port;
        switch (str) {
            case 1:
                return head + "/api/auth/login";
            case 2:
                return head + "/api/auth/check-token";
            case 3:
                return head + "/api/auth/ganti-pswd";


//                siswa
            case 101:
                return head + "/api/siswa/presensi/buat";
            case 102:
                return head + "/api/siswa/presensi/isi";
            case 100:
                return head + "/api/siswa/dashboard";
            case 103:
                return head + "/api/siswa/profil";


//                admin

            case 300:
                return head + "/api/admin/dashboard";

            case 310:
                return head + "/api/admin/master/lihat/tanggal";
            case 309:
                return head + "/api/admin/master/tanggal";

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
