package tech.opsign.kkp.absensi.Parent;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.Parent.tool_presensi_parent.Model_presensi_parent;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.SplashScreen;

public class GantiPass_parent extends AppCompatActivity {

    private GenKey key;private GantiPass_parent activity;
    private SharedPreferences sp;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private EditText pas_lama, pas_baru_1, pas_baru_2;
    private Button tombol;
    private String pas1_string, pas2_string, paslama_string;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Ganti Password");
        setContentView(R.layout.ganti_pass);
        key = new GenKey();
        this.activity = this;
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        pas_lama = findViewById(R.id.password_lama);
        pas_baru_1 = findViewById(R.id.password_baru1);
        pas_baru_2 = findViewById(R.id.password_baru2);

        pas_lama.addTextChangedListener(logintextwarcher);
        pas_baru_1.addTextChangedListener(logintextwarcher);
        pas_baru_2.addTextChangedListener(logintextwarcher);

        tombol = findViewById(R.id.tombol_ubah);
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pas_baru_2.setError(null);
                if (pas1_string.equals(pas2_string)) {
//                    mulai();
                    volley_call();
                    Log.e("ER", "mulai");
                } else {
                    pas_baru_2.setError("Password Baru Tidak Sama");
                }


            }
        });

    }

    private void volley_call() {
        key.showProgress(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(407),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        key.hideProgress();
                        try {
                            androidx.appcompat.app.AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                            ab.setCancelable(false).setTitle("Informasi");

                            JSONObject json = new JSONObject(response);
                            Log.e("ER", json.toString(3));
                            if (json.getBoolean("token")) {
                                if (json.getBoolean("hasil")) {
                                    ab
                                            .setMessage("Password sudah Berhasil Ganti")
                                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            })
                                            .show();

                                } else {
                                    ab.setMessage(("message")).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                                }
                            } else {
                                SharedPreferences.Editor editorr = sp.edit();
                                editorr.putString("username", "");
                                editorr.putString("token", "");
                                editorr.apply();
                                ab.setMessage(json.getString("message"))
                                        .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                                Intent login = new Intent(activity, SplashScreen.class);
                                                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(login);
                                                activity.finish();
                                            }
                                        }).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                key.hideProgress();

                try {
                    String message;
                    if (!(error instanceof NetworkError | error instanceof TimeoutError)) {

                        NetworkResponse networkResponse = error.networkResponse;
                        message = "Gagal terhubung dengan server, siahkan coba beberapa menit lagi\nError Code: " + networkResponse.statusCode;

                    } else {
                        Log.e("ER", (error instanceof NetworkError) + "" + error.getMessage());
                        message = "Gagal terhubung dengan server, siahkan coba beberapa menit lagi";
                    }


                    new androidx.appcompat.app.AlertDialog.Builder(activity)
                            .setTitle("Informasi")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("Coba Lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    volley_call();
                                }
                            }).show();
                } catch (Exception se) {
                    se.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                postMap.put("xnip", uid.getText().toString().trim());
//                postMap.put("xpassword", (pass.getText().toString().trim()));
//                postMap.put("xtype", "1");
//                postMap.put("xkey", generate.imei(activity));
//                Log.e("ER", generate.Ubah_POST(postMap, core) + "  ");

                Map<String, String> postMap2 = new HashMap<>();
                try {
                    JsonObject xdata = new JsonObject();


                    xdata.addProperty("x1d", sp.getString("username", ""));
                    xdata.addProperty("token", sp.getString("token", ""));
                    xdata.addProperty("xp4s5", key.gen_pass(pas2_string));
                    xdata.addProperty("xp4s5_lama", key.gen_pass(paslama_string));


                    Log.e("er", xdata.toString());
                    postMap2.put("parsing", xdata.toString());
                } catch (Exception e) {
                    postMap2 = null;
                }
                return postMap2;
            }
        };

        //make the request to your server as indicated in your request URL
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Utilities.rto(),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(activity).add(stringRequest);
    }
    private TextWatcher logintextwarcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            pas1_string = pas_baru_1.getText().toString().trim();
            pas2_string = pas_baru_2.getText().toString().trim();
            paslama_string = pas_lama.getText().toString().trim();
            boolean b = !pas1_string.isEmpty() && !pas2_string.isEmpty() && !paslama_string.isEmpty();
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

    private class Param {
        String x1d, type, key, token, xp4s5, xp4s5_lama, parent;
    }

    private class callAPI extends AsyncTask<Void, Void, Void> {

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
                param.x1d = sp.getString("username", "");
                param.type = "mmm";
                param.key = Utilities.imei(activity);
                param.token = sp.getString("token", "");
                param.xp4s5 = key.gen_pass(pas2_string);
                param.xp4s5_lama = key.gen_pass(paslama_string);
                param.parent = "=";
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
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", "");
                    editor.putString("token", "");
                    editor.apply();

                    ab
                            .setMessage("Password sudah Berhasil Ganti, Silahkan Login Kembali")
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent login = new Intent(activity, SplashScreen.class);
                                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(login);
                                    activity.finish();
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

                            Intent login = new Intent(activity, Login.class);
                            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                            activity.finish();
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
