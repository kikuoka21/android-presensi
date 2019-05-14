package tech.opsign.kkp.absensi.siswa.Fragmen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Tools.GenKey;
import tech.opsign.kkp.absensi.R;

public class Dashboard extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Dashboard Siswa");
        v = inflater.inflate(R.layout.s_fragmen_dashboard, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences("shared", 0x0000);

        getnama();
        return v;
    }

    private void getnama() {

        ((TextView)v.findViewById(R.id.textView14)).setText(sp.getString("nama", ""));
    }


}
