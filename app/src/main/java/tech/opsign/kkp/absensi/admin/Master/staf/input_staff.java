package tech.opsign.kkp.absensi.admin.Master.staf;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;

public class input_staff extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static SharedPreferences sp;
    private input_staff activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;

    private static String str_level = "";
    private EditText nip, nama;
    private String str_nip, str_nama;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_staf_input);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setTitle("Input Siswa");
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Spinner spiner = findViewById(R.id.level_staff);
        spiner.setAdapter(null);
        ArrayList<String> jenis = new ArrayList<String>();
        jenis.add("Guru Wali Kelas");
        jenis.add("Guru Piket");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spiner_item, jenis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(adapter);
        spiner.setOnItemSelectedListener(this);

//        nisn, nama, level,nama_wali, alamat, no_ijazah, no_ujian;
        nip = (EditText) findViewById(R.id.nip_staf);
        nama = (EditText) findViewById(R.id.namna_staf);

        Button tombol = findViewById(R.id.kirimtanggal);
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closekeyboard();
                nip.setError(null);
                nama.setError(null);
                str_nip = nip.getText().toString().trim();
                str_nama = nama.getText().toString().trim();


                if (str_nip.equals("")) {
                    nip.setError("Wajib diisi");
                } else if (str_nip.length() < 6) {
                    nip.setError("Mimal 6 Digit Angka");
                } else if (nama.getText().toString().trim().equals("")) {
                    nama.setError("Wajib diisi");
                } else if (str_level.equals("")) {
                    pesan("Tanggal Lahir Wajib Diisi", activity);
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
        start = new kirim_siswa().execute();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("agamanya", parent.getItemAtPosition(position).toString());
        str_level = String.valueOf(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class kirim_siswa extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token;
            String nip, nama_staf, level;
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
                param.nip = str_nip;
                param.nama_staf = str_nama;
                param.level = str_level;

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(335), p);
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
                    ab.setMessage("Penginputan Siswa Berhasil.\nUsername \'" + str_nip + "\'\nPassword 'admin'").setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
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
                        }
                    }).show();
                }



            } else {
                Utilities.codeerror(activity, "ER0211");
            }
        }


    }


    private static void pesan(String pesannya, Context context) {
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab
                .setCancelable(false).setTitle("Informasi")
                .setMessage(pesannya)
                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
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
