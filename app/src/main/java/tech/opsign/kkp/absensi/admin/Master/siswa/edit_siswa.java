package tech.opsign.kkp.absensi.admin.Master.siswa;

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
import android.widget.TextView;

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

public class edit_siswa extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static SharedPreferences sp;
    private edit_siswa activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private Spinner spiner;

    @SuppressLint("StaticFieldLeak")
    private static TextView tgl;
    private static String str_tgl_lahir = "";
    private EditText nis, nisn, nama, tmp_lahir, nama_wali, alamat, no_ijazah, no_ujian;
    private String str_nis, str_nisn, str_nama, str_tmp_lahir, str_agama, str_nama_wali, str_alamat, str_no_ijazah, str_no_ujian;

    String[] pilihan_agama = {"Islam", "Kristen", "Katolik", "Hindu", "Buddha", "Kong Hu Cu"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_siswa_input);

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
        spiner = findViewById(R.id.agama);
        spiner.setAdapter(null);
        ArrayList<String> jenis = new ArrayList<String>();
        int i;
        for (i = 0; pilihan_agama.length > i; i++) {
            jenis.add(pilihan_agama[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spiner_item, jenis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(adapter);
        spiner.setOnItemSelectedListener(this);

        tgl = (TextView) findViewById(R.id.inpt_tgl);
//        nisn, nama, tmp_lahir,nama_wali, alamat, no_ijazah, no_ujian;
        nis = (EditText) findViewById(R.id.nis);
        nisn = (EditText) findViewById(R.id.nisn);
        nama = (EditText) findViewById(R.id.namna);
        tmp_lahir = (EditText) findViewById(R.id.tmp_lahir);
        nama_wali = (EditText) findViewById(R.id.orang_tua);
        alamat = (EditText) findViewById(R.id.alamat);
        no_ijazah = (EditText) findViewById(R.id.no_ijazah);
        no_ujian = (EditText) findViewById(R.id.no_ujian);
        LinearLayout date_pick = (LinearLayout) findViewById(R.id.pilih_tgl);
        date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closekeyboard();
                DialogFragment dialogfragment = new tanggalmulai();
                dialogfragment.show(getFragmentManager(), "Tanggal Mulai");
            }
        });

        Button tombol = findViewById(R.id.kirimtanggal);
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closekeyboard();
                nis.setError(null);
                nama.setError(null);
                tmp_lahir.setError(null);
                nama_wali.setError(null);
                alamat.setError(null);
                str_nis = nis.getText().toString().trim();
                str_nisn = nisn.getText().toString().trim();
                str_nama = nama.getText().toString().trim();
                str_tmp_lahir = tmp_lahir.getText().toString().trim();
                str_nama_wali = nama_wali.getText().toString().trim();
                str_alamat = alamat.getText().toString().trim();
                str_no_ijazah = no_ijazah.getText().toString().trim();
                str_no_ujian = no_ujian.getText().toString().trim();


                if (nama.getText().toString().trim().equals("")) {
                    nama.setError("Wajib diisi");
                } else if (str_tmp_lahir.equals("")) {
                    tmp_lahir.setError("Wajib diisi");
                } else if (str_tgl_lahir.equals("")) {
                    pesan("Tanggal Lahir Wajib Diisi", activity);
                } else if (str_alamat.equals("")) {
                    alamat.setError("Wajib diisi");
                } else {
                    Log.e("ER__", "KIRM BOIIIII");
                    kirim();
                }
            }
        });

        nis.setEnabled(false);
        ambil();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void ambil() {
        Log.e("ER", "start");
        start = new panggilan_awal().execute();
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
                                    ambil();
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
        str_agama = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class panggilan_awal extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token;
            String nis;
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

                Intent intent = getIntent();
                Param param = new Param();
                param.x1d = sp.getString("username", "");
                param.type = "mmm";
                param.key = Utilities.imei(activity);
                param.token = sp.getString("token", "");
                param.nis = intent.getStringExtra("nis_target");


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(306), p);
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
                    try {
                        json = json.getJSONObject("data");
                        nis.setText(json.getString("nis"));
                        nisn.setText(json.getString("nisn"));
                        nama.setText(json.getString("nama_siswa"));
                        tmp_lahir.setText(json.getString("tmp_lahir"));

                        int i;
                        str_agama = json.getString("agama");
                        for (i = 0; pilihan_agama.length > i; i++) {
                            if (pilihan_agama[i].equals(str_agama)) {
                                spiner.setSelection(i);
                                break;
                            } else {
                                spiner.setSelection(0);
                            }
                        }
                        nama_wali.setText(json.getString("orang_tua"));
                        alamat.setText(json.getString("alamat"));
                        no_ijazah.setText(json.getString("no_ijazah"));
                        no_ujian.setText(json.getString("no_ujiansmp"));

                        str_tgl_lahir = json.getString("tgl_lahir");
                        tgl.setText(Utilities.gettgl_lahir(str_tgl_lahir));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private class kirim_siswa extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token;
            String nis, nisn, nama_siswa, tgl_lhr, alamat, tmpt_lhr, agama,
                    orangtua, no_ijazah, no_ujiansmp;
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
                param.nis = str_nis;
                param.nisn = str_nisn;
                param.nama_siswa = str_nama;
                param.tmpt_lhr = str_tmp_lahir;
                param.alamat = str_alamat;
                param.agama = str_agama;
                param.orangtua = str_nama_wali;
                param.no_ijazah = str_no_ijazah;
                param.no_ujiansmp = str_no_ujian;
                param.tgl_lhr = str_tgl_lahir;

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(305), p);
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
                ab.setCancelable(false).setTitle("Informasi");
                if (code.equals("OK4")) {
                    ab.setMessage("Data sudah berhasil diubah").setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
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


    public static class tanggalmulai extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat
                    date = new SimpleDateFormat("yyyy-MM-dd");
            String strin = String.valueOf(year) + "-" + ubahan(month + 1) + "-" + ubahan(day);

            try {
                Date tanggalpilihan, tanggalskrng;
                tanggalskrng = date.parse(sp.getString("tanggal", ""));
                tanggalpilihan = date.parse(strin);
                if ((tanggalpilihan.before(tanggalskrng) || tanggalpilihan.equals(tanggalskrng))) {
                    str_tgl_lahir = strin;
                    tgl.setText(Utilities.gettgl_lahir(strin));
                } else {
                    str_tgl_lahir = "";
                    pesan("Pilihan Tanggal Tidak Boleh Sesudah " + Utilities.gettgl_lahir(sp.getString("tanggal", "")), getActivity());
                    tgl.setText("Pilih Tanggal");

                }
            } catch (Exception e) {
                e.printStackTrace();
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

    public static String ubahan(int a) {
        String x = String.valueOf(a);
        if (x.length() == 1) {
            return "0" + x;
        } else {
            return x;
        }
    }

}
