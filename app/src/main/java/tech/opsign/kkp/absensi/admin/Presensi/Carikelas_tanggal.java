package tech.opsign.kkp.absensi.admin.Presensi;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_kelas.Adapter_kelas_list;
import tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_kelas.Model_kelas_list;
import tech.opsign.kkp.absensi.admin.Master.kelas.cari_kelas;

public class Carikelas_tanggal extends AppCompatActivity {
    private  SharedPreferences sp;
    private Carikelas_tanggal activity;
    private Handler handler;
    private AsyncTask start;
    private ProgressDialog dialog;
    private GenKey key;
    private RecyclerView recyclerView;
    private List<Model_kelas_list> modelList = new ArrayList<>();
    private Adapter_kelas_list adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_kelas_cari);

        this.activity = this;
        key = new GenKey();
        sp = activity.getSharedPreferences("shared", 0x0000);
        handler = new Handler();
        setTitle("Cari Kelas");
    }
}
