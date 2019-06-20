package tech.opsign.kkp.absensi.admin.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Adapter;
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Model;

public class DashboardFragmentAdmin extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;

    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;


    private List<Model> modelList = new ArrayList<>();
    private Adapter adapter;

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
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list_kehadiran_siswa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mulai();
        return v;
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
        String x1d, type, key, token;
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
                    ((TextView) v.findViewById(R.id.info)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) v.findViewById(R.id.keterangan)).setText("Masuk");
                    ((TextView) v.findViewById(R.id.info)).setVisibility(View.GONE);
                }
                ((TextView) v.findViewById(R.id.info)).setText(data.getString("ket"));

                Model row;
                JSONArray aray = json.getJSONArray("list_absen");
                if (aray != null && aray.length() > 0) {
                    for(int i =0; i<aray.length();i++){
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
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }
    }


}
