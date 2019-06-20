package tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas;

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
import android.widget.LinearLayout;
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
import tech.opsign.kkp.absensi.admin.Master.siswa.Tool_list_siswa.Adapter_list_siswa;
import tech.opsign.kkp.absensi.admin.Master.siswa.Tool_list_siswa.Model_list_siswa;

public class ubah_ketuakelas extends AppCompatActivity {
    private static SharedPreferences sp;
    private ubah_ketuakelas activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_list_siswa> modelList = new ArrayList<>();
    private Adapter_list_siswa adapter;
    private String str_nis, str_KDkelas, action;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_kelas_walikelas);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if (action.equals("1"))
            setTitle("Pilih Ketua Kelas");
        else
            setTitle("Hapus Siswa dari Kelas");


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        adapter = new Adapter_list_siswa(modelList);
        recyclerView = (RecyclerView) findViewById(R.id.list_wali_kelas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        proses();
        setResult(RESULT_OK);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void proses() {
        try {
            Intent intent = getIntent();
            Model_list_siswa row;
//            String aaa;
//            aaa = intent.getStringExtra("array_siswa");
//            Log.e("ER", aaa);
            str_KDkelas = intent.getStringExtra("kode_kels");
            JSONArray aray = new JSONArray(intent.getStringExtra("array_siswa"));
            if (aray.length() > 0) {
                ((LinearLayout) findViewById(R.id.nulldata)).setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                JSONObject list;
                for (int i = 0; i < aray.length(); i++) {
                    list = aray.getJSONObject(i);
                    row = new Model_list_siswa(
                            list.getString("nis"),
                            list.getString("nama_siswa")

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
            e.printStackTrace();
        }
    }

    private void showSelectedMatkul(Model_list_siswa list) {
        str_nis = list.nis;
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        String codee;
        if (action.equals("1"))

            codee = "Apakah anda yakin ingin menggantikan Ketua Kelas kepada " + list.nama;
        else
            codee = "Apakah anda yakin ingin menghapus " + list.nama+" dari kelas";
        ab
                .setCancelable(false)
                .setTitle("Informasi")
                .setMessage(codee)
                .setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        kirim();
                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void kirim() {
        Log.e("ER", "start");
        start = new kirimkan().execute();
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


    private class kirimkan extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        class Param {
            String x1d, type, key, token;
            String id_kelas, nis;
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
                param.id_kelas = str_KDkelas;
                param.nis = str_nis;
//                param.kd_kelas ="A00001";


                Gson gson = new Gson();
                Log.e("ER___", gson.toJson(param));
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                String url;
                if (action.equals("1"))
                    url = key.url(325);
                else
                    url = key.url(327);

                json = jParser.getJSONFromUrl(url, p);
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

                    if (action.equals("1"))

                        code = "Ketua Kelas sudah berhasil diganti";
                    else
                        code = "Siswa berhasil dihapus dalam kelas";

                    ab.setMessage(code).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (action.equals("1")){
                                finish();
                            }
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


}
