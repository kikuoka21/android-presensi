package tech.opsign.kkp.absensi.siswa.pengurus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Presensi.tool_harian.Adapter_lap_harian;
import tech.opsign.kkp.absensi.admin.Presensi.tool_harian.Model_lap_harian;

public class lihatpresensi_tanggal extends AppCompatActivity{
    private static SharedPreferences sp;
    private lihatpresensi_tanggal activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private List<Model_lap_harian> modelList = new ArrayList<>();
    private Adapter_lap_harian adapter;
    private static String strtanggal;
    private static TextView tgl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.s_lihat_presensi);

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("LIhat presensi");
        ((TableRow) findViewById(R.id.tanggal)).setVisibility(View.VISIBLE);
        LinearLayout date_pick = (LinearLayout) findViewById(R.id.pilih_tgl);
        date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogfragment = new tanggalmulai();
                dialogfragment.show(getFragmentManager(), "Tanggal Mulai");
            }
        });
        tgl = (TextView) findViewById(R.id.inpt_tgl);
        strtanggal = sp.getString("tanggal", "");
        tgl.setText(Utilities.gettgl_lahir(sp.getString("tanggal", "")));
        ((LinearLayout)findViewById(R.id.isiannya)).setVisibility(View.GONE);

//
//
        Log.e("ER", sp.getString("kd_kelas", ""));
        adapter = new Adapter_lap_harian(modelList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_lap_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        Button button = findViewById(R.id.carikelas);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirim();
            }
        });


    }

    private void kirim() {
        ((LinearLayout)findViewById(R.id.isiannya)).setVisibility(View.GONE);
        modelList.clear();
        adapter.notifyDataSetChanged();
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
            String tgl, id_kelas;
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
                param.tgl = strtanggal;
                param.id_kelas = sp.getString("kd_kelas", "");


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(105), p);
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
                ab
                        .setCancelable(false).setTitle("Informasi");
                if (code.equals("OK4")) {
                    proses();
                    ((LinearLayout)findViewById(R.id.isiannya)).setVisibility(View.VISIBLE);

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

                JSONObject data, isidetil;
                Model_lap_harian row;
                JSONArray aray = json.getJSONArray("presensi");

                if (aray != null && aray.length() > 0) {
                    for (int i = 0; i < aray.length(); i++) {
                        data = aray.getJSONObject(i);
                        isidetil = data.getJSONObject("kehadiran");
                        row = new Model_lap_harian(
                                data.getString("nis"),
                                data.getString("nama"),
                                Utilities.status_kehadiran(isidetil.getString("stat")),
                                isidetil.getString("ket")
                        );
                        modelList.add(row);

                    }

                    adapter.notifyDataSetChanged();


                }

                data = json.getJSONObject("datakelas");
                ((TextView) findViewById(R.id.thn_ajar)).setText("Tanggal " + Utilities.gettgl_lahir(strtanggal));
                ((TextView) findViewById(R.id.periode)).setText("Tahun Ajar " + Utilities.ubahan_thn_ajrn(data.getString("thn_ajar").substring(0, 4)));
//                ((TextView) findViewById(R.id.periode)).setText("Tahun Ajar ");
                ((TextView) findViewById(R.id.nama_kelas)).setText(data.getString("nama"));
                ((TextView) findViewById(R.id.ketua_kels)).setText(data.getString("ketua"));
                ((TextView) findViewById(R.id.walikelas)).setText(data.getString("wali"));
            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
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
                    strtanggal = strin;
                    tgl.setText(Utilities.gettgl_lahir(strin));
                } else {
                    strtanggal = sp.getString("tanggal", "");
                    tgl.setText(Utilities.gettgl_lahir(sp.getString("tanggal", "")));
                    Utilities.showMessageBox(getActivity(), "Informasi", "Pilihan Tanggal Tidak Boleh Setelah " + Utilities.gettgl_lahir(sp.getString("tanggal", "")));
//                    tgl.setText("Pilih Tanggal");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    private static String ubahan(int a) {
        String x = String.valueOf(a);
        if (x.length() == 1) {
            return "0" + x;
        } else {
            return x;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


}
