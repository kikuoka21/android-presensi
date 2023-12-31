package tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

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
import tech.opsign.kkp.absensi.Listener.ItemClickSupport;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas.Tool_list_walikelas.Adapter_walikelas;
import tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas.Tool_list_walikelas.Model_walikelas;

public class lihat_walikelas extends AppCompatActivity {
    private static SharedPreferences sp;
    private lihat_walikelas activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_walikelas> modelList = new ArrayList<>();
    private Adapter_walikelas adapter;
    String str_nip, str_KDkelas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_kelas_walikelas);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setTitle("Ubah Wali Kelas");
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        adapter = new Adapter_walikelas(modelList);
        recyclerView = findViewById(R.id.list_wali_kelas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Sedang memproses data. Harap tunggu sejenak.");
        dialog.setCancelable(false);

        get_walikelas();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void get_walikelas() {
        Log.e("ER", "start");
        start = new call_list_walikelas().execute();
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
                                    get_walikelas();
                                }
                            }).setNegativeButton("kembali", new DialogInterface.OnClickListener() {
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


    private class call_list_walikelas extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token;
            String thn_ajar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            background = true;
            dialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                Intent intent = getIntent();
                Param param = new Param();
                param.x1d = sp.getString("username", "");
                param.type = "mmm";
                param.key = Utilities.imei(activity);
                param.token = sp.getString("token", "");
                param.thn_ajar = intent.getStringExtra("tahun");
                str_KDkelas = intent.getStringExtra("kode_kels");

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(323), p);
                Log.e("ER___", json.toString(2));
                Log.e("ER___", gson.toJson(param));
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
                ab
                        .setCancelable(false).setTitle("Informasi");
                if (code.equals("OK4")) {
                    proses();
                } else if (code.equals("TOKEN2") || code.equals("TOKEN1")) {
                    SharedPreferences.Editor editorr = sp.edit();
                    editorr.putString("username", "");
                    editorr.putString("token", "");
                    editorr.commit();
                    ab.setMessage(GenKey.pesan(code))
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                    Intent login = new Intent(activity, Login.class);
                                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(login);
                                    finish();
                                }
                            }).show();
                } else {
                    ab.setMessage(code).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }


            } else {
                Utilities.codeerror(activity, "ER0211");
            }
        }

        private void proses() {
            try {

                Model_walikelas row;
                JSONArray aray = json.getJSONArray("data");
                if (aray != null && aray.length() > 0) {
                    findViewById(R.id.nulldata).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONObject list;
                    for (int i = 0; i < aray.length(); i++) {
                        list = aray.getJSONObject(i);
                        row = new Model_walikelas(
                                list.getString("nip"),
                                list.getString("nama")

                        );
                        modelList.add(row);
                    }
                    adapter.notifyDataSetChanged();
                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            showSelectedMatkul(modelList.get(position));
                        }
                    });
                } else {
                    findViewById(R.id.nulldata).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }

        private void showSelectedMatkul(Model_walikelas list) {
            str_nip = list.nip;
            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
            ab
                    .setCancelable(false)
                    .setTitle("Informasi")
                    .setMessage("Apakah anda yakin ingin menggantikan wali kelas kepada " + list.nama)
                    .setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
//                            kirim();
                            volley_update();
                        }
                    }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    private void volley_update() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, key.url(324),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        try {
                            androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(activity);
                            ab.setCancelable(false).setTitle("Informasi");

                            JSONObject json = new JSONObject(response);
                            String code = json.getString("code");
                            if (code.equals("OK4")) {
                                ab.setMessage("Wali kelas sudah digantikan").setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }).show();
                            } else if (code.equals("TOKEN2") || code.equals("TOKEN1")) {
                                SharedPreferences.Editor editorr = sp.edit();
                                editorr.putString("username", "");
                                editorr.putString("token", "");
                                editorr.commit();
                                ab.setMessage(GenKey.pesan(code))
                                        .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                                Intent login = new Intent(activity, Login.class);
                                                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(login);
                                                finish();
                                            }
                                        }).show();
                            } else {
                                ab.setMessage(code).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

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
                            .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
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


                Map<String, String> postMap2 = new HashMap<>();
                try {

                    Param2 param = new Param2();
                    param.x1d = sp.getString("username", "");
                    param.type = "mmm";
                    param.key = Utilities.imei(activity);
                    param.token = sp.getString("token", "");
                    param.id_kelas = str_KDkelas;
                    param.nip = str_nip;


                    Gson gson = new Gson();
                    postMap2.put("parsing", gson.toJson(param));
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


    private class Param2 {
        String x1d, type, key, token;
        String id_kelas, nip;
    }


}
