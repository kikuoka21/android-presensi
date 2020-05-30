package tech.opsign.kkp.absensi.Parent;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.siswa.Fragmen.Dashboard;
import tech.opsign.kkp.absensi.siswa.scan_qr_code;

public class MainParent2 extends AppCompatActivity {
    private SharedPreferences sp;
    private MainParent2 activity;
    private boolean doubleBackToExitPressedOnce = false;
    private Handler handler;
    private AsyncTask start;

    private GenKey key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainactivity_parent);
        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show();
        this.activity = this;
        sp = activity.getSharedPreferences("shared", 0x0000);
        key = new GenKey();
        handler = new Handler();


        ((TextView) findViewById(R.id.namasiswa)).setText(sp.getString("nama", ""));

        ((TextView) findViewById(R.id.tanggal)).setText(Utilities.gettanggal(sp.getString("tanggal", "")));
//        ((TextView) navHeaderView.findViewById(R.id.nip_nav)).setText(sp.getString("username", ""));
        mulai();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu pojok kanan atas titik 3 https://stackoverflow.com/questions/46967736/how-to-remove-settings-option-in-navigation-drawer-activity-in-android-studio
//        getMenuInflater().inflate(R.menu.menu_scan_qr, menu);
        // return true -> akan menampilkan menu ini
        //return false -> menghilangkan menu ini
        return false;
    }


    private void mulai() {
        start = new callAPI().execute();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (key.progres_isShowing()) {
                    start.cancel(true);
                    key.hideProgress();
                    new androidx.appcompat.app.AlertDialog.Builder(activity)
                            .setTitle("Informasi")
                            .setMessage("Telah Terjadi Kesalahan Pada Koneksi Anda.")
                            .setCancelable(false)
                            .setPositiveButton("Coba Lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mulai();
                                }
                            }).show();
                }
            }
        }, Utilities.rto());
    }

    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan KEMBALI lagi untuk keluar", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);


    }

    private class Param {
        String x1d, type, key, token, token_firebase;
    }

    private class callAPI extends AsyncTask<Void, Void, Void> {

        private JSONObject json;
        private boolean background;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            background = true;
            key.showProgress(activity, false);

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
                param.token_firebase = sp.getString("token_firebase", "");
//                param.kd_kls = sp.getString("token", "");

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(402), p);
//                Log.e("param login ", gson.toJson(param));
                Log.e("isi json login", json.toString(2));

            } catch (Exception e) {
                background = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            key.hideProgress();
            handler.removeCallbacksAndMessages(null);

            if (background) {


                androidx.appcompat.app.AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setCancelable(false).setTitle("Informasi");

                try {
                    if (json.getBoolean("token")) {
                        if (json.getBoolean("hasil")) {

                            proses();
                        } else {
                            ab.setMessage(json.getString("message")).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    } else {
                        SharedPreferences.Editor editorr = sp.edit();
                        editorr.putString("username", "");
                        editorr.putString("token", "");
                        editorr.apply();
                        ab.setMessage(GenKey.pesan(json.getString("message")))
                                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                        Intent login = new Intent(activity, Login.class);
                                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(login);
                                        activity.finish();
                                    }
                                }).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                Utilities.codeerror(activity, "ER0211");
            }
        }

        private void proses() {
            try {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("kd_kelas", json.getString("kd_kelas"));
                editor.putString("nama_kelas", json.getString("nm_kelas"));
                editor.apply();

                json = json.getJSONObject("hari_ini");
                if (json.getBoolean("status")) {
                    ((TextView) findViewById(R.id.keterangan)).setText("Masuk");
                    findViewById(R.id.info).setVisibility(View.GONE);
                } else {
                    ((TextView) findViewById(R.id.keterangan)).setText("Libur");
                    ((TextView) findViewById(R.id.info)).setText(json.getString("ket"));
                    findViewById(R.id.info).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }
    }

}
