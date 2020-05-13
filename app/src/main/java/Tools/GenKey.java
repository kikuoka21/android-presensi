package Tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Window;
import android.widget.ImageView;

import java.security.MessageDigest;

import tech.opsign.kkp.absensi.R;

public class GenKey {
    public String url(int str) {
        String web, port, head;
        //web = "192.168.0.17";
        web = "192.168.1.5";
        //web = "192.168.0.18";
        //web = "192.168.12.17";
        //web = "192.168.12.33";
        //web = "192.168.43.242";
        port = ":8000";
        head = "http://" + web + port;
        switch (str) {
            case 1:
                return head + "/api/auth/login";
            case 2:
                return head + "/api/auth/check-token";
            case 3:
                return head + "/api/auth/ganti-pswd";


            //siswa
            case 101:
                return head + "/api/siswa/presensi/buat";

            case 102:
                return head + "/api/siswa/presensi/isi";

            case 100:
                return head + "/api/siswa/dashboard";

            case 103:
                return head + "/api/siswa/profil";
            case 104:
                return head + "/api/siswa/presensi/saya";
            case 105:
                return head + "/api/siswa/presensi/lihat/harian";


            //admin

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
            case 313:
                return head + "/api/admin/master/tanggal/hapus";

            //kelas
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

            //
            case 340:
                return head + "/api/admin/laporan-smes";

            case 341:
                return head + "/api/admin/lihat";
            case 342:
                return head + "/api/admin/laporan-bulan";
            case 343:
                return head + "/api/admin/absen-ubah/getdata";
            case 344:
                return head + "/api/admin/absen-ubah/per-siswa";
            case 345:
                return head + "/api/admin/absen-ubah/per-kelas";


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

    private Dialog mDialog;

    public void showProgress(Context context, boolean cancelable) {
        mDialog = new Dialog(context);
        // no tile for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_loading);


        mDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
