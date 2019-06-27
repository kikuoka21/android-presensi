package tech.opsign.kkp.absensi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Tools.GenKey;
import Tools.InetConnection;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.admin.MainAdmin;
import tech.opsign.kkp.absensi.admin.Presensi.Carikelas_tanggal;
import tech.opsign.kkp.absensi.admin.Presensi.cari_kelas_smester;
import tech.opsign.kkp.absensi.siswa.MainSiswa;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences sp;
    private SplashScreen activity;
    private GenKey key;
    private Handler handler;
    private AsyncTask start;
    private boolean runhttpget = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_splash_screen);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        PackageInfo pInfo;

        try {
            pInfo = activity.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
//            String version = key.gen_pass("1234");
            ((TextView) findViewById(R.id.version)).setText(version);
//            Log.e("ER", version);
        } catch (Exception ignored) {

        }

//        loadIMEI();\
        md5a("aa");
//        check();

    }
    private void check(){
        for(int i=0;i<255; i++ ){
            Log.e("ER__"+i, Integer.toHexString(i));
        }
    }
    private void md5a(String s) {
        String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());



            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();

            Log.e("MD5__b_", Arrays.toString(s.getBytes()));
            Log.e("MD5__b_", String.valueOf(digest.digest()));
            Log.e("MD5__b_", String.valueOf(messageDigest));
            Log.e("MD5__b_", Arrays.toString(digest.digest()));
            Log.e("MD5__b_", Arrays.toString(messageDigest));
            int i = 0;
            for (byte aMessageDigest : messageDigest) {

                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
//                Log.e("MD5_ messageDigest ", String.valueOf(messageDigest[i]));
//                Log.e("MD5_ aMessageDigest", String.valueOf( aMessageDigest));
//                Log.e("MD5_ 0xFF & aMess", String.valueOf( 0xFF & aMessageDigest));
//                Log.e("MD5_ 0xFF", String.valueOf( 0xFF));
//                Log.e("MD5___h", h);
                hexString.append(h);
                i++;
            }
            Log.e("ER", String.valueOf(hexString));
            Log.e("ER", hexString.toString());

            Log.e("ER", toHexString(s.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static final int   INIT_A     = 0x67452301;
    private static final int   INIT_B     = (int) 0xEFCDAB89L;
    private static final int   INIT_C     = (int) 0x98BADCFEL;
    private static final int   INIT_D     = 0x10325476;
    private static final int[] SHIFT_AMTS = { 7, 12, 17, 22, 5, 9, 14, 20, 4,
            11, 16, 23, 6, 10, 15, 21    };
    private static final int[] TABLE_T    = new int[64];
    static
    {
        for (int i = 0; i < 64; i++)
            TABLE_T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
    }

    public static byte[] computeMD5(byte[] message)
    {
        int messageLenBytes = message.length;
        int numBlocks = ((messageLenBytes + 8) >>> 6) + 1;
        int totalLen = numBlocks << 6;
        byte[] paddingBytes = new byte[totalLen - messageLenBytes];
        paddingBytes[0] = (byte) 0x80;
        long messageLenBits = (long) messageLenBytes << 3;
        for (int i = 0; i < 8; i++)
        {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageLenBits;
            messageLenBits >>>= 8;
        }
        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;
        int[] buffer = new int[16];
        for (int i = 0; i < numBlocks; i++)
        {
            int index = i << 6;
            for (int j = 0; j < 64; j++, index++)
                buffer[j >>> 2] = ((int) ((index < messageLenBytes) ? message[index]
                        : paddingBytes[index - messageLenBytes]) << 24)
                        | (buffer[j >>> 2] >>> 8);
            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;
            for (int j = 0; j < 64; j++)
            {
                int div16 = j >>> 4;
                int f = 0;
                int bufferIndex = j;
                switch (div16)
                {
                    case 0:
                        f = (b & c) | (~b & d);
                        break;
                    case 1:
                        f = (b & d) | (c & ~d);
                        bufferIndex = (bufferIndex * 5 + 1) & 0x0F;
                        break;
                    case 2:
                        f = b ^ c ^ d;
                        bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                        break;
                    case 3:
                        f = c ^ (b | ~d);
                        bufferIndex = (bufferIndex * 7) & 0x0F;
                        break;
                }
                int temp = b
                        + Integer.rotateLeft(a + f + buffer[bufferIndex]
                                + TABLE_T[j],
                        SHIFT_AMTS[(div16 << 2) | (j & 3)]);
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
        }
        byte[] md5 = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++)
        {
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++)
            {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return md5;
    }

    public static String toHexString(byte[] b)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++)
        {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }



    private void loadIMEI() {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CAMERA
        };

        if(!hasPermissions(activity, PERMISSIONS)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }else{
            inten();
        }



    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int PERMISSION_ALL = 1;

        if (requestCode == PERMISSION_ALL) {
            if (hasPermissions(activity, permissions)) {
                inten();
            } else {
                new android.app.AlertDialog.Builder(activity)
                        .setTitle("Permission Request")
                        .setMessage("Wajib Memberikan Akses")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    private void inten() {
        AlertDialog alert = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert).create();
        if ((new InetConnection()).isConnected(activity)) {

            if (sp.getString("username", "").equals("") || sp.getString("token", "").equals("")) {
//            if (true) {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                inten_Login();

                            }
                        }, 2000
                );
            } else {

                mulai();
            }
        } else {
            alert.setTitle("Koneksi Error");

            alert.setMessage("Tidak dapat terkoneksi dengan server. Pastikan Anda terhubung dengan internet.");
            alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            alert.show();
        }
    }

    private void mulai() {
        start = new asyncUser().execute();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (runhttpget) {
                    start.cancel(true);
                    new android.support.v7.app.AlertDialog.Builder(activity)
                            .setTitle("Informasi")
                            .setMessage("Telah Terjadi Kesalahan Pada Koneksi Anda.")
                            .setCancelable(false)
                            .setPositiveButton("Coba Lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mulai();
                                }
                            }).setNegativeButton("Keluar Aplikasi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
                }
            }
        }, Utilities.rto());
    }


    private class Param {
        String x1d, type, key, token, akses;
    }

    private class asyncUser extends AsyncTask<Void, Void, Void> {
        private String code;
        private JSONObject json;
        private boolean background;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            background = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                Param param = new Param();
                param.x1d = sp.getString("username", "");
                param.token = sp.getString("token", "");
                param.type = "mmm";
                param.key = Utilities.imei(activity);
                param.akses = sp.getString("status", "");

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));
                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(2), p);
