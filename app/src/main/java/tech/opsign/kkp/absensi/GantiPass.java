package tech.opsign.kkp.absensi;

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
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.JsonParser;
import Tools.Utilities;

public class GantiPass extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private EditText pas_lama, pas_baru_1, pas_baru_2;
    private Button tombol;
    private String pas1_string, pas2_string, paslama_string;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Ganti Password");
        v = inflater.inflate(R.layout.ganti_pass, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        pas_lama = (EditText) v.findViewById(R.id.password_lama);
        pas_baru_1 = (EditText) v.findViewById(R.id.password_baru1);
        pas_baru_2 = (EditText) v.findViewById(R.id.password_baru2);

        pas_lama.addTextChangedListener(logintextwarcher);
        pas_baru_1.addTextChangedListener(logintextwarcher);
        pas_baru_2.addTextChangedListener(logintextwarcher);

        tombol = (Button) v.findViewById(R.id.tombol_ubah);
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pas_baru_2.setError(null);
                if (pas1_string.equals(pas2_string)) {
                    mulai();
                    Log.e("ER", "mulai");
                } else {
                    pas_baru_2.setError("Password Baru Tidak Sama");
                }


            }
        });

        return v;
    }

    private TextWatcher logintextwarcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            pas1_string = pas_baru_1.getText().toString().trim();
            pas2_string = pas_baru_2.getText().toString().trim();
            paslama_string = pas_lama.getText().toString().trim();
            boolean b = !pas1_string.isEmpty() && !pas2_string.isEmpty() && !paslama_string.isEmpty();
            tombol.setEnabled(b);

            if (b)
                tombol.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.button));
            else
                tombol.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.button_deny));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


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
        String x1d, type, key, token, xp4s5, xp4s5_lama;
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
                param.xp4s5 = key.gen_pass(pas2_string);
                param.xp4s5_lama = key.gen_pass(paslama_string);
                Gson gson = new Gson();
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("parsing", gson.toJson(param)));

                JsonParser jParser = new JsonParser();
                json = jParser.getJSONFromUrl(key.url(3), p);
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
                ab.setCancelable(false).setTitle("Informasi");
                if (code.equals("OK4")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", "");
                    editor.putString("token", "");
                    editor.commit();

                    ab
                            .setMessage("Password sudah Berhasil Ganti, Silahkan Login Kembali")
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent login = new Intent(v.getContext(), Login.class);
                                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(login);
                                    getActivity().finish();
                                }
                            })
                            .show();
                } else if (code.equals("TOKEN2") || code.equals("TOKEN1")) {
                    SharedPreferences.Editor editorr = sp.edit();
                    editorr.putString("username", "");
                    editorr.putString("token", "");
                    editorr.commit();

                    ab.setMessage(GenKey.pesan(code)).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            Intent login = new Intent(v.getContext(), Login.class);
                            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                            getActivity().finish();
                        }
                    })
                            .show();
                } else {
                    ab.setMessage(code)
                            .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }


            } else {
                Utilities.codeerror(v.getContext(), "ER0211");
            }
        }

    }

}
