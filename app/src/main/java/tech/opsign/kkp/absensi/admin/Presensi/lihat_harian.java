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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import tech.opsign.kkp.absensi.admin.Presensi.tool_harian.Adapter_lap_harian;
import tech.opsign.kkp.absensi.admin.Presensi.tool_harian.Model_lap_harian;

public class lihat_harian extends AppCompatActivity {
    private static SharedPreferences sp;
    private lihat_harian activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private List<Model_lap_harian> modelList = new ArrayList<>();
    private Adapter_lap_harian adapter;
    private RecyclerView recyclerView;
    private String kode_kelas = "", tanggal = "", namakelas = "", action;//y untuk hapus n untuk lihat saja

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setContentView(R.layout.a_laporan_harian);
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
        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if (action.equals("y")) {
            setTitle("Ubah Presensi");

            ((Button) findViewById(R.id.ubahperkelas)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.ubahperkelas)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ER", "ASDWADADA");

                    Intent inten = new Intent(activity, ubah_Presensi.class);
                    inten.putExtra("nama", "Form untuk mengganti presensi semua siswa pada kelas " + namakelas);

                    inten.putExtra("kode", kode_kelas);
                    inten.putExtra("tanggal", tanggal);

                    inten.putExtra("perintah", "all");

//
                    startActivityForResult(inten, 0);
                }
            });

        } else {
            setTitle("Laporan Presensi perHari");
            ((Button) findViewById(R.id.ubahperkelas)).setVisibility(View.GONE);

        }
        adapter = new Adapter_lap_harian(modelList);
        recyclerView = (RecyclerView) findViewById(R.id.list_lap_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        kirim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refres, menu);
        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            kirim();
        }

    }

    private class kirim_kelas extends AsyncTask<Void, Void, Void> {

        private String code;
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
                kode_kelas = intent.getStringExtra("id_kelas");
                param.id_kelas = kode_kelas;
                tanggal = intent.getStringExtra("tgl");
                param.tgl = tanggal;


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(343), p);
//                Log.e("ER___", gson.toJson(param));
//                Log.e("ER___", json.toString(2));
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
                    if (action.equals("y")) {
                        Toast.makeText(activity, "Tekan siswa dibawah untuk merubah presensi atau klik ubah semua", Toast.LENGTH_LONG).show();

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
                Model_lap_harian row;
                JSONArray aray = json.getJSONArray("presensi");

                if (aray != null && aray.length() > 0) {
                    ((LinearLayout) findViewById(R.id.nulldata)).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
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
//                data = json.getJSONObject("data");
                data = json.getJSONObject("data");
                ((TextView) findViewById(R.id.thn_ajar)).setText("Tanggal " + Utilities.gettgl_lahir(json.getString("tanggal")));
                ((TextView) findViewById(R.id.periode)).setText("Tahun Ajar " + Utilities.ubahan_thn_ajrn(data.getString("thn_ajar").substring(0, 4)));
//                ((TextView) findViewById(R.id.periode)).setText("Tahun Ajar ");
                namakelas = data.getString("nama");
                ((TextView) findViewById(R.id.nama_kelas)).setText(namakelas);
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

        private void showSelectedMatkul(Model_lap_harian isi) {
            if (action.equals("y")) {
//                Utilities.showMessageBox(activity, "Riwayat presensi " + isi.nama, isi.nama);

                Intent inten = new Intent(activity, ubah_Presensi.class);
                inten.putExtra("nama", "Form untuk mengganti presensi  " + isi.nama);

                inten.putExtra("kode", isi.nis);
                inten.putExtra("tanggal", tanggal);

                inten.putExtra("perintah", "one");

//
                startActivityForResult(inten, 0);
            }

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        if (id == R.id.action_favorite)
            kirim();
        return super.onOptionsItemSelected(item);
    }


}
