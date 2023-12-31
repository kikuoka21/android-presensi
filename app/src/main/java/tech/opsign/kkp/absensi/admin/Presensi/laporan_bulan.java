package tech.opsign.kkp.absensi.admin.Presensi;

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
import tech.opsign.kkp.absensi.Listener.ItemClickSupport;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Presensi.tool_lap_bulan.Adapter_laporan_bulan;
import tech.opsign.kkp.absensi.admin.Presensi.tool_lap_bulan.Model_laporan_bulan;

public class laporan_bulan extends AppCompatActivity {
    private static SharedPreferences sp;
    private laporan_bulan activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private List<Model_laporan_bulan> modelList = new ArrayList<>();
    private Adapter_laporan_bulan adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setTitle("Laporan Presensi perSemester");
        setContentView(R.layout.a_laporan_semester);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
//        ((TableRow) findViewById(R.id.spin_smester)).setVisibility(View.VISIBLE);

        adapter = new Adapter_laporan_bulan(modelList);
        recyclerView = findViewById(R.id.list_lap_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        kirim();
    }


    private void kirim() {
        modelList.clear();
        adapter.notifyDataSetChanged();
        Log.e("ER", "start");
        start = new kirim_kelas().execute();
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


    private class kirim_kelas extends AsyncTask<Void, Void, Void> {

        private String code, tgl;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token, tgl, id_kelas;
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
                Intent intent = getIntent();
                param.id_kelas = intent.getStringExtra("id_kelas");
                tgl = intent.getStringExtra("tgl");
                param.tgl = tgl;


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(342), p);
                Log.e("ER___", gson.toJson(param));
                Log.e("ER___", json.toString(2));
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
                            finish();
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
                StringBuilder str_detil ;
                Model_laporan_bulan row;
                JSONArray detil, aray = json.getJSONArray("presensi");

                if (aray != null && aray.length() > 0) {
                    findViewById(R.id.nulldata).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < aray.length(); i++) {
                        data = aray.getJSONObject(i);
                        str_detil = new StringBuilder();
                        detil = data.getJSONArray("kehadiran");
                        int alpha = 0;
                        int sakit = 0;
                        int izin = 0;
                        int telat = 0;
                        for (int a = 0; a < detil.length(); a++) {
                            isidetil = detil.getJSONObject(a);
                            String ket = isidetil.getString("stat");
                            if (ket.equals("A")) {
                                alpha++;
                            } else if (ket.equals("I")) {
                                izin++;
                            } else if (ket.equals("S")) {
                                sakit++;
                            } else if (ket.equals("H"))
                                telat++;
                            str_detil.append("Tanggal : ").append(Utilities.tgl_bulan(isidetil.getString("tanggal"))).append("\nStatus Kehadiran : ").append(Utilities.status_kehadiran(isidetil.getString("stat"))).append("\nKeterangan : ").append(isidetil.getString("ket")).append("\n\n");
                        }
                        row = new Model_laporan_bulan(
                                data.getString("nis"),
                                data.getString("nama"),
                                String.valueOf(sakit),
                                String.valueOf(izin),
                                String.valueOf(alpha),
                                String.valueOf(telat),
                                str_detil.toString()
                        );
                        modelList.add(row);
                    }
                    adapter.notifyDataSetChanged();


                }
                data = json.getJSONObject("data");
                ((TextView) findViewById(R.id.thn_ajar)).setText("Bulan "+Utilities.bln_thn(tgl.substring(0, 7)));
                ((TextView) findViewById(R.id.periode)).setText("Tahun Ajar " + ubahan_thn_ajrn(data.getString("thn_ajar").substring(0, 4)));
                ((TextView) findViewById(R.id.nama_kelas)).setText(data.getString("nama_kelas"));
                ((TextView) findViewById(R.id.walikelas)).setText(data.getString("wali"));
                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        showSelectedMatkul(modelList.get(position));
                    }
                });
            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }

        private void showSelectedMatkul(Model_laporan_bulan isi) {

            Utilities.showMessageBox(activity, "Riwayat presensi " + isi.nama, isi.detil);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public static String ubahan_thn_ajrn(String a) {
        return a + "/" + (Integer.parseInt(a) + 1);
    }
}
