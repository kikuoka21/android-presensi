package tech.opsign.kkp.absensi.admin.Presensi;

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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
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
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_kelas.Adapter_kelas_list;
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_kelas.Model_kelas_list;
import tech.opsign.kkp.absensi.admin.Master.kelas.cari_kelas;

public class Carikelas_tanggal extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static SharedPreferences sp;
    private Carikelas_tanggal activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_kelas_list> modelList = new ArrayList<>();
    private Adapter_kelas_list adapter;
    private String action = "", strtahun = "", strbulan = "";
    private static String strtanggal;
    private static TextView tgl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_kelas_laporan);

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
        Intent intent = getIntent();
        action = intent.getStringExtra("next_action");
        if (action.equals("111")) {
            setTitle("Cari Kelas - Laporan Hari");
            ((TableRow) findViewById(R.id.bulan_tahun)).setVisibility(View.GONE);
            ((TableRow) findViewById(R.id.tanggal)).setVisibility(View.VISIBLE);
            LinearLayout date_pick = (LinearLayout) findViewById(R.id.pilih_tgl);
            date_pick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialogfragment = new tanggalmulai();
                    dialogfragment.show(getFragmentManager(), "Tanggal Mulai");
                }
            });
            tgl = (TextView)findViewById(R.id.inpt_tgl);
            strtanggal = sp.getString("tanggal", "");
            tgl.setText(Utilities.gettgl_lahir(sp.getString("tanggal", "")));
        } else {
            setTitle("Cari Kelas - Laporan Bulan");

            ((TableRow) findViewById(R.id.bulan_tahun)).setVisibility(View.VISIBLE);
            ((TableRow) findViewById(R.id.tanggal)).setVisibility(View.GONE);

            Spinner spiner = findViewById(R.id.tahn);
            spiner.setAdapter(null);
            ArrayList<String> jenis = new ArrayList<String>();
            for (int i = Integer.parseInt(sp.getString("tanggal", "").substring(0, 4)); i > 2013; i--) {

                jenis.add(String.valueOf(i));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spiner_item, jenis);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spiner.setAdapter(adapter);
            spiner.setOnItemSelectedListener(this);

            Spinner spinerthn = findViewById(R.id.bulan);
            spinerthn.setAdapter(null);
            ArrayList<String> mont = new ArrayList<String>();
            mont.add("Januari");
            mont.add("Februari");
            mont.add("Maret");
            mont.add("April");
            mont.add("Mei");
            mont.add("Juni");
            mont.add("Juli");
            mont.add("Agustus");
            mont.add("September");
            mont.add("Oktober");
            mont.add("November");
            mont.add("Desember");
            ArrayAdapter<String> adapterbln = new ArrayAdapter<String>(this.activity, R.layout.spiner_item, mont);
            adapterbln.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinerthn.setAdapter(adapterbln);
            spinerthn.setOnItemSelectedListener(this);
            spinerthn.setSelection(Integer.parseInt(sp.getString("tanggal", "").substring(5, 7)) - 1);
        }

        adapter = new Adapter_kelas_list(modelList);
        recyclerView = (RecyclerView) findViewById(R.id.list_kelas);
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
            String tgl;
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


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(341), p);
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

                Model_kelas_list row;
                JSONArray aray = json.getJSONArray("data");
                if (aray != null && aray.length() > 0) {
                    ((LinearLayout) findViewById(R.id.nulldata)).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < aray.length(); i++) {
                        json = aray.getJSONObject(i);
                        // type true akan menghilangkan row kelas


                        row = new Model_kelas_list(
                                json.getString("id"),
                                json.getString("nama_kelas"),
                                json.getString("wali"),
                                json.getString("ketua")

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
                    ((LinearLayout) findViewById(R.id.nulldata)).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }

        private void showSelectedMatkul(Model_kelas_list kelas) {
//            Toast.makeText(activity, kelas.kd_kelas, Toast.LENGTH_SHORT).show();
            Intent pindah;
            if (action.equals("111")) {
                pindah = new Intent(activity, lihat_harian.class);
                pindah.putExtra("action", "111");

            } else {
                pindah = new Intent(activity, laporan_bulan.class);
            }
//            Intent pindah = new Intent(activity, laporan_semester.class);
//            pindah.putExtra("smes", smester);
            pindah.putExtra("id_kelas", kelas.kd_kelas);
            pindah.putExtra("tgl", strtanggal);
            startActivity(pindah);
            finish();

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
//                e.printStackTrace();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent == findViewById(R.id.tahn)) {
            strtahun = parent.getItemAtPosition(position).toString();
        }
        if (parent == findViewById(R.id.bulan)) {
            strbulan = String.valueOf(position + 1);
            if (strbulan.length() == 1) {
                strbulan = "0" + strbulan;
            }
        }
        strtanggal = strtahun + "-" + strbulan;
        Log.e("ER", strtanggal);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
