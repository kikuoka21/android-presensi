package tech.opsign.kkp.absensi.siswa;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

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
import com.google.zxing.Result;

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
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;

public class scan_qr_code extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private SharedPreferences sp;
    private scan_qr_code activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private JSONObject qrcode;


    private ZXingScannerView mScannerView;
    private Result rawResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.s_generate_qr);

        this.activity = this;


        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setTitle("QR Code Kelas");
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
//
//        mulai();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mScannerView = new ZXingScannerView(activity);
        setContentView(mScannerView);

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Sedang memproses data. Harap tunggu sejenak.");
        dialog.setCancelable(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e("TAG22", String.valueOf(Utilities.checkjson(rawResult.getText())));

        if (Utilities.checkjson(rawResult.getText())) {
//            onPause();
            Log.e("TAG", rawResult.getText()); // Prints scan results

            try {
                qrcode = new JSONObject(rawResult.getText());

                Log.e("ER", "st11111art");
                Log.e("ER", qrcode.getString("id_kelas"));
                Log.e("ER", sp.getString("kd_kelas", ""));
//                Log.e("TAG222", String.valueOf(qrcode.getString("kd_kls").equals(sp.getString("kd_kelas", ""))));
                if (qrcode.getString("id_kelas").equals(sp.getString("kd_kelas", ""))) {

                    if (qrcode.getString("tanggal").equals(sp.getString("tanggal", ""))) {
                        onPause();
                        volley_update();
                    } else {

                        generate_pesan("QR code Sudah Kedaluwarsa.");
                    }
                } else {
//                onPause();
                    Log.e("ER", "st11111asdawdaart");

                    generate_pesan("QR Code Tersebut Bukan Untuk Kelas Anda.");
                }
                Log.e("ER", "stardawdw1t");
//                mulai();
            } catch (Exception e) {
//                e.printStackTrace();
                generate_pesan("QR-Code salah");
            }


//            onResume();
        } else
            Log.e("TAGbnawah", rawResult.getText()); // Prints scan results
//        mScannerView.resumeCameraPreview(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }





    private class Param {
        String x1d, type, key, token, kd_kls, token_absen, tanggal,nama;
    }


    private void generate_pesan(String pesan) {
        onPause();
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab
                .setCancelable(false).setTitle("Informasi")
                .setMessage(pesan)
                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                        dialog.dismiss();

//                                    onResume();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onResume();
            }
        })
                .show();
    }


    private void volley_update() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, key.url(102),
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
                                ab.setMessage("Anda Berhasil Presensi").setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
                                        onResume();
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

                    Param param = new Param();
                    param.x1d = sp.getString("username", "");
                    param.type = "mmm";
                    param.key = Utilities.imei(activity);
                    param.token = sp.getString("token", "");
//                param.kd_kls = sp.getString("token", "");
                    param.kd_kls = sp.getString("kd_kelas", "");
                    param.token_absen = qrcode.getString("token");
                    param.tanggal = qrcode.getString("tanggal");
                    param.nama = sp.getString("nama", "");

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

}
