package tech.opsign.kkp.absensi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import tech.opsign.kkp.absensi.Parent.MainParent2;
import tech.opsign.kkp.absensi.admin.MainAdmin;
import tech.opsign.kkp.absensi.siswa.MainSiswa;

public class Login_Parent extends AppCompatActivity {

    private EditText jnip;
    private Login_Parent activity;
    private EditText jpassword;
    private GenKey key;
    private Handler handler;
    private AsyncTask start;
    private Button tombol;
    private String str_username;
    private String str_pass;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_parent);

        this.activity = this;
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.hitam));
        }
        key = new GenKey();
        handler = new Handler();
        jnip = findViewById(R.id.nip);
        jpassword = findViewById(R.id.password);


        jnip.addTextChangedListener(logintextwarcher);
        jpassword.addTextChangedListener(logintextwarcher);

        sp = activity.getSharedPreferences("shared", 0x0000);

        tombol = findViewById(R.id.login);
        tombol.setEnabled(true);

        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closekeyboard();
                str_username = jnip.getText().toString().trim();
                str_pass = jpassword.getText().toString().trim();
                jnip.setError(null);
                jpassword.setError(null);
                mulai();


            }
        });

        Button tombolbiasa = findViewById(R.id.login_biasa);
        tombolbiasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closekeyboard();

                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("usertype", true);
                editor.apply();

                Intent login = new Intent(activity, SplashScreen.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();

            }
        });


    }

    private class Param {
        String x1d, type, key, xp455;
    }

    @SuppressLint("StaticFieldLeak")
    private class asyncUser extends AsyncTask<Void, Void, Void> {

        private JSONObject json;
        private boolean background;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            background = true;
//            dialog = new ProgressDialog(activity);
//            dialog.setMessage("Sedang memproses data. Harap tunggu sejenak.");
//            dialog.setCancelable(false);
//            dialog.show();
            key.showProgress(activity, true);

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
                json = jParser.getJSONFromUrl(key.url(401), p);
                Log.e("param login ", gson.toJson(param));
                Log.e("isi json login", json.toString(2));

            } catch (Exception e) {
                e.printStackTrace();
                background = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
            key.hideProgress();
            handler.removeCallbacksAndMessages(null);

            if (background) {

                try {
                    if (json.getBoolean("hasil")) {


                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", str_username);
                        editor.putString("token", json.getString("token"));
                        editor.putString("thn_ajar", json.getString("thn-ajar"));
                        editor.putString("tanggal", json.getString("tanggal"));
//
                        editor.putString("nama", json.getString("nama"));
                        editor.apply();

                        startActivity(new Intent(activity, MainParent2.class));
                        finish();
                        Log.e("ER_", "berhasil boii parent");

                    } else {
                        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                        ab
                                .setCancelable(false).setTitle("Informasi")
                                .setMessage(json.getString("message"))
                                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utilities.codeerror(activity, "ER0211");
            }
        }
    }
    private void closekeyboard() {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {

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
                tombol.setBackground(ContextCompat.getDrawable(activity, R.drawable.button));
            else
                tombol.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_deny));
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
                if (key.progres_isShowing()) {
                    key.hideProgress();
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


}






