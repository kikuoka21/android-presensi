package tech.opsign.kkp.absensi.admin.Master.kelas;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_siswa.Adapter_siswakelas;
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_siswa.Model_siswakelas;
import tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas.lihat_walikelas;
import tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas.nama_kelas;
import tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas.ubah_ketuakelas;

public class ubah_kelas extends AppCompatActivity {
    private static SharedPreferences sp;
    private ubah_kelas activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_siswakelas> modelList = new ArrayList<>();
    private Adapter_siswakelas adapter;
    private JSONArray list_siswa;
    private String namakelas, kode_kelas, tahun_ajar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_kelas_ubah);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setTitle("Ubah Data Kelas");
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        adapter = new Adapter_siswakelas(modelList);
        recyclerView = (RecyclerView) findViewById(R.id.list_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        awalan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            awalan();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void awalan() {
        modelList.clear();
        adapter.notifyDataSetChanged();
        Log.e("ER", "start");
        start = new awalan().execute();
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
                                    awalan();
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


    private class awalan extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token;
            String kd_kelas;
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
                list_siswa = new JSONArray("[]");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                Intent intent = getIntent();
                Param param = new Param();
                param.x1d = sp.getString("username", "");
                param.type = "mmm";
                param.key = Utilities.imei(activity);
                param.token = sp.getString("token", "");
                kode_kelas = intent.getStringExtra("kd_kelas");
                kode_kelas = "A00002";
                param.kd_kelas = kode_kelas;


                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(322), p);
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
                        }
                    }).show();
                }


            } else {
                Utilities.codeerror(activity, "ER0211");
            }
        }

        private void proses() {
            try {

                Model_siswakelas row;
                list_siswa = json.getJSONArray("list");
                if (list_siswa != null && list_siswa.length() > 0) {
                    ((LinearLayout) findViewById(R.id.nulldata)).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONObject list;
                    for (int i = 0; i < list_siswa.length(); i++) {
                        list = list_siswa.getJSONObject(i);
                        row = new Model_siswakelas(
                                list.getString("nis"),
                                list.getString("nama_siswa"),
                                list.getString("level")

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

                json = json.getJSONObject("data");
                namakelas = json.getString("nama");
                ((TextView) findViewById(R.id.nama_kelas)).setText(namakelas);
                ((TextView) findViewById(R.id.blakangthn)).setText(json.getString("tahun") + "/" + ubahan_thn_ajrn(json.getString("tahun")));
                String str_wali = "-", str_ketua = "-";
                if (!json.getString("nip").equals("-")) {
                    str_wali = "(" + json.getString("nip") + ") " + json.getString("nama_staf");
                }
                if (!json.getString("nis").equals("-")) {
                    str_ketua = "(" + json.getString("nis") + ") " + json.getString("nama_siswa");
                }
                tahun_ajar = json.getString("tahun") + ubahan_thn_ajrn(json.getString("tahun"));
                ((TextView) findViewById(R.id.walikelas)).setText(str_wali);
                ((TextView) findViewById(R.id.ketua_kelas)).setText(str_ketua);


                ((LinearLayout) findViewById(R.id.ubahnama)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, nama_kelas.class);
                        intent.putExtra("nama_kelas", namakelas);
                        intent.putExtra("kode_kels", kode_kelas);
                        startActivityForResult(intent, 0);
                    }
                });
                ((LinearLayout) findViewById(R.id.ubahwali)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(activity, lihat_walikelas.class);
                        intent.putExtra("tahun", tahun_ajar);
                        intent.putExtra("kode_kels", kode_kelas);
                        startActivityForResult(intent, 0);


                    }
                });
                ((LinearLayout) findViewById(R.id.ubah_ketua)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.e("ER______", String.valueOf(list_siswa));
//                        String aaaa = .toString();
                        Intent inten = new Intent(activity, ubah_ketuakelas.class);
                        inten.putExtra("array_siswa", String.valueOf(list_siswa));
                        inten.putExtra("kode_kels", kode_kelas);
                        startActivityForResult(inten, 0);
                    }
                });
            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }

        private void showSelectedMatkul(Model_siswakelas hadir) {
            Toast.makeText(activity, hadir.nama, Toast.LENGTH_SHORT).show();
//            Intent myIntent = new Intent(activity, edit_siswa.class);
//            myIntent.putExtra("nis_target", hadir.nis);
//            startActivity(myIntent);
        }
    }


    public static String ubahan_thn_ajrn(String a) {
        return String.valueOf(Integer.parseInt(a) + 1);
    }

}
