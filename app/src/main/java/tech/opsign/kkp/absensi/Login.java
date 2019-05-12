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
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


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

public class Login extends AppCompatActivity {

    private EditText jnip;
    private Login activity;
    private EditText jpassword;
    private GenKey key;
    private ProgressDialog dialog;
    private Handler handler;
    private AsyncTask start;
    private Button tombol;


    @SuppressLint("StaticFieldLeak")
    private class asyncUser extends AsyncTask<Void, Void, Void> {
        private String Str_nip;
        private String Str_pass;
        private String code;
        private boolean background;


        private asyncUser(String str_nip, String str_pass) {
            Str_nip = str_nip.trim();
            Str_pass = str_pass.trim();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            background = true;


            if (Str_nip.equals("") || Str_pass.equals(""))
                this.cancel(true);
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

//
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair(key.key(145), ""));

                JsonParser jParser = new JsonParser();
//                JSONObject json = jParser.getJSONFromUrl(key.url(1), p);
//                Log.e("param login ", xdata);
//                Log.e("isi json login", json.toString());


//                code = json.getString("code");
                code = "ok";
//                Log.e("token", json.toString(3));

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
            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
            if (background) {
                if (code.equals("ok")) {
                    if (jnip.getText().toString().trim().equals("1")) {
                        Log.d("yeyy", "1");
                        startActivity(new Intent(activity, MainAdmin.class));
                        finish();
                    } else {
                        Log.d("yeyy", "2");
                        startActivity(new Intent(activity, MainSiswa.class));
                        finish();

                    }
                } else {
                    ab
                            .setCancelable(false).setTitle("Informasi")
                            .setMessage("gagal")
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
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


        tombol = findViewById(R.id.login);
        tombol.setEnabled(true);

        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_nip = jnip.getText().toString().trim();

                String str_pass = jpassword.getText().toString().trim();

                jnip.setError(null);
                jpassword.setError(null);
                mulai(str_nip, str_pass);


            }
        });


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

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void mulai(final String nip, final String pass) {
        Log.e("ER", "start");
        start = new asyncUser(nip, pass).execute();
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
                                    mulai(nip, pass);
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

    private boolean isEmailValid(String nip) {
//        return nip.length() == 6;
        return true;
    }


//    private class asyncUser extends AsyncTask<Void, Void, Void> {
//        String Str_nim;
//        String Str_password;
//        String Str_user_type;
//        private String responseMessage;
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//                List<NameValuePair> p = new ArrayList<NameValuePair>();
//                String user_type = key.key(88);
//                imei1 = Utilities.imei(getApplicationContext());
//
//                Gson gson = new Gson();
//                Seeder_Login sl = new Seeder_Login(str_nim, key.key2(str_password), imei1, user_type);
//                p.add(new BasicNameValuePair(key.key(2003), key.encode(gson.toJson(sl))));
//
//
//                JsonParser jParser = new JsonParser();
//                json = jParser.GetJSONObjectEncrypted(key.url(1), p);
//
//                code = json.getString(key.key(44));
//                json2 = json.getJSONObject(key.key(2004));
//
//                if (code.equals(key.key(999))) {
//
//                    // init shared preferences
//                    SharedPreferences sp = getApplicationContext().getSharedPreferences(key.key(1000), 0x0000);
//
//                    // init editor buat edit data di sharedpreferences
//                    SharedPreferences.Editor editor = sp.edit();
//
//                    // set value ([index], [valuenya]);
//                    editor.putString(key.key(1001), str_nim);
//                    editor.putString(key.key(1002), json2.getString(key.key(2001)));
//                    editor.putString(key.key(1956), json2.getString(key.key(2044)));
//                    editor.putString(key.key(1970), json2.getString(key.key(2058)));
//                    editor.putString(key.key(1972), json2.getString(key.key(9002)));
//                    // save data di sharedpreferences
//                    editor.apply();
//                    if (json2.getString(key.key(2058)).equals(key.key(88))) {
//                        Log.e("hasil", "0");
//                    } else {
//                        Log.e("hasil", "Non 0");
//                    }
//
//                }
//
//            } catch (Exception e) {
//                Log.e("E_LOGIN_A02", e.getMessage(), e);
//                code = key.key(9999);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            AlertDialog.Builder ab = new AlertDialog.Builder(HalamanLogin.this);
//            try {
//                code = json.getString(key.key(44));
//
//                if (code.equals(key.key(999))) {
//                    Log.e("ERR_1", "masuk");
//                    startActivity(new Intent(HalamanLogin.this, MainActivity.class));
//                    finish();
//                } else {
//                    Log.e("ERR_1", "not");
//                    ab
//                            .setTitle("Informasi")
//                            .setMessage(pesan.getMessage(code))
//                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .show();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
//
//        }
//    }


}






