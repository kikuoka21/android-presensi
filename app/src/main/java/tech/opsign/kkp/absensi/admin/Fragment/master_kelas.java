package tech.opsign.kkp.absensi.admin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Master.kelas.cari_kelas;
import tech.opsign.kkp.absensi.admin.Master.kelas.input_kelas;

public class master_kelas extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("List Menu Master Kelas");
        v = inflater.inflate(R.layout.a_fragmen_masterkelas, container, false);

        Button input = (Button) v.findViewById(R.id.input_kelas);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), input_kelas.class));
            }
        });
        Button cari = (Button) v.findViewById(R.id.lihat_kelas);
        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), cari_kelas.class));
            }
        });
        Button hapus = (Button) v.findViewById(R.id.hapus_kelas);
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), hapus_siswa.class));
            }
        });


        return v;
    }

}