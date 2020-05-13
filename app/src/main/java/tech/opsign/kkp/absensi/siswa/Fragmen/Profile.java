package tech.opsign.kkp.absensi.siswa.Fragmen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import tech.opsign.kkp.absensi.siswa.Fragmen.ToolProfile.Adapter;
import tech.opsign.kkp.absensi.siswa.Fragmen.ToolProfile.Model;

public class Profile extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;

    private RecyclerView recyclerView;
    private List<Model> modelList = new ArrayList<>();
    private Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile ");
        v = inflater.inflate(R.layout.s_fragmen_profile, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences("shared", 0x0000);
        handler = new Handler();
//        ((TextView) v.findViewById(R.id.textView14)).setText(sp.getString("nama", ""));
//        ((TextView) v.findViewById(R.id.tanggal)).setText(Utilities.gettanggal(sp.getString("tanggal", "")));

        adapter = new Adapter(modelList);
        recyclerView = (RecyclerView) v.findViewById(R.id.profil_kelas);
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
                json = jParser.getJSONFromUrl(key.url(103), p);
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
                Utilities.codeerror(v.getContext(), "ER0211");
            }
        }

        private void proses() {
            try {
                json = json.getJSONObject("data");
                JSONObject data = json.getJSONObject("profil");
                ((TextView) v.findViewById(R.id.nis)).setText(data.getString("nis"));
                ((TextView) v.findViewById(R.id.nama)).setText(data.getString("nama_siswa"));
                ((TextView) v.findViewById(R.id.nisn)).setText(data.getString("nisn"));
                String jeniskelamin ;
                if(data.getString("jenkel").equals("L")){
                    jeniskelamin ="Laki-Laki";
                }else if(data.getString("jenkel").equals("P")){
                    jeniskelamin = "Perempuan";
                }else {
                    jeniskelamin="-";
                }
                ((TextView) v.findViewById(R.id.jenkel)).setText(jeniskelamin);
                ((TextView) v.findViewById(R.id.tmp_tgl_lahir)).setText(data.getString("tmp_lahir").toUpperCase() + ", " + Utilities.gettgl_lahir(data.getString("tgl_lahir")));
                ((TextView) v.findViewById(R.id.agama)).setText(data.getString("agama"));
                ((TextView) v.findViewById(R.id.orangtua)).setText(data.getString("orang_tua"));
                ((TextView) v.findViewById(R.id.alamat)).setText(data.getString("alamat"));


                Model row;
                JSONArray aray = json.getJSONArray("kelas");
                if (aray != null && aray.length() > 0) {
                    for (int i = 0; i < aray.length(); i++) {
//               for(int i =0; i<1;i++){
                        data = aray.getJSONObject(i);
                        // type true akan menghilangkan row kelas
                        row = new Model(
                                data.getString("kd_kelas"),
                                data.getString("kelas"),
                                data.getString("tahun_ajar").substring(0, 4) + "/" +
                                        data.getString("tahun_ajar").substring(4)

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
