package tech.opsign.kkp.absensi.Parent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Parent.tool_presensi_parent.Adapter_presensi_parent;
import tech.opsign.kkp.absensi.Parent.tool_presensi_parent.Model_presensi_parent;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.SplashScreen;

public class Presensi_Siswa_parent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SharedPreferences sp;
    private Presensi_Siswa_parent activity;
    private GenKey key;
    private List<Model_presensi_parent> modelList = new ArrayList<>();
    private Adapter_presensi_parent Adapter;
    private String strtahun = "", strbulan = "";
    private String strtanggal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTitle("Rekap Presensi");
        setContentView(R.layout.s_siswa_lihat);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.bulan_tahun).setVisibility(View.VISIBLE);

        Spinner spiner = findViewById(R.id.tahn);
        spiner.setAdapter(null);
        ArrayList<String> jenis = new ArrayList<String>();

        String temp = sp.getString("tanggal", "");

        for (int i = Integer.parseInt(temp.substring(0, 4)); i > 2016; i--) {

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

        Adapter = new Adapter_presensi_parent(modelList);
        RecyclerView recyclerView = findViewById(R.id.list_kelas);
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
        volley_call();
    }

    void volley_call() {
        key.showProgress(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(405),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            androidx.appcompat.app.AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                            ab.setCancelable(false).setTitle("Informasi");

                            JSONObject json = new JSONObject(response);
                            Log.e("ER", json.toString(3));
                            if (json.getBoolean("token")) {
                                if (json.getBoolean("hasil")) {
                                    Log.e("ER", "clearrr");
                                    Model_presensi_parent row;
                                    JSONObject data;
                                    JSONArray aray = json.getJSONArray("kehadiran");

                                    data = json.getJSONObject("datakelas");
                                    ((TextView) findViewById(R.id.presensi_header)).setText("Rekap Presensi " + sp.getString("nama", ""));
                                    ((TextView) findViewById(R.id.thn_ajar)).setText(data.getString("thn_ajar").substring(0, 4) + "/" +
                                            data.getString("thn_ajar").substring(4));
                                    ((TextView) findViewById(R.id.nama_kelas)).setText(data.getString("nama"));
                                    ((TextView) findViewById(R.id.ketua_kelas)).setText(data.getString("ketua"));
                                    ((TextView) findViewById(R.id.walikelas)).setText(data.getString("wali"));

                                    if (aray.length() > 0) {

                                        findViewById(R.id.list_kelas).setVisibility(View.VISIBLE);
                                        findViewById(R.id.nulldata).setVisibility(View.GONE);
                                        for (int i = 0; i < aray.length(); i++) {
//               for(int i =0; i<1;i++){
                                            data = aray.getJSONObject(i);
                                            // type true akan menghilangkan row kelas
                                            row = new Model_presensi_parent(
                                                    Utilities.gettanggal(data.getString("tanggal")),
                                                    data.getString("stat") + " / " + Utilities.status_kehadiran(data.getString("stat")),
                                                    data.getString("ket")

                                            );
                                            modelList.add(row);
                                        }

                                        Adapter.notifyDataSetChanged();
                                    } else {

                                        findViewById(R.id.list_kelas).setVisibility(View.GONE);
                                        findViewById(R.id.nulldata).setVisibility(View.VISIBLE);
                                    }

                                    findViewById(R.id.isian).setVisibility(View.VISIBLE);
//
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
                                                Intent login = new Intent(activity, SplashScreen.class);
                                                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(login);
                                                activity.finish();
                                            }
                                        }).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        key.hideProgress();
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
                            .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
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
                    xdata.addProperty("tanggal", strtanggal);
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
