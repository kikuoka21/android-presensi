package tech.opsign.kkp.absensi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.admin.Master.tanggal.Tool_Input_Tanggal.Adapter_tanggal;
import tech.opsign.kkp.absensi.admin.Master.tanggal.Tool_Input_Tanggal.Model_tanggal;

public class Tanggal_Libur extends AppCompatActivity {
    private Tanggal_Libur activity;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_tanggal> modelList = new ArrayList<>();
    private Adapter_tanggal adapter;
    private PullRefreshLayout refres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tgl_libur);
        this.activity = this;
        key = new GenKey();

        adapter = new Adapter_tanggal(modelList);

        recyclerView = findViewById(R.id.list_tanggal);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        volley_call();
        refres = findViewById(R.id.pull_refresh);
        refres.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        refres.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                volley_call();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refres.setRefreshing(false);
                    }
                }, 2000);

            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refres.setEnabled(topRowVerticalPosition >= 0);
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

    private void clear() {

        modelList.clear();
        adapter.notifyDataSetChanged();
    }

    private void volley_call() {
        clear();
        key.showProgress(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, key.url(406),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        key.hideProgress();
                        try {

                            JSONObject json;

                            JSONArray aray = new JSONArray(response);
                            if (aray.length() > 0) {
                                findViewById(R.id.nulldata).setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                for (int i = 0; i < aray.length(); i++) {
                                    json = aray.getJSONObject(i);
                                    // type true akan menghilangkan row kelas
//                                    Log.e("ER__ keterangan ", json.getString("ket"));
                                    modelList.add(new Model_tanggal(
                                            json.getString("tgl"),
                                            Utilities.gettanggal(json.getString("tgl")),
                                            json.getString("ket")

                                    ));
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                findViewById(R.id.nulldata).setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
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


                    new AlertDialog.Builder(activity)
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


        };

        //make the request to your server as indicated in your request URL
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Utilities.rto(),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(activity).add(stringRequest);
    }

}
