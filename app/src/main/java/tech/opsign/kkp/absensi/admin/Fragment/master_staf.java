package tech.opsign.kkp.absensi.admin.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tech.opsign.kkp.absensi.R;

public class master_staf extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("List Menu Master Tanggal");
        v = inflater.inflate(R.layout.a_fragmen_mastertanggal, container, false);



        return v;
    }

}
