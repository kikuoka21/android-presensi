package tech.opsign.kkp.absensi.siswa;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.GantiPass;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.SplashScreen;
import tech.opsign.kkp.absensi.siswa.Fragmen.Dashboard;
import tech.opsign.kkp.absensi.siswa.Fragmen.Profile;
import tech.opsign.kkp.absensi.siswa.pengurus.generate_qr;
import tech.opsign.kkp.absensi.siswa.pengurus.lihatpresensi_tanggal;

public class MainSiswa extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private View navHeaderView;
    private MainSiswa activity;
    private GenKey key;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_mainactivity_siswa);
        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show();
        this.activity = this;
        sp = activity.getSharedPreferences("shared", 0x0000);

        key = new GenKey();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                    new Dashboard()).commit();
//                    new GantiPass()).commit();
        }
        navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header);
        navigationView.setItemIconTintList(null);

        String level = sp.getString("level", "");
        Log.e("ER_", level);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("toolbar", "Clicked");
            }
        });
//                setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("toolbar","Clicked");
//            }
//        });

        Menu menu = navigationView.getMenu();
        if (level.equals("1")) {
            menu.findItem(R.id.pengurus).setVisible(true);
        } else {
            menu.findItem(R.id.pengurus).setVisible(false);
        }

        ((TextView) navHeaderView.findViewById(R.id.nama_nav)).setText(sp.getString("nama", ""));
        ((TextView) navHeaderView.findViewById(R.id.nip_nav)).setText(sp.getString("username", ""));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu pojok kanan atas titik 3 https://stackoverflow.com/questions/46967736/how-to-remove-settings-option-in-navigation-drawer-activity-in-android-studio
        getMenuInflater().inflate(R.menu.menu_scan_qr, menu);
        // return true -> akan menampilkan menu ini
        //return false -> menghilangkan menu ini
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.scanqr) {
            Intent intent = new Intent(activity, scan_qr_code.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(false);
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (id == R.id.presensi_saya) {
            Intent intent  = new Intent(activity, cari_presensi.class);
            startActivity(intent);
        }
        if (id == R.id.lihat_presensi_hari) {
            Intent intent  = new Intent(activity, lihatpresensi_tanggal.class);
            startActivity(intent);
        }
        if (id == R.id.homedashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new Dashboard()).commit();
        }
        if (id == R.id.profil) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new Profile()).commit();
        }
        if (id == R.id.ganti_pswd) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new GantiPass()).commit();
        }


        if (id == R.id.buatqr) {
            Intent intent = new Intent(activity, generate_qr.class);
            startActivity(intent);
        }
        if (id == R.id.nav_out) {
            new AlertDialog.Builder(activity)
                    .setCancelable(false).setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin keluar ?")
                    .setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          logout();
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        FragmentManager fragmentManager = getFragmentManager();


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            this.doubleBackToExitPressedOnce = false;
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan KEMBALI lagi untuk keluar", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }

    private void logout() {
        key.showProgress(activity, true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(404),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        key.hideProgress();
                        try {


                            JSONObject json = new JSONObject(response);
                            Log.e("ER", json.toString(3));
                            if (json.getBoolean("hasil")) {

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username", "");
                                editor.putString("token", "");
                                editor.apply();
                                startActivity(new Intent(activity, SplashScreen.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();


                            } else {
                                androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(activity);
                                ab.setCancelable(false).setTitle("Informasi");
                                ab.setMessage(json.getString("message")).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                key.hideProgress();

                try {
                    String message;
                    if (!(error instanceof NetworkError | error instanceof TimeoutError)) {

                        NetworkResponse networkResponse = error.networkResponse;
                        message = "Gagal terhubung dengan server, siahkan coba beberapa menit lagi\nError Code: " + networkResponse.statusCode;

                    } else {
                        Log.e("ER", (error instanceof NetworkError) + "" + error.getMessage());
                        message = "Gagal terhubung dengan server, siahkan coba beberapa menit lagi";
                    }


                    new androidx.appcompat.app.AlertDialog.Builder(activity)
                            .setTitle("Informasi")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } catch (Exception se) {
                    se.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> postMap2 = new HashMap<>();
                try {
                    JsonObject xdata = new JsonObject();


                    xdata.addProperty("x1d", sp.getString("username", ""));
                    xdata.addProperty("token", sp.getString("token", ""));
                    xdata.addProperty("wali", false);
                    Log.e("er", xdata.toString());
                    postMap2.put("parsing", xdata.toString());
                } catch (Exception e) {
                    postMap2 = null;
                }
                return postMap2;
            }
        };

        //make the request to your server as indicated in your request URL
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Utilities.rto(),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(activity).add(stringRequest);
    }

}
