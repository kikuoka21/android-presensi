package tech.opsign.kkp.absensi.siswa;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import Tools.GenKey;
import tech.opsign.kkp.absensi.GantiPass;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.siswa.Fragmen.Dashboard;
import tech.opsign.kkp.absensi.siswa.Fragmen.Profile;
import tech.opsign.kkp.absensi.siswa.pengurus.generate_qr;
import tech.opsign.kkp.absensi.siswa.pengurus.lihatpresensi_tanggal;

public class MainSiswa extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private View navHeaderView;
    private MainSiswa activity;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_mainactivity_siswa);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show();
        this.activity = this;
        sp = activity.getSharedPreferences("shared", 0x0000);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("username", "");
                            editor.putString("token", "");
                            editor.commit();
                            startActivity(new Intent(activity, Login.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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


}
