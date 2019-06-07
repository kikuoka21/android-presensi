package tech.opsign.kkp.absensi.admin.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Tools.GenKey;
import tech.opsign.kkp.absensi.R;

public class InputTanggal extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Input Tanggal");
        v = inflater.inflate(R.layout.a_fragmen_inputtanggal, container, false);

        key = new GenKey();
        sp = v.getContext().getSharedPreferences(key.key(9145), 0x0000);
//        getnama();
        Log.e("ER", "Input tanggal");

        return v;
    }




}