//                Log.e("ER_", json.toString(3));
//
                code = json.getString("code");

            } catch (Exception e) {
                e.printStackTrace();
                background = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                runhttpget = false;
//               Log.e("xp455", json.toString(3));

                handler.removeCallbacksAndMessages(null);
                if (background) {
                    if (code.equals("OK4")) {

                        try {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("thn_ajar", json.getString("thn-ajar"));
                            editor.putString("tanggal", json.getString("tanggal"));
                            json = json.getJSONObject("data");
                            editor.putString("nama", json.getString("nama"));
                            editor.putString("level", json.getString("level"));
                            editor.commit();
                            new Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent homeIntent;
                                            if (sp.getString("status", "").equals("1")) {
                                                Log.d("yeyy", "1");
                                                homeIntent = new Intent(activity, MainAdmin.class);
//                                                homeIntent = new Intent(activity, test_layout.class);
//                                                homeIntent = new Intent(activity, Carikelas_tanggal.class);
//                                                homeIntent = new Intent(activity, ubah_kelas.class);
                                            } else {
                                                Log.d("yeyy", "2");
                                                homeIntent = new Intent(activity, MainSiswa.class);
                                            }
                                            startActivity(homeIntent);
                                            finish();
                                        }
                                    }, 2000
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", "");
                        editor.putString("token", "");
                        editor.commit();
                        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                        ab
                                .setCancelable(false).setTitle("Informasi")
                                .setMessage(code)
                                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        inten_Login();
                                    }
                                })
                                .show();
                    }

                } else {
                    Utilities.codeerror(activity, "ER0211");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inten_Login(){
        startActivity(new Intent(activity, Login.class));
        finish();
    }
}
