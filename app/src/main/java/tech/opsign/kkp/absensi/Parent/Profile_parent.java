package tech.opsign.kkp.absensi.Parent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.SplashScreen;
import tech.opsign.kkp.absensi.siswa.Fragmen.ToolProfile.Adapter;
import tech.opsign.kkp.absensi.siswa.Fragmen.ToolProfile.Model;

public class Profile_parent extends AppCompatActivity {

    private GenKey key;
    private SharedPreferences sp;
    private Profile_parent activity;
    private RecyclerView recyclerView;
    private List<Model> modelList = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_profile);
        this.activity = this;

        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);

        adapter = new Adapter(modelList);
        recyclerView = findViewById(R.id.profil_kelas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        volley_call();
    }

    private void volley_call() {
        modelList.clear();
        key.showProgress(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(403),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        key.hideProgress();
                        try {


                            JSONObject json = new JSONObject(response);
                            Log.e("ER", json.toString(3));
                            if (json.getBoolean("token")) {
                                if (json.getBoolean("hasil")) {

                                    JSONObject data = json.getJSONObject("profil");
                                    ((TextView) findViewById(R.id.nis)).setText(data.getString("nis"));
                                    ((TextView) findViewById(R.id.nama)).setText(data.getString("nama_siswa"));
                                    ((TextView) findViewById(R.id.nisn)).setText(data.getString("nisn"));
                                    String jeniskelamin;
                                    if (data.getString("jenkel").equals("L")) {
                                        jeniskelamin = "Laki-Laki";
                                    } else if (data.getString("jenkel").equals("P")) {
                                        jeniskelamin = "Perempuan";
                                    } else {
                                        jeniskelamin = "-";
                                    }
                                    ((TextView) findViewById(R.id.jenkel)).setText(jeniskelamin);
                                    ((TextView) findViewById(R.id.tmp_tgl_lahir)).setText(data.getString("tmp_lahir").toUpperCase());
                                    ((TextView) findViewById(R.id.tmp_tgl_lahir2)).setText(Utilities.gettgl_lahir(data.getString("tgl_lahir")));
                                    ((TextView) findViewById(R.id.agama)).setText(data.getString("agama"));
                                    ((TextView) findViewById(R.id.orangtua)).setText(data.getString("orang_tua"));
                                    ((TextView) findViewById(R.id.alamat)).setText(data.getString("alamat"));


                                    Model row;
                                    JSONArray aray = json.getJSONArray("kelas");
                                    if (aray.length() > 0) {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        findViewById(R.id.nulldata).setVisibility(View.GONE);
                                        for (int i = 0; i < aray.length(); i++) {
//                                      for(int i =0; i<1;i++){
                                            data = aray.getJSONObject(i);
                                            // type true akan menghilangkan row kelas
                                            row = new Model(
                                                    data.getString("kd_kelas") + "",
                                                    data.getString("kelas") + "",
                                                    data.getString("tahun_ajar").substring(0, 4) + "/" +
                                                            data.getString("tahun_ajar").substring(4)

                                            );
                                            modelList.add(row);
                                        }
                                    } else {

                                        recyclerView.setVisibility(View.GONE);
                                        findViewById(R.id.nulldata).setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();

                                } else {
                                    androidx.appcompat.app.AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                                    ab.setCancelable(false).setTitle("Informasi");
                                    ab.setMessage(("message")).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
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
                                androidx.appcompat.app.AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                                ab.setCancelable(false).setTitle("Informasi");
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
                            .setPositiveButton("Coba Lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    volley_call();
                                }
                            }).show();
                } catch (Exception se) {
                    se.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                postMap.put("xnip", uid.getText().toString().trim());
//                postMap.put("xpassword", (pass.getText().toString().trim()));
//                postMap.put("xtype", "1");
//                postMap.put("xkey", generate.imei(activity));
//                Log.e("ER", generate.Ubah_POST(postMap, core) + "  ");

                Map<String, String> postMap2 = new HashMap<>();
                try {
                    JsonObject xdata = new JsonObject();


                    xdata.addProperty("x1d", sp.getString("username", ""));
                    xdata.addProperty("token", sp.getString("token", ""));
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
