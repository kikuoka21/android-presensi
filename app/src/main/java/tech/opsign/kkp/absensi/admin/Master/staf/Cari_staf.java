package tech.opsign.kkp.absensi.admin.Master.staf;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import tech.opsign.kkp.absensi.Listener.ItemClickSupport;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Master.siswa.Tool_list_siswa.Adapter_list_siswa;
import tech.opsign.kkp.absensi.admin.Master.siswa.edit_siswa;
import tech.opsign.kkp.absensi.admin.Master.staf.Tool_list_staf.Adapter_list_staf;
import tech.opsign.kkp.absensi.admin.Master.staf.Tool_list_staf.Model_list_staf;

public class Cari_staf extends AppCompatActivity {
    private static SharedPreferences sp;
    private Cari_staf activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;

    private RecyclerView recyclerView;
    private List<Model_list_staf> modelList = new ArrayList<>();
    private Adapter_list_staf adapter;

    private EditText nama;
    private String str_nama;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_staf_cari);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setTitle("Cari Siswa");
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        nama = (EditText) findViewById(R.id.nama_nip) ;


        adapter = new Adapter_list_staf(modelList);
        recyclerView = (RecyclerView) findViewById(R.id.list_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        Button tombol = findViewById(R.id.carisiswa);
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closekeyboard();

                str_nama = nama.getText().toString().trim();
                if (str_nama.equals("")) {
                    Utilities.showMessageBox(activity, "Warning", "Kolom cari tidak boleh kosng");
                } else if (str_nama.length() <=2) {
                    Utilities.showMessageBox(activity, "Warning", "Minimal 3 huruf");
                } else  {
//                    Log.e("ER__", "KIRM BOIIIII");
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
        modelList.clear();
        adapter.notifyDataSetChanged();
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
            String x1d, type, key, token, nama;
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
                param.nama=str_nama;

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(336), p);
                Log.e("isi json login", json.toString(2));
//                Log.e("isi json login", gson.toJson(param));
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
                Model_list_staf row;
                JSONArray aray = json.getJSONArray("data");
                if (aray != null && aray.length() > 0) {
                    ((LinearLayout) findViewById(R.id.nulldata)).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < aray.length(); i++) {
                        json = aray.getJSONObject(i);
                        // type true akan menghilangkan row kelas
                        row = new Model_list_staf(
                                json.getString("nip"),
                                json.getString("nama_staf"),
                                json.getString("level")

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

        private void showSelectedMatkul(Model_list_staf hadir) {
//            Toast.makeText(activity, hadir.nip, Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(activity, edit_siswa.class);
            myIntent.putExtra("nis_target", hadir.nip);
            startActivity(myIntent);
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
//
//    private void hapus() {
//        Log.e("ER", "start");
//        start = new call_hapus().execute();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                    start.cancel(true);
//                    new AlertDialog.Builder(activity)
//                            .setTitle("Informasi")
//                            .setMessage("Telah Terjadi Kesalahan Pada Koneksi Anda.")
//                            .setCancelable(false)
//                            .setPositiveButton("Coba Lagi", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    hapus();
//                                }
//                            }).setNegativeButton("kembali", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            finish();
//                        }
//                    }).show();
//                }
//            }
//        }, Utilities.rto());
//    }
//
//    private class call_hapus extends AsyncTask<Void, Void, Void> {
//
//        private String code;
//        private JSONObject json;
//        private boolean background;
//
//        class Param {
//            String x1d, type, key, token, id_kelas, p4ss;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            background = true;
//            dialog = new ProgressDialog(activity);
//            dialog.setMessage("Sedang memproses data. Harap tunggu sejenak.");
//            dialog.setCancelable(false);
//            dialog.show();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//
//
//                Param param = new Param();
//                param.x1d = sp.getString("username", "");
//                param.type = "mmm";
//                param.key = Utilities.imei(activity);
//                param.token = sp.getString("token", "");
////                param.id_kelas = kd_kelas;
////                param.p4ss = xpass;
//
//                Gson gson = new Gson();
//                List<NameValuePair> p = new ArrayList<NameValuePair>();
//                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));
//
//                JsonParser jParser = new JsonParser();
//                json = jParser.getJSONFromUrl(key.url(329), p);
//                Log.e("isi json login", json.toString(2));
//                Log.e("isi json login", gson.toJson(param));
//                code = json.getString("code");
//
//            } catch (Exception e) {
//                background = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//            handler.removeCallbacksAndMessages(null);
//
//            if (background) {
//
//                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
//                ab
//                        .setCancelable(false).setTitle("Informasi");
//                if (code.equals("OK4")) {
//                    ab.setMessage("Siswa telah berhasil dihapus").setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            kirim();
//                        }
//                    }).show();
//                } else if (code.equals("TOKEN2") || code.equals("TOKEN1")) {
//                    SharedPreferences.Editor editorr = sp.edit();
//                    editorr.putString("username", "");
//                    editorr.putString("token", "");
//                    editorr.commit();
//                    ab.setMessage(GenKey.pesan(code))
//                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    dialog.dismiss();
//                                    Intent login = new Intent(activity, Login.class);
//                                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(login);
//                                    finish();
//                                }
//                            }).show();
//                } else {
//                    ab.setMessage(code).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//                }
//
//
//
//            } else {
//                Utilities.codeerror(activity, "ER0211");
//            }
//        }
//
//
//    }


}
