package tech.opsign.kkp.absensi;

import android.Manifest;
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

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.InetConnection;
import Tools.JsonParser;
import Tools.Utilities;
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

        loadIMEI();
    }

    private void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);
//            requestReadPhoneStatePermission();
        } else {
            inten();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

            if (sp.getString("username", "").equals("") && sp.getString("token", "").equals("")) {
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
//                Log.e("ER_", key.url(2));
//                Log.e("ER_", String.valueOf(p));
//                Log.e("ER_", "asdwad");
                json = jParser.getJSONFromUrl(key.url(2), p);
                Log.e("ER_", json.toString(3));
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
                                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
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
