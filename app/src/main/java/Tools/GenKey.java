package Tools;

import java.security.MessageDigest;

public class GenKey {
    public String url(int str) {
        String web, port, head;
        web = "192.168.0.17";
//        web = "192.168.12.17";
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


            case 308:
                return head + "/api/admin/master/siswa/input";
            case 307:
                return head + "/api/admin/master/siswa/cari";
            case 306:
                return head + "/api/admin/master/siswa/datasiswa";
            case 305:
                return head + "/api/admin/master/siswa/ubah";
            case 304:
                return head + "/api/admin/master/siswa/hapus";


            case 310:
                return head + "/api/admin/master/tanggal/list";
            case 309:
                return head + "/api/admin/master/tanggal/input";
            case 311:
                return head + "/api/admin/master/tanggal/history";
            case 312:
                return head + "/api/admin/master/tanggal/ubah";
            case 313:
                return head + "/api/admin/master/tanggal/hapus";

//                kelas
            case 320:
                return head + "/api/admin/master/kelas/input";
            case 321:
                return head + "/api/admin/master/kelas/list";
            case 322:
                return head + "/api/admin/master/kelas/isi";
            case 323:
                return head + "/api/admin/master/kelas/ubah/wali-kelas/list";
            case 324:
                return head + "/api/admin/master/kelas/ubah/wali-kelas";
            case 325:
                return head + "/api/admin/master/kelas/ubah/ketua-kelas";
            case 326:
                return head + "/api/admin/master/kelas/ubah/nama-kelas";
            case 327:
                return head + "/api/admin/master/kelas/hapus/siswa";
            case 328:
                return head + "/api/admin/master/kelas/ubah/level-siswa";
            case 329:
                return head + "/api/admin/master/kelas/hapus/kelas";
            case 330:
                return head + "/api/admin/master/kelas/tambah/siswa";
            case 331:
                return head + "/api/admin/master/kelas/tambah/list-siswa";

                //staf
            case 335:
                return head + "/api/admin/master/staf/input";
            case 336:
                return head + "/api/admin/master/staf/list";
            case 337:
                return head + "/api/admin/master/staf/data-staf";
            case 338:
                return head + "/api/admin/master/staf/ubah";
            case 339:
                return head + "/api/admin/master/staf/hapus";





            default:
                return head;
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

    public static String pesan(String key) {
        switch (key) {
            case "TOKEN1":
                return "Token Sudah Tidak Valid, Silahkan Login Kembali";
            case "TOKEN2":
                return "Token Anda Salah, Silahkan Login Kembali";

            default:
                return "Generate Pesan salah";
        }
    }
}
