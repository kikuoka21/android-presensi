package tech.opsign.kkp.absensi.admin.Fragment;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Adapter;
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Model;

import static com.github.mikephil.charting.animation.Easing.EaseInOutCubic;

public class DashboardFragmentAdmin extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.home);
        v = inflater.inflate(R.layout.a_fragmen_admin, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences("shared", 0x0000);
        handler = new Handler();

        ((TextView) v.findViewById(R.id.textView14)).setText(sp.getString("nama", ""));
        ((TextView) v.findViewById(R.id.tanggal)).setText(Utilities.gettanggal(sp.getString("tanggal", "")));


        adapter = new Adapter(modelList);
        RecyclerView recyclerView = v.findViewById(R.id.list_kehadiran_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mulai();


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


        Log.e("ER", Arrays.toString(MATERIAL_COLORS));
        return v;
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

    private class callAPI extends AsyncTask<Void, Void, Void> {

        private String code;
        private JSONObject json;
        private boolean background;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    if (list_kelas.length() >6) {

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
