package tech.opsign.kkp.absensi.admin.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Adapter;
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Model;
import tech.opsign.kkp.absensi.admin.Presensi.Carikelas_tanggal;

import static com.github.mikephil.charting.animation.Easing.EaseInOutCubic;

public class DashboardFragmentAdmin extends Fragment {

    private GenKey key;
    private View v;
    private static SharedPreferences sp;

    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;


    private List<Model> modelList = new ArrayList<>();
    private Adapter adapter;
    private PieChart pieChart;
    private int[] MATERIAL_COLORS = {
            Color.rgb(217, 80, 138), Color.rgb(254, 149, 7),
            Color.rgb(106, 167, 134), Color.rgb(53, 194, 209),
            Color.rgb(255, 140, 157), ColorTemplate.rgb("#2ecc71"),
            ColorTemplate.rgb("#f1c40f"), ColorTemplate.rgb("#e74c3c"), ColorTemplate.rgb("#3498db"),
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0),
            Color.rgb(245, 199, 0), Color.rgb(106, 150, 31),
            Color.rgb(179, 100, 53), Color.rgb(64, 89, 128),
            Color.rgb(149, 165, 124), Color.rgb(191, 134, 134),
            Color.rgb(179, 48, 80), Color.rgb(42, 109, 130)
    };
    private static TextView txt_tanggal;
    private static Button Button_carikelas;
    private static String strtanggal;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.home);
        v = inflater.inflate(R.layout.a_fragmen_admin, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences("shared", 0x0000);
        handler = new Handler();

        ((TextView) v.findViewById(R.id.textView14)).setText(sp.getString("nama", ""));
        txt_tanggal = v.findViewById(R.id.tanggal);
        txt_tanggal.setText(Utilities.gettanggal(sp.getString("tanggal", "")));


        adapter = new Adapter(modelList);
        RecyclerView recyclerView = v.findViewById(R.id.list_kehadiran_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        Button_carikelas = v.findViewById(R.id.caridata);
        Button_carikelas.setVisibility(View.GONE);
        mulai();


        Button_carikelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volley_call();
            }
        });
        pieChart = v.findViewById(R.id.piechart);


