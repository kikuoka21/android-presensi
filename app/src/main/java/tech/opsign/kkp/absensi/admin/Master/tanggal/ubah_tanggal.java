package tech.opsign.kkp.absensi.admin.Master.tanggal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Listener.ItemClickSupport;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Master.tanggal.Tool_Input_Tanggal.Adapter_tanggal;
import tech.opsign.kkp.absensi.admin.Master.tanggal.Tool_Input_Tanggal.Model_tanggal;

public class ubah_tanggal extends AppCompatActivity {
    private static SharedPreferences sp;
    private ubah_tanggal activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;

    private RecyclerView recyclerView;
    private List<Model_tanggal> modelList = new ArrayList<>();
    private Adapter_tanggal adapter;

    private EditText ket;
    private TextView tgl;
    private String tgl_string = "";
    private String ket_string;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_input_tanggal);

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
        setTitle("Ubah Hari Libur");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        tgl = (TextView) findViewById(R.id.inpt_tgl);
        ket = (EditText) findViewById(R.id.ket);
        tgl_string = intent.getStringExtra("tgl");
        tgl.setText(Utilities.gettanggal(tgl_string));
        ket.setText(intent.getStringExtra("ket"));

        Button tombol = findViewById(R.id.kirimtanggal);

        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closekeyboard();

                ket_string = ket.getText().toString().trim();
                if (ket.getText().toString().trim().equals("")) {
                    Utilities.showMessageBox(activity, "Informasi", "Keterangan Tidak Boleh Kosong");
                } else {
                    Log.e("ER__", "KIRM BOIIIII");
                    kirim();
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

    private void kirim() {
        Log.e("ER", "start");
        start = new kirim_tgl().execute();
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
                                    kirim();
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

    private class kirim_tgl extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token, tanggal, ket;
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
                param.tanggal = tgl_string;
                param.ket = ket_string;

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(312), p);
//                Log.e("isi json login", json.toString(2));
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
                    ab.setMessage("P Tanggal Berhasil").setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
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



}
