package tech.opsign.kkp.absensi.admin;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.GantiPass;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Fragment.DashboardFragmentAdmin;
import tech.opsign.kkp.absensi.admin.Fragment.master_kelas;
import tech.opsign.kkp.absensi.admin.Fragment.master_siswa;
import tech.opsign.kkp.absensi.admin.Fragment.master_staf;
import tech.opsign.kkp.absensi.admin.Fragment.master_tanggal;
import tech.opsign.kkp.absensi.admin.Fragment.presensi;
import tech.opsign.kkp.absensi.admin.Presensi.Carikelas_tanggal;

public class MainAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private View navHeaderView;
    private MainAdmin activity;
    private boolean doubleBackToExitPressedOnce = false;
    private Handler halder;
    private AsyncTask start;
    private boolean flag = true;

    private GenKey key;
    Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_mainactivity_admin);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show();
        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        halder = new Handler();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,
                    new DashboardFragmentAdmin()).commit();
//                    new presensi()).commit();
        }
        navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header);
        navigationView.setItemIconTintList(null);

        mHandler = new Handler();
        String level = sp.getString("level", "");
        Menu menu = navigationView.getMenu();
        if (level.equals("1")) {
            menu.findItem(R.id.master).setVisible(true);
            menu.findItem(R.id.ubahpresensi).setVisible(true);
        } else {
            menu.findItem(R.id.master).setVisible(false);
            menu.findItem(R.id.ubahpresensi).setVisible(false);
        }
        ((TextView) navHeaderView.findViewById(R.id.nama_nav)).setText(sp.getString("nama", ""));
        ((TextView) navHeaderView.findViewById(R.id.nip_nav)).setText(sp.getString("username", ""));

//        m_Runnable.run();

    }


//    private final Runnable m_Runnable = new Runnable()
//    {
//        public void run()
//
//        {
//            Toast.makeText(activity,"in runnable "+ aaa(),Toast.LENGTH_SHORT).show();
//
//            mHandler.postDelayed(m_Runnable, 7000);
//        }
//
//    };
//    int a=1;
//    String aaa(){
//
//        a++;
//        return String.valueOf(a);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu pojok kanan atas titik 3 https://stackoverflow.com/questions/46967736/how-to-remove-settings-option-in-navigation-drawer-activity-in-android-studio
        getMenuInflater().inflate(R.menu.menu_scan_qr, menu);
//       return true;
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(false);
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.ubahpresensi) {
            Intent myIntent = new Intent(activity, Carikelas_tanggal.class);
            myIntent.putExtra("next_action", "111");
            myIntent.putExtra("ubah", "y");
            startActivity(myIntent);
        }
        if (id == R.id.laporan) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new presensi()).commit();
        }
//
        if (id == R.id.homedashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new DashboardFragmentAdmin()).commit();
        }
        if (id == R.id.input_tanggal) {
//            startActivity(new Intent(activity, input_tanggal.class));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new master_tanggal()).commit();
        }
        if (id == R.id.input_siswa) {
//            startActivity(new Intent(activity, input_siswa.class));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new master_siswa()).commit();
        }
        if (id == R.id.input_staff) {
//            startActivity(new Intent(activity, input_tanggal.class));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new master_staf()).commit();
        }
        if (id == R.id.input_kelas) {
//            startActivity(new Intent(activity, input_siswa.class));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new master_kelas()).commit();
        }
        if (id == R.id.ganti_pswd) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
                    , new GantiPass()).commit();
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
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }


    private void codeeror(String kode) {
        if (flag) {
            Utilities.codeerror(activity, kode);
            flag = false;
        }
    }

}
