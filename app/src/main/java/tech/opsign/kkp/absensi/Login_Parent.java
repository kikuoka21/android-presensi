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
import android.widget.TextView;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        jnip.setText("181142");

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
                volley_call();


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




    private void volley_call() {
        key.showProgress(activity, true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(401),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        key.hideProgress();
                        try {

                            JSONObject json = new JSONObject(response);
                            Log.e("ER", json.toString(3));
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
                                        .setTitle("Informasi")
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
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
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

                    xdata.addProperty("x1d", str_username);
                    xdata.addProperty("type", "mmm");
                    xdata.addProperty("key", Utilities.imei(activity));
                    xdata.addProperty("xp455", key.gen_pass(str_pass));
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

}






