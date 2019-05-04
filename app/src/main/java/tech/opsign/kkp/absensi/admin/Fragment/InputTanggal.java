package tech.opsign.kkp.absensi.admin.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Adapter;
import tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard.Model;

public class InputTanggal extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Input Tanggal");
        v = inflater.inflate(R.layout.fragmen_inputtanggal, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences(key.key(9145), 0x0000);
//        getnama();
        Log.e("ER", "kandnwkaksdad");

        getnama();
        return v;
    }

    private void getnama() {

    }


}