//        pieChart.getDescription().setEnabled(false);

        pieChart.setDragDecelerationFrictionCoef(0.99f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(15);
        pieChart.setHoleColor(0xffeeeeee);
        pieChart.setTransparentCircleRadius(25);

        pieChart.setEntryLabelTextSize(18);
        pieChart.animateY(1000, EaseInOutCubic);

        Description description = new Description();
//        description.setPosition();
        description.setText("*hanya siswa yang berstatus Alpha");
        pieChart.setDescription(description);


        Log.e("ER", MATERIAL_COLORS.length + " MATERIAL_COLORS " + Arrays.toString(MATERIAL_COLORS));
        v.findViewById(R.id.pilihtgl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogfragment = new tanggalmulai();
                dialogfragment.show(getActivity().getFragmentManager(), "Tanggal Mulai");

            }
        });
        return v;
    }

    public static class tanggalmulai extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),
                    android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat
                    date = new SimpleDateFormat("yyyy-MM-dd");
            String strin = year + "-" + Carikelas_tanggal.ubahan(month + 1) + "-" + Carikelas_tanggal.ubahan(day);

            try {
                Date tanggalpilihan, tanggalskrng;
                tanggalskrng = date.parse(sp.getString("tanggal", ""));
                tanggalpilihan = date.parse(strin);
                if ((tanggalpilihan.before(tanggalskrng) || tanggalpilihan.equals(tanggalskrng))) {
                    strtanggal = strin;

                    txt_tanggal.setText(Utilities.gettanggal(strin));
//                    volley_call();

                    Button_carikelas.setVisibility(View.VISIBLE);

                } else {
                    strtanggal = sp.getString("tanggal", "");

                    Button_carikelas.setVisibility(View.GONE);
                    txt_tanggal.setText(Utilities.gettanggal(sp.getString("tanggal", "")));
                    Utilities.showMessageBox(getActivity(), "Informasi", "Pilihan Tanggal Tidak Boleh Setelah " + Utilities.gettanggal(sp.getString("tanggal", "")));
//                    tgl.setText("Pilih Tanggal");

                }
            } catch (Exception e) {
//                e.printStackTrace();
            }


        }

    }

    public class MyValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return "" + ((int) value);
        }
    }

    private void mulai() {
        start = new callAPI().execute();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    start.cancel(true);
                    new AlertDialog.Builder(v.getContext())
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

    private class Param {
        String x1d, type, key, token, token_firebase;
    }

    private void volley_call() {
        modelList.clear();
        adapter.notifyDataSetChanged();
        Button_carikelas.setVisibility(View.GONE);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, key.url(303),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.e("ER", json.toString(3));

                            AlertDialog.Builder ab = new AlertDialog.Builder(v.getContext());
                            ab.setCancelable(false).setTitle("Informasi");
                            String code = json.getString("code");
                            if (code.equals("OK4")) {

                                JSONObject data = json.getJSONObject("date");
                                if (data.getString("status").equals("L")) {
                                    ((TextView) v.findViewById(R.id.keterangan)).setText("Libur");
                                    v.findViewById(R.id.info).setVisibility(View.VISIBLE);
                                } else {
                                    ((TextView) v.findViewById(R.id.keterangan)).setText("Ada Kegiatan Belajar Mengajar");
                                    v.findViewById(R.id.info).setVisibility(View.GONE);
                                }
                                ((TextView) v.findViewById(R.id.info)).setText(data.getString("ket"));

                                Model row;
                                JSONArray aray = json.getJSONArray("list_absen");

                                if (aray.length() > 0) {
                                    v.findViewById(R.id.list_kehadiran_siswa).setVisibility(View.VISIBLE);
                                    for (int i = 0; i < aray.length(); i++) {
                                        data = aray.getJSONObject(i);
                                        // type true akan menghilangkan row kelas
                                        row = new Model(
                                                data.getString("nis"),
                                                data.getString("nama"),
                                                data.getString("kelas"),
                                                data.getString("ket")

                                        );
                                        modelList.add(row);
                                    }
                                } else
                                    v.findViewById(R.id.list_kehadiran_siswa).setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();


                                json = json.getJSONObject("statistik");
                                ((TextView) v.findViewById(R.id.hadir)).setText(json.getString("hadir"));
                                ((TextView) v.findViewById(R.id.alpha)).setText(json.getString("alpha"));
                                ((TextView) v.findViewById(R.id.sakit)).setText(json.getString("sakit"));
                                ((TextView) v.findViewById(R.id.izin)).setText(json.getString("izin"));
                                ((TextView) v.findViewById(R.id.telat)).setText(json.getString("telat"));

                                JSONArray list_kelas = json.getJSONArray("list_kelas");
                                if (list_kelas.length() > 0) {
                                    pieChart.setVisibility(View.VISIBLE);
                                    ArrayList<PieEntry> datapie = new ArrayList<>();

                                    for (int i = 0; i < list_kelas.length(); i++) {
                                        data = list_kelas.getJSONObject(i);
                                        if (data.getInt("alpha") > 0)
                                            datapie.add(new PieEntry(data.getInt("alpha"), "kelas " + data.getString("kelas")));
                                    }

                                    PieDataSet pieDataSet = new PieDataSet(datapie, "");
                                    pieDataSet.setSliceSpace(2);
                                    pieDataSet.setSelectionShift(6f);
                                    pieDataSet.setValueTextSize(18);
                                    pieDataSet.setValueFormatter(new MyValueFormatter());
                                    pieDataSet.setColors(MATERIAL_COLORS);

                                    PieData pieData = new PieData(pieDataSet);


                                    pieChart.setData(pieData);
                                    pieChart.invalidate();
                                    if (list_kelas.length() > 6) {

                                        pieChart.getLegend().setEnabled(false);
                                    } else

                                        pieChart.getLegend().setEnabled(true);
                                } else {
                                    pieChart.setVisibility(View.GONE);
                                }

                            } else if (code.equals("TOKEN2") || code.equals("TOKEN1")) {
                                txt_tanggal.setText(Utilities.gettanggal(sp.getString("tanggal", "")));
                                SharedPreferences.Editor editorr = sp.edit();
                                editorr.putString("username", "");
                                editorr.putString("token", "");
                                editorr.commit();
                                ab.setMessage(GenKey.pesan(code))
                                        .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                                Intent login = new Intent(v.getContext(), Login.class);
                                                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(login);
                                                getActivity().finish();
                                            }
                                        }).show();
                            } else {
                                txt_tanggal.setText(Utilities.gettanggal(sp.getString("tanggal", "")));
                                ab.setMessage(code).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
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

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                txt_tanggal.setText(Utilities.gettanggal(sp.getString("tanggal", "")));
                try {
                    String message;
                    if (!(error instanceof NetworkError | error instanceof TimeoutError)) {

                        NetworkResponse networkResponse = error.networkResponse;
                        message = "Gagal terhubung dengan server, siahkan coba beberapa menit lagi\nError Code: " + networkResponse.statusCode;

                    } else {
                        Log.e("ER", (error instanceof NetworkError) + "" + error.getMessage());
                        message = "Gagal terhubung dengan server, siahkan coba beberapa menit lagi";
                    }


                    new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
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

//                postMap.put("xnip", uid.getText().toString().trim());
//                postMap.put("xpassword", (pass.getText().toString().trim()));
//                postMap.put("xtype", "1");
//                postMap.put("xkey", generate.imei(activity));
//                Log.e("ER", generate.Ubah_POST(postMap, core) + "  ");

                Map<String, String> postMap2 = new HashMap<>();
                try {
                    JsonObject xdata = new JsonObject();


                    xdata.addProperty("x1d", sp.getString("username", ""));
                    xdata.addProperty("type", "mmm");
                    xdata.addProperty("token", sp.getString("token", ""));
                    xdata.addProperty("token_firebase", sp.getString("token_firebase", ""));
                    xdata.addProperty("tgl", strtanggal);

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
        Volley.newRequestQueue(v.getContext()).add(stringRequest);
    }

    private class callAPI extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            modelList.clear();
            adapter.notifyDataSetChanged();
            background = true;
            dialog = new ProgressDialog(v.getContext());
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
                param.key = Utilities.imei(getActivity());
                param.token = sp.getString("token", "");
                param.token_firebase = sp.getString("token_firebase", "");

//                param.kd_kls = sp.getString("token", "");

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(300), p);
                Log.e("param login ", gson.toJson(param));
                Log.e("isi json login", json.toString(2));
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


                AlertDialog.Builder ab = new AlertDialog.Builder(v.getContext());
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
                                    Intent login = new Intent(v.getContext(), Login.class);
                                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(login);
                                    getActivity().finish();
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
                Utilities.codeerror(v.getContext(), "error saat pemanggilan api");
            }
        }

        private void proses() {
            try {
                JSONObject data = json.getJSONObject("date");
                if (data.getString("status").equals("L")) {
                    ((TextView) v.findViewById(R.id.keterangan)).setText("Libur");
                    v.findViewById(R.id.info).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) v.findViewById(R.id.keterangan)).setText("Ada Kegiatan Belajar Mengajar");
                    v.findViewById(R.id.info).setVisibility(View.GONE);
                }
                ((TextView) v.findViewById(R.id.info)).setText(data.getString("ket"));

                Model row;
                JSONArray aray = json.getJSONArray("list_absen");

                if (aray.length() > 0) {
                    v.findViewById(R.id.list_kehadiran_siswa).setVisibility(View.VISIBLE);
                    for (int i = 0; i < aray.length(); i++) {
//               for(int i =0; i<1;i++){
                        data = aray.getJSONObject(i);
                        // type true akan menghilangkan row kelas
                        row = new Model(
                                data.getString("nis"),
                                data.getString("nama"),
                                data.getString("kelas"),
                                data.getString("ket")

                        );
                        modelList.add(row);
                    }
                } else
                    v.findViewById(R.id.list_kehadiran_siswa).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();


                json = json.getJSONObject("statistik");
                ((TextView) v.findViewById(R.id.hadir)).setText(json.getString("hadir"));
                ((TextView) v.findViewById(R.id.alpha)).setText(json.getString("alpha"));
                ((TextView) v.findViewById(R.id.sakit)).setText(json.getString("sakit"));
                ((TextView) v.findViewById(R.id.izin)).setText(json.getString("izin"));
                ((TextView) v.findViewById(R.id.telat)).setText(json.getString("telat"));

                JSONArray list_kelas = json.getJSONArray("list_kelas");
                if (list_kelas.length() > 0) {
                    pieChart.setVisibility(View.VISIBLE);
                    ArrayList<PieEntry> datapie = new ArrayList<>();

                    for (int i = 0; i < list_kelas.length(); i++) {
                        data = list_kelas.getJSONObject(i);
                        if (data.getInt("alpha") > 0)
                            datapie.add(new PieEntry(data.getInt("alpha"), "kelas " + data.getString("kelas")));
                    }

                    PieDataSet pieDataSet = new PieDataSet(datapie, "");
                    pieDataSet.setSliceSpace(2);
                    pieDataSet.setSelectionShift(6f);
                    pieDataSet.setValueTextSize(18);
                    pieDataSet.setValueFormatter(new MyValueFormatter());
                    pieDataSet.setColors(MATERIAL_COLORS);

                    PieData pieData = new PieData(pieDataSet);


                    pieChart.setData(pieData);
                    pieChart.invalidate();
                    if (list_kelas.length() > 6) {

                        pieChart.getLegend().setEnabled(false);
                    } else

                        pieChart.getLegend().setEnabled(true);
                } else {
                    pieChart.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }
    }


}
