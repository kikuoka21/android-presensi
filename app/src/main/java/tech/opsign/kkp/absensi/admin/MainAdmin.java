package tech.opsign.kkp.absensi.admin;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Fragment.DashboardFragmentAdmin;

public class MainAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private View navHeaderView;
    private MainAdmin activity;
    private boolean doubleBackToExitPressedOnce = false;
    private Handler halder;
    private AsyncTask start;
    private boolean flag = true;

    private GenKey key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show();
        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences(key.key(9145), 0x0000);
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
        }
        navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header);

        mulai();

    }


    private void mulai() {
//        start = new cektoken().execute();
    }

    private class cektoken extends AsyncTask<Void, Void, Void> {
        private boolean background;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {


            } catch (Exception e) {
//
                background = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(activity);
            halder.removeCallbacksAndMessages(null);
            try {

//                getprofil();
            } catch (Exception e) {
                codeeror("ER0023");
            }

        }
    }


    private void getprofil() {

//        Bitmap logo = null;
        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//
//            JSONObject nama = new JSONObject(sp.getString(GenKey.GenKey(1102), ""));
//            JSONObject foto = new JSONObject(sp.getString(GenKey.GenKey(8920), ""));
//            JSONObject df = foto.getJSONObject("data");

//            ((TextView) navHeaderView.findViewById(R.id.nama_nav)).setText(sp.getString(key.key(1102), ""));
//            ((TextView) navHeaderView.findViewById(R.id.nip_nav)).setText(sp.getString(key.key(1001), ""));
//
//            NavigationView navigationView = findViewById(R.id.nav_view);
//            View hView = navigationView.getHeaderView(0);

//            InputStream is = new URL(df.getString("path")).openStream();
//            try {
//                logo = BitmapFactory.decodeStream(is);
//                if (logo != null)
//                    ((ImageView) hView.findViewById(R.id.profile_image)).setImageBitmap(logo);
//            } finally {
//                is.close();
//            }
        } catch (Exception e) {
            codeeror("ER0021");
//            Log.e("ER", String.valueOf(e));

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu pojok kanan atas titik 3 https://stackoverflow.com/questions/46967736/how-to-remove-settings-option-in-navigation-drawer-activity-in-android-studio
        getMenuInflater().inflate(R.menu.menu_samping, menu);
//       return true;
        return false;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(false);
        int id = item.getItemId();
//        FragmentManager fragmentManager = getFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//        if (id == R.id.nav_profile) {
//            Intent intent = new Intent(activity, DataDiri.class);
//            startActivity(intent);
//        }
//        if (id == R.id.riwayatpgw) {
//            Intent intent = new Intent(activity, RiwayatPekerjaan.class);
//            startActivity(intent);
//        }
//        if (id == R.id.absensi) {
//            Intent intent = new Intent(activity, Absensi.class);
//            startActivity(intent);
//        }
//        if (id == R.id.form_izin) {
//            Intent intent = new Intent(activity, Pengajuan_surat.class);
//            startActivity(intent);
//        }
//        if (id == R.id.history_ijin) {
//            Intent intent = new Intent(activity, HistoriIzin.class);
//            startActivity(intent);
//        }
//        if (id == R.id.mnggu_persetujuan) {
//            Intent intent = new Intent(activity, Menunggu_Persetujuan.class);
//            startActivity(intent);
//        }

//        if (id == R.id.nav_dash) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter
//                    , new DashboardFragmentSiswa()).commit();
//        }

//        if (id == R.id.nav_dos_penelitian) {
////            Intent intent = new Intent(activity, DPenelitian.class);
////            startActivity(intent);
//            /*new AlertDialog.Builder(activity).setTitle("Informasi").setMessage("Mohon maaf, untuk saat ini fitur yang Anda pilih masih dalam pengembangan.").setNegativeButton("tutup", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }).show();*/
//        }

//
//        if (id == R.id.nav_out) {
//            new AlertDialog.Builder(activity)
//                    .setCancelable(false).setTitle("Konfirmasi")
//                    .setMessage("Apakah Anda yakin ingin keluar ?")
//                    .setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            SharedPreferences.Editor editorr = sp.edit();
//                            editorr.putString(key.key(1001), "");
//                            editorr.putString(key.key(1002), "");
//                            editorr.commit();
//                            startActivity(new Intent(activity, Login.class));
//                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                            finish();
//                        }
//                    })
//                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//
//        }

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

    public class Param {
        String xnip, xtoken, xuser_key, xuser_type;
    }

    private void codeeror(String kode) {
        if (flag) {
            Utilities.codeerror(activity, kode);
            flag = false;
        }
    }

}
