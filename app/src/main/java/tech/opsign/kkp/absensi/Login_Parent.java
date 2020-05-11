package tech.opsign.kkp.absensi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.admin.MainAdmin;
import tech.opsign.kkp.absensi.siswa.MainSiswa;

public class Login_Parent extends AppCompatActivity {

    private EditText jnip;
    private Login_Parent activity;
    private EditText jpassword;
    private GenKey key;
    private ProgressDialog dialog;
    private Handler handler;
    private AsyncTask start;
    private Button tombol;
    private String str_username;
    private String str_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.activity = this;
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.hitam));
        }
        key = new GenKey();
        dialog = new ProgressDialog(activity);
        handler = new Handler();
        jnip = findViewById(R.id.nip);
        jpassword = findViewById(R.id.password);


        jnip.addTextChangedListener(logintextwarcher);
        jpassword.addTextChangedListener(logintextwarcher);

        SharedPreferences sp = activity.getSharedPreferences("shared", 0x0000);

        tombol = findViewById(R.id.login);
        tombol.setEnabled(true);

        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_username = jnip.getText().toString().trim();
                str_pass = jpassword.getText().toString().trim();
                jnip.setError(null);
                jpassword.setError(null);
                mulai();


            }
        });


    }

    private class Param {
        String x1d, type, key, xp455;
    }

    @SuppressLint("StaticFieldLeak")
    private class asyncUser extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            background = true;
            dialog = new ProgressDialog(activity);
            dialog.setMessage("Sedang memproses data. Harap tunggu sejenak.");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                Param param = new Param();
                param.x1d = str_username;
                param.xp455 = key.gen_pass(str_pass);
                param.type = "mmm";
                param.key = Utilities.imei(activity);

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(1), p);
                Log.e("param login ", gson.toJson(param));
                Log.e("isi json login", json.toString(2));
                code = json.getString("code");

            } catch (Exception e) {
                background = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            handler.removeCallbacksAndMessages(null);

            if (background) {

                if (code.equals("OK4")) {
                    try {
                        json = json.getJSONObject("data");

                        String status = json.getString("status");
                        Intent homeIntent;
                        if (status.equals("1")) {
                            //aksen admin
                            Log.d("yeyy", "1");
                            homeIntent = new Intent(activity, MainAdmin.class);
                        } else {
                            //siswa
                            Log.d("yeyy", "2");
                            homeIntent = new Intent(activity, MainSiswa.class);

                        }
                        SharedPreferences sp = activity.getSharedPreferences("shared", 0x0000);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", str_username);
                        editor.putString("status", status);
                        editor.putString("token", json.getString("token"));
                        editor.putString("thn_ajar", json.getString("thn-ajar"));
                        editor.putString("tanggal", json.getString("tanggal"));

                        json = json.getJSONObject("data_pribadi");
                        editor.putString("nama", json.getString("nama"));
                        editor.putString("level", json.getString("level"));


                        editor.commit();
                        startActivity(homeIntent);
                        finish();
                        Log.e("ER_", "berhasil boii");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                    ab
                            .setCancelable(false).setTitle("Informasi")
                            .setMessage(code)
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }

            } else {
                Utilities.codeerror(activity, "ER0211");
            }
        }
    }


    private TextWatcher logintextwarcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String user = jnip.getText().toString().trim();
            String pass = jpassword.getText().toString().trim();


            tombol.setEnabled(!user.isEmpty() && !pass.isEmpty());
            if (!user.isEmpty() && !pass.isEmpty())
                tombol.setBackground(ContextCompat.getDrawable(activity,R.drawable.button ));
            else
                tombol.setBackground(ContextCompat.getDrawable(activity,R.drawable.button_deny ));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void mulai() {
        Log.e("ER", "start");
        start = new asyncUser().execute();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
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


}






