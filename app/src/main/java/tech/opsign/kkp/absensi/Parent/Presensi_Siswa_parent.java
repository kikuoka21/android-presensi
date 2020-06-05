package tech.opsign.kkp.absensi.Parent;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.siswa.tool_presensi.Adapter_presensi;
import tech.opsign.kkp.absensi.siswa.tool_presensi.Model_presensi;

public class Presensi_Siswa_parent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SharedPreferences sp;
    private Presensi_Siswa_parent activity;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_presensi> modelList = new ArrayList<>();
    private Adapter_presensi Adapter;
    private String action = "", strtahun = "", strbulan = "";
    private String strtanggal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.s_siswa_lihat);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        action = intent.getStringExtra("next_action");

        setTitle("Rekap Presensi");

        findViewById(R.id.bulan_tahun).setVisibility(View.VISIBLE);

        Spinner spiner = findViewById(R.id.tahn);
        spiner.setAdapter(null);
        ArrayList<String> jenis = new ArrayList<String>();

        String temp = sp.getString("tanggal", "");

        for (  int i = Integer.parseInt(temp.substring(0, 4))  ; i > 2016; i--) {

            jenis.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spiner_item, jenis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(adapter);
        spiner.setOnItemSelectedListener(this);

        Spinner spinerthn = findViewById(R.id.bulan);
        spinerthn.setAdapter(null);
        ArrayList<String> mont = new ArrayList<String>();
        mont.add("Januari");
        mont.add("Februari");
        mont.add("Maret");
        mont.add("April");
        mont.add("Mei");
        mont.add("Juni");
        mont.add("Juli");
        mont.add("Agustus");
        mont.add("September");
        mont.add("Oktober");
        mont.add("November");
        mont.add("Desember");
        ArrayAdapter<String> adapterbln = new ArrayAdapter<String>(this.activity, R.layout.spiner_item, mont);
        adapterbln.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinerthn.setAdapter(adapterbln);
        spinerthn.setOnItemSelectedListener(this);
        spinerthn.setSelection(Integer.parseInt(sp.getString("tanggal", "").substring(5, 7)) - 1);


        Button button = findViewById(R.id.carikelas);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirim();
            }
        });

        Adapter = new Adapter_presensi(modelList);
        recyclerView = findViewById(R.id.list_kelas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(Adapter);


        findViewById(R.id.isian).setVisibility(View.GONE);
    }

    private void kirim() {

        findViewById(R.id.isian).setVisibility(View.GONE);
        modelList.clear();
        Adapter.notifyDataSetChanged();
        Log.e("ER", "start");
    }

//
//
//    private void proses() {
//        try {
//
//            Model_presensi row;
//            JSONObject data ;
//            JSONArray aray = json.getJSONArray("kehadiran");
////                data.getString("tahun_ajar").substring(0, 4) + "/" +
////                        data.getString("tahun_ajar").substring(4)
//            data = json.getJSONObject("datakelas");
//            ((TextView)findViewById(R.id.presensi_header)).setText("Rekap Presensi "+sp.getString("nama", ""));
//            ((TextView)findViewById(R.id.thn_ajar)).setText(data.getString("thn_ajar").substring(0, 4) + "/" +
//                    data.getString("thn_ajar").substring(4));
//            ((TextView)findViewById(R.id.nama_kelas)).setText(data.getString("nama"));
//            ((TextView)findViewById(R.id.ketua_kelas)).setText(data.getString("ketua"));
//            ((TextView)findViewById(R.id.walikelas)).setText(data.getString("wali"));
//
//            if (aray != null && aray.length() > 0) {
//
//                findViewById(R.id.list_kelas).setVisibility(View.VISIBLE);
//                findViewById(R.id.nulldata).setVisibility(View.GONE);
//                for (int i = 0; i < aray.length(); i++) {
////               for(int i =0; i<1;i++){
//                    data = aray.getJSONObject(i);
//                    // type true akan menghilangkan row kelas
//                    row = new Model_presensi(
//                            Utilities.gettanggal(data.getString("tanggal")),
//                            data.getString("stat")+" / "+Utilities.status_kehadiran(data.getString("stat")),
//                            data.getString("ket")
//
//                    );
//                    modelList.add(row);
//                }
//
//                Adapter.notifyDataSetChanged();
//            }else {
//
//                findViewById(R.id.list_kelas).setVisibility(View.GONE);
//                findViewById(R.id.nulldata).setVisibility(View.VISIBLE);
//            }
//
//            findViewById(R.id.isian).setVisibility(View.VISIBLE);
//
//        } catch (Exception e) {
//            Log.e("ER___", String.valueOf(e));
//        }
//
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent == findViewById(R.id.tahn)) {
            strtahun = parent.getItemAtPosition(position).toString();
        }
        if (parent == findViewById(R.id.bulan)) {
            strbulan = String.valueOf(position + 1);
            if (strbulan.length() == 1) {
                strbulan = "0" + strbulan;
            }
        }
        strtanggal = strtahun + "-" + strbulan;
        Log.e("ER", strtanggal);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
