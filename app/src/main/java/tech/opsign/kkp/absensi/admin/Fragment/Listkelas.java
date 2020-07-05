package tech.opsign.kkp.absensi.admin.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;
import tech.opsign.kkp.absensi.Listener.ItemClickSupport;
import tech.opsign.kkp.absensi.Login;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_kelas.Adapter_kelas_list;
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_kelas.Model_kelas_list;
import tech.opsign.kkp.absensi.admin.Presensi.Generate_qr_admin;

public class Listkelas extends Fragment {

    private View v;
    private GenKey key;
    private SharedPreferences sp;

    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;

    private RecyclerView recyclerView;
    private List<Model_kelas_list> modelList = new ArrayList<>();
    private Adapter_kelas_list adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("List Kelas");
        v = inflater.inflate(R.layout.dry_list, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences("shared", 0x0000);
        handler = new Handler();

        adapter = new Adapter_kelas_list(modelList);
        recyclerView = v.findViewById(R.id.recycle_dry);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mulai();
    }

    private void mulai() {
        modelList.clear();
        adapter.notifyDataSetChanged();
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

                JsonObject xdata = new JsonObject();
                xdata.addProperty("x1d", sp.getString("username", ""));
                xdata.addProperty("type", "mmm");
                xdata.addProperty("token", sp.getString("token", ""));
//                xdata.addProperty("x1d", sp.getString("username", ""));
//                param.key = Utilities.imei(getActivity());
//                param.token = sp.getString("token", "");
//                param.token_firebase = sp.getString("token_firebase", "");

//                param.kd_kls = sp.getString("token", "");

                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(xdata)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(301), p);
                Log.e("param login ", gson.toJson(xdata));
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
                    editorr.apply();
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
        private void showSelectedMatkul(Model_kelas_list kelas) {
//            Toast.makeText(activity, kelas.kd_kelas, Toast.LENGTH_SHORT).show();

                Intent myIntent;
                    myIntent = new Intent(getContext(), Generate_qr_admin.class);

                myIntent.putExtra("kd_kelas", kelas.getKd_kelas());
                myIntent.putExtra("nama", kelas.getKelas());
                startActivity(myIntent);

        }
        private void proses() {
            try {
//                JSONObject data = json.getJSONObject("data");
                JSONArray aray = json.getJSONArray("data");
//                Log.e("ER", data.toString(3));

                if (aray.length() > 0) {

                    Model_kelas_list row;
                    v.findViewById(R.id.nulldata).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < aray.length(); i++) {
                        json = aray.getJSONObject(i);
                        // type true akan menghilangkan row kelas

                        row = new Model_kelas_list(
                                json.getString("id"),
                                json.getString("nama_kelas"),
                                json.getString("wali"),
                                json.getString("ketua")

                        );
                        modelList.add(row);
                    }
                    adapter.notifyDataSetChanged();
                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            showSelectedMatkul(modelList.get(position));
                        }
                    });

                    Toast.makeText(getActivity(), "Pilih kelas untuk membuat QR code", Toast.LENGTH_LONG).show();
                } else {
                    v.findViewById(R.id.nulldata).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e("ER___", String.valueOf(e));
            }
        }
    }

}
