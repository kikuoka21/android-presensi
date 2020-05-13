package tech.opsign.kkp.absensi.siswa;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.siswa.tool_presensi.Adapter_presensi;
import tech.opsign.kkp.absensi.siswa.tool_presensi.Model_presensi;

public class cari_presensi extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static SharedPreferences sp;
    private cari_presensi activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_presensi> modelList = new ArrayList<>();
    private Adapter_presensi Adapter;
    private String action = "", strtahun = "", strbulan = "";
    private static String strtanggal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.s_siswa_lihat);

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

        setTitle("Rekap Presensi");

        ((TableRow) findViewById(R.id.bulan_tahun)).setVisibility(View.VISIBLE);

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


        Button button = findViewById(R.id.carikelas);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirim();
            }
        });

        Adapter = new Adapter_presensi(modelList);
        recyclerView = (RecyclerView) findViewById(R.id.list_kelas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(Adapter);


        ((TableLayout)findViewById(R.id.isian)).setVisibility(View.GONE);
    }

    private void kirim() {

        ((TableLayout)findViewById(R.id.isian)).setVisibility(View.GONE);
        modelList.clear();
        Adapter.notifyDataSetChanged();
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
            String tanggal;
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
                param.tanggal = strtanggal;


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(104), p);
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

                Model_presensi row;
                JSONObject data ;
                JSONArray aray = json.getJSONArray("kehadiran");
//                data.getString("tahun_ajar").substring(0, 4) + "/" +
//                        data.getString("tahun_ajar").substring(4)
                data = json.getJSONObject("datakelas");
                ((TextView)findViewById(R.id.presensi_header)).setText("Rekap Presensi "+sp.getString("nama", ""));
                ((TextView)findViewById(R.id.thn_ajar)).setText(data.getString("thn_ajar").substring(0, 4) + "/" +
                        data.getString("thn_ajar").substring(4));
                ((TextView)findViewById(R.id.nama_kelas)).setText(data.getString("nama"));
                ((TextView)findViewById(R.id.ketua_kelas)).setText(data.getString("ketua"));
                ((TextView)findViewById(R.id.walikelas)).setText(data.getString("wali"));

                if (aray != null && aray.length() > 0) {

                    ((RecyclerView)findViewById(R.id.list_kelas)).setVisibility(View.VISIBLE);
                    ((LinearLayout)findViewById(R.id.nulldata)).setVisibility(View.GONE);
                    for (int i = 0; i < aray.length(); i++) {
//               for(int i =0; i<1;i++){
                        data = aray.getJSONObject(i);
                        // type true akan menghilangkan row kelas
                        row = new Model_presensi(
                                Utilities.gettanggal(data.getString("tanggal")),
                                data.getString("stat"),
                                data.getString("ket")

                        );
                        modelList.add(row);
                    }

                    Adapter.notifyDataSetChanged();
                }else {

                    ((RecyclerView)findViewById(R.id.list_kelas)).setVisibility(View.GONE);
                    ((LinearLayout)findViewById(R.id.nulldata)).setVisibility(View.VISIBLE);
                }

                ((TableLayout)findViewById(R.id.isian)).setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }

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
