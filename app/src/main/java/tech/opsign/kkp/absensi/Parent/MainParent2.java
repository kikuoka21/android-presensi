package tech.opsign.kkp.absensi.Parent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.SplashScreen;
import tech.opsign.kkp.absensi.Tanggal_Libur;

public class MainParent2 extends AppCompatActivity {
    private SharedPreferences sp;
    private MainParent2 activity;
    private boolean doubleBackToExitPressedOnce = false;
    private GenKey key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainactivity_parent);
        this.activity = this;
        sp = activity.getSharedPreferences("shared", 0x0000);
        key = new GenKey();


        ((TextView) findViewById(R.id.namasiswa)).setText("(" + sp.getString("username", "") + ") " + sp.getString("nama", ""));

        ((TextView) findViewById(R.id.tanggal)).setText(Utilities.gettanggal(sp.getString("tanggal", "")));
//        ((TextView) navHeaderView.findViewById(R.id.nip_nav)).setText(sp.getString("username", ""));
//        mulai();
        volley_call();

        findViewById(R.id.his_tanggal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {

                                startActivity(new Intent(activity, Tanggal_Libur.class));
                            }
                        }, 300
                );
            }
        });

        findViewById(R.id.riwayat_presensi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {

                                startActivity(new Intent(activity, Presensi_Siswa_parent.class));
                            }
                        }, 300
                );
            }
        });
        findViewById(R.id.profil_siswa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(activity, Profile_parent.class));
                            }
                        }, 300
                );
            }
        });
        findViewById(R.id.logout_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new android.app.AlertDialog.Builder(activity, R.style.ThemeOverlay_MaterialComponents_Dialog)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin keluar ?")
                        .setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }


    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan KEMBALI lagi untuk keluar", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);


    }

    private void volley_call() {
        key.showProgress(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(402),
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

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("kd_kelas", json.getString("kd_kelas"));
                                    editor.putString("nama_kelas", json.getString("nm_kelas"));
                                    editor.putString("tanggal", json.getString("tanggal"));


                                    ((TextView) findViewById(R.id.tanggal)).setText(Utilities.gettanggal(json.getString("tanggal")));
                                    editor.apply();
                                    JSONObject status = json.getJSONObject("status_siswa");
                                    json = json.getJSONObject("hari_ini");
                                    if (json.getBoolean("status")) {
                                        ((TextView) findViewById(R.id.keterangan)).setText("Ada Kegiatan Belajar Mengajar");
                                        findViewById(R.id.info).setVisibility(View.GONE);

                                        findViewById(R.id.keterangan_status).setVisibility(View.VISIBLE);
                                        if (status.getString("status").equals("H") || status.getString("status").equals("h")) {
                                            ((TextView) findViewById(R.id.ket_status)).setText("Sudah Melakukan Presensi hari ini");
                                        } else if (status.getString("status").equals("")) {
                                            ((TextView) findViewById(R.id.ket_status)).setText(status.getString("ket"));

                                        } else
                                            ((TextView) findViewById(R.id.ket_status)).setText(status.getString("status") + " (" + Utilities.status_kehadiran(status.getString("status")) + "), dengan keterangan " + status.getString("ket"));


                                    } else {
                                        ((TextView) findViewById(R.id.keterangan)).setText("Libur");
                                        ((TextView) findViewById(R.id.info)).setText(json.getString("ket"));
                                        findViewById(R.id.info).setVisibility(View.VISIBLE);
                                        findViewById(R.id.keterangan_status).setVisibility(View.GONE);
                                    }

//                                    if (status.getBoolean("flag")) {
//                                    ((TextView) findViewById(R.id.ket_status)).setText(status.getString("ket"));
//                                    } else {
//                                        ((TextView) findViewById(R.id.ket_status)).setText("Tidak ada");
//                                    }

                                } else {
                                    ab.setMessage(json.getString("message")).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
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
                                ab.setMessage(GenKey.pesan(json.getString("message")))
                                        .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                                Intent splash = new Intent(activity, SplashScreen.class);
                                                splash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(splash);
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

//                postMap.put("xnip", uid.getText().toString().trim());
//                postMap.put("xpassword", (pass.getText().toString().trim()));
//                postMap.put("xtype", "1");
//                postMap.put("xkey", generate.imei(activity));
//                Log.e("ER", generate.Ubah_POST(postMap, core) + "  ");

                Map<String, String> postMap2 = new HashMap<>();
                try {
                    JsonObject xdata = new JsonObject();


                    xdata.addProperty("x1d", sp.getString("username", ""));
                    xdata.addProperty("type", "mmm");
                    xdata.addProperty("key", Utilities.imei(activity));
                    xdata.addProperty("token", sp.getString("token", ""));
                    xdata.addProperty("token_firebase", sp.getString("token_firebase", ""));
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

    private void logout() {
        key.showProgress(activity, true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(404),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        key.hideProgress();
                        try {


                            JSONObject json = new JSONObject(response);
                            Log.e("ER", json.toString(3));
                            if (json.getBoolean("hasil")) {

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username", "");
                                editor.putString("token", "");
                                editor.apply();
                                startActivity(new Intent(activity, SplashScreen.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            } else {
                                androidx.appcompat.app.AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                                ab.setCancelable(false).setTitle("Informasi");
                                ab.setMessage(json.getString("message")).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
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

                Map<String, String> postMap2 = new HashMap<>();
                try {
                    JsonObject xdata = new JsonObject();


                    xdata.addProperty("x1d", sp.getString("username", ""));
                    xdata.addProperty("token", sp.getString("token", ""));
                    xdata.addProperty("wali", true);
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
