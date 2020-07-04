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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.InetConnection;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Parent.MainParent2;
import tech.opsign.kkp.absensi.admin.MainAdmin;
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
            ((TextView) findViewById(R.id.version)).setText("Versi "+version + "\nIcons made by Freepik\nfrom www.flaticon.com is licensed by CC 3.0 BY");
//            Log.e("ER", version);
        } catch (Exception ignored) {

            ((TextView) findViewById(R.id.version)).setText("Versi -" + "\nIcons made by Freepik\nfrom www.flaticon.com is licensed by CC 3.0 BY");
        }

        loadIMEI();
//        check();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("ER_", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        if (!token.equals("")) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token_firebase", token);
                            editor.apply();
                        }

                        // Log and toast
//                        Log.e("token firebase", token);

                    }
                });
    }

    private void check() {
        String url;
        int a = 0;
        for (int i = 1; i < 500; i++) {
            url = key.url(i);
            if (!url.equals(key.url(888))) {

                Log.e("__genkey " + i, url);
                a++;
            }
        }

        Log.e("ER total ", "" + a);
    }


    private void loadIMEI() {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CAMERA
        };
//        String[] PERMISSIONS = { android.Manifest.permission.CAMERA};

        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        } else {
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
                    new androidx.appcompat.app.AlertDialog.Builder(activity)
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
        boolean wali;
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
                param.wali = !sp.getBoolean("usertype", true);


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));
                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(2), p);
                Log.e("ER_splashscreen", json.toString(3));
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
                            /**
                             * true mewakili user siswa
                             * false mewakili user wali

                             */
//        Log.e("ER", sp.getBoolean("usertype", true) + " usernya");
                            if (sp.getBoolean("usertype", true)) {

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("thn_ajar", json.getString("thn-ajar"));
                                editor.putString("tanggal", json.getString("tanggal"));
                                json = json.getJSONObject("data");
                                editor.putString("nama", json.getString("nama"));
                                editor.putString("level", json.getString("level"));
                                editor.apply();
                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent homeIntent;
                                                if (sp.getString("status", "").equals("1")) {
                                                    homeIntent = new Intent(activity, MainAdmin.class);
//                                                homeIntent = new Intent(activity, test_layout.class);
//                                                homeIntent = new Intent(activity, Carikelas_tanggal.class);
//                                                homeIntent = new Intent(activity, ubah_kelas.class);
                                                } else {
                                                    homeIntent = new Intent(activity, MainSiswa.class);
//                                                homeIntent = new Intent(activity, cari_presensi.class);

                                                }
                                                startActivity(homeIntent);
                                                finish();
                                            }
                                        }, 1500
                                );
                            } else {
//                                Utilities.showMessageBox(activity, "Informasi", "berhasil token parent");
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("thn_ajar", json.getString("thn-ajar"));
                                editor.putString("tanggal", json.getString("tanggal"));
//
                                editor.putString("nama", json.getString("nama"));
                                editor.apply();


                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {

                                                startActivity(new Intent(activity, MainParent2.class));
//                                                startActivity(new Intent(activity, Tanggal_Libur.class));
                                                finish();
                                                Log.e("ER_", "berhasil boii parent");
                                            }
                                        }, 1500
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", "");
                        editor.putString("token", "");
                        editor.apply();
                        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                        ab
                                .setCancelable(false).setTitle("Informasi")
                                .setMessage(GenKey.pesan(code))
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

    private void inten_Login() {
//        startActivity(new Intent(activity, Login.class));
//        if (sp.getString("type", "").equals("") || sp.getString("token", "").equals("")) {
        /**
         true mewakili user siswa
         false mewakili user wali
         */
//        Log.e("ER", sp.getBoolean("usertype", true) + " usernya");
        if (sp.getBoolean("usertype", true)) {

            startActivity(new Intent(activity, Login.class));
        } else {

            startActivity(new Intent(activity, Login_Parent.class));
        }
//        }else

        finish();
    }
}
