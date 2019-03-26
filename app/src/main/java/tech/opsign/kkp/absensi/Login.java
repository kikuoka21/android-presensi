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


import Tools.GenKey;
import Tools.Message.GenerateMessage;
import Tools.Utilities;

public class Login extends AppCompatActivity {

    private EditText jnip;
    private Login activity;
    private EditText jpassword;
    private GenKey key;
    private GenerateMessage pesan;
    private ProgressDialog dialog;
    private Handler handler;
    private AsyncTask start;
    private Button tombol;



//
//    @SuppressLint("StaticFieldLeak")
//    private class asyncUser extends AsyncTask<Void, Void, Void> {
//        private String Str_nip;
//        private String Str_pass;
//        private String code;
//        private boolean background;
//
//
//        private asyncUser(String str_nip, String str_pass) {
//            Str_nip = str_nip.trim();
//            Str_pass = str_pass.trim();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            background = true;
//
//
//            if (Str_nip.equals("") || Str_pass.equals(""))
//                this.cancel(true);
//            dialog = new ProgressDialog(activity);
//
//            dialog.setMessage("Sedang memproses data. Harap tunggu sejenak.");
//            dialog.setCancelable(false);
//
//            dialog.show();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//
//                Param param = new Param();
//                param.xnip = Str_nip;
//                param.xpassword = key.key2(Str_pass);
//                param.xuser_key = Utilities.imei(activity);
//                param.xuser_type = "13";
//                Gson gson = new Gson();
//                String xdata = gson.toJson(param);
//                List<NameValuePair> p = new ArrayList<NameValuePair>();
//                p.add(new BasicNameValuePair(key.key(145), key.encode(xdata)));
//
//                JsonParser jParser = new JsonParser();
//                JSONObject json = jParser.GetJSONObjectEncrypted(key.url(1), p);
////                Log.e("param login ", xdata);
////                Log.e("isi json login", json.toString());
//
//
//                code = json.getString("code");
//                Log.e("token", json.toString(3));
//
//                if (code.equals(key.key(999))) {
//                    Paramnama data = new Paramnama();
//                    JSONObject token = json.getJSONObject("data");
//
//                    data.xtoken = token.getString(key.key(5192));
//                    data.xuser_key = Utilities.imei(activity);
//                    data.xuser_type = "13";
//                    data.xnip = Str_nip;
//                    xdata = gson.toJson(data);
//                    List<NameValuePair> N = new ArrayList<NameValuePair>();
//                    N.add(new BasicNameValuePair(key.key(145), key.encode(xdata)));
//
//                    JSONObject jsonnama = jParser.GetJSONObjectEncrypted(key.url(2), N);
////                    JSONObject foto = jParser.GetJSONObjectEncrypted(key.url(92), N);
////                    Log.e("ERfoto", foto.toString(3));
//                    jsonnama = jsonnama.getJSONObject("data");
//                    Log.e("ERprofil", jsonnama.toString(3));
////                    Log.e("ERjson", data.xtoken);
//
//                    SharedPreferences sp = activity.getSharedPreferences(key.key(9145), 0x0000);
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putString(key.key(1001), Str_nip);
//                    editor.putString(key.key(1002), data.xtoken);
//                    editor.putString(key.key(1102), jsonnama.getString("nama"));
//                    editor.putString(key.key(1103), jsonnama.getString("jabatan"));
//
//                    editor.commit();
//                }
//            } catch (Exception e) {
//                background = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//            handler.removeCallbacksAndMessages(null);
//            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
//            if (background) {
//                if (code.equals(key.key(999))) {
//                    startActivity(new Intent(activity, MainActivity.class));
//                    finish();
//                    Log.d("yeyy", "berhasil");
//                } else {
//                    ab
//                            .setCancelable(false).setTitle("Informasi")
//                            .setMessage(pesan.getMessage(code))
//                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .show();
//                }
//            } else {
//                Utilities.codeerror(activity, "ER0211");
//            }
//        }
//    }

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
        pesan = new GenerateMessage();
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
                if (!isEmailValid(str_nip)) {
                    jnip.setError(getString(R.string.error_invalid_email));
                    jnip.requestFocus();
                } else {
                    mulai(str_nip, str_pass);
                }


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
//       Log.e("ER", "start");
//        start = new asyncUser(nip, pass).execute();
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
        return nip.length() == 6;
    }


}






