package tech.opsign.kkp.absensi.siswa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import tech.opsign.kkp.absensi.R;

import static android.Manifest.permission_group.CAMERA;

public class isi_absen extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private SharedPreferences sp;
    private isi_absen activity;
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

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                onPause();
                Log.e("ER", "st11111art");
                Log.e("ER", qrcode.getString("id_kelas"));
                Log.e("ER", sp.getString("kd_kelas", ""));
//                Log.e("TAG222", String.valueOf(qrcode.getString("kd_kls").equals(sp.getString("kd_kelas", ""))));
                if (qrcode.getString("id_kelas").equals(sp.getString("kd_kelas", ""))) {

                    if (qrcode.getString("tanggal").equals(sp.getString("tanggal", ""))) {
                        mulai();
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
                e.printStackTrace();
                onResume();
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



    private class Param {
        String x1d, type, key, token, kd_kls, token_absen, tanggal;
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
            onPause();

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
//                param.kd_kls = sp.getString("token", "");
                param.kd_kls = sp.getString("kd_kelas", "");
                param.token_absen = qrcode.getString("token");
                param.tanggal = qrcode.getString("tanggal");


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(102), p);
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
//                if (true) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                    ab
                            .setCancelable(false).setTitle("Informasi")
                            .setMessage("Berhasil")
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                } else {
                    generate_pesan(code);
                }

            } else {
                Utilities.codeerror(activity, "ER0211");
            }
        }

        private void proses() {
            try {

            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }
    }

    private void generate_pesan(String pesan) {
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab
                .setCancelable(false).setTitle("Informasi")
                .setMessage(pesan)
                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
}
