package tech.opsign.kkp.absensi.admin.Master;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.SplashScreen;

public class GantiPass_User extends AppCompatActivity {

    private GenKey key;
    private GantiPass_User activity;
    private SharedPreferences sp;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private EditText pas_baru_1, pas_baru_2, pass;
    private Button tombol;
    private String pas1_string, pas2_string, passadmin, username_target;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ganti_pass);


        setTitle("Ganti Password User");

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        Intent intent = getIntent();
        username_target = intent.getStringExtra("username");
        ((TextView) findViewById(R.id.usernametarget)).setText(username_target);
        ((TextView) findViewById(R.id.namatarget)).setText(intent.getStringExtra("nama"));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.password_lama).setVisibility(View.GONE);
        findViewById(R.id.judulpasss).setVisibility(View.GONE);
        pas_baru_1 = findViewById(R.id.password_baru1);
        pas_baru_2 = findViewById(R.id.password_baru2);

        pas_baru_1.addTextChangedListener(logintextwarcher);
        pas_baru_2.addTextChangedListener(logintextwarcher);

        tombol = findViewById(R.id.tombol_ubah);
        tombol.setText("Ganti Password");
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pas_baru_2.setError(null);
                if (pas1_string.equals(pas2_string)) {
                    Intent intent = getIntent();
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                    builder.setTitle("Informasi");
                    builder.setMessage("Apakah anda yakin ingin mengganti password untuk user " + intent.getStringExtra("username") + ", \nMasukan Password anda");
                    pass = new EditText(activity);
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(pass);
                    builder.setPositiveButton("kirim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            passadmin = key.gen_pass(pass.getText().toString().trim());
                            dialog.dismiss();
                            mulai();
                        }
                    });
                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    android.app.AlertDialog ad = builder.create();
                    ad.show();


                    Log.e("ER", "mulai");
                } else {
                    pas_baru_2.setError("Password Baru Tidak Sama");
                }


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher logintextwarcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            pas1_string = pas_baru_1.getText().toString().trim();
            pas2_string = pas_baru_2.getText().toString().trim();
            boolean b = !pas1_string.isEmpty() && !pas2_string.isEmpty();
            tombol.setEnabled(b);

            if (b)
                tombol.setBackground(ContextCompat.getDrawable(activity, R.drawable.button));
            else
                tombol.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_deny));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void mulai() {
        start = new callAPI().execute();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    start.cancel(true);
                    new AlertDialog.Builder(activity)
                            .setTitle("Informasi")
                            .setMessage("Telah Terjadi Kesalahan Pada Koneksi Anda.")
                            .setCancelable(false)
                            .setPositiveButton("Coba Lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mulai();
                                }
                            }).show();
                }
            }
        }, Utilities.rto());
    }


    private class callAPI extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        private class Param {
            String x1d, type, key, token, xp4s5, xp4s5_lama, username_target;
        }


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
                param.x1d = sp.getString("username", "");
                param.type = "mmm";
                param.key = Utilities.imei(activity);
                param.token = sp.getString("token", "");
                param.xp4s5 = key.gen_pass(pas2_string);
                param.xp4s5_lama = passadmin;
                param.username_target = username_target;
                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(3), p);
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
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setCancelable(false).setTitle("Informasi");
                if (code.equals("OK4")) {
                    ab
                            .setMessage("Password Berhasil Ganti")
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                } else if (code.equals("TOKEN2") || code.equals("TOKEN1")) {
                    SharedPreferences.Editor editorr = sp.edit();
                    editorr.putString("username", "");
                    editorr.putString("token", "");
                    editorr.commit();

                    ab.setMessage(GenKey.pesan(code)).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            Intent login = new Intent(activity, SplashScreen.class);
                            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                            finish();
                        }
                    })
                            .show();
                } else {
                    ab.setMessage(code)
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

}
