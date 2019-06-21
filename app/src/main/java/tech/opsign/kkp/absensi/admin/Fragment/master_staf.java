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
import tech.opsign.kkp.absensi.admin.Master.staf.Cari_staf;
import tech.opsign.kkp.absensi.admin.Master.staf.input_staff;
import tech.opsign.kkp.absensi.admin.Master.tanggal.input_tanggal;

public class master_staf extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("List Menu Master Tanggal");
        v = inflater.inflate(R.layout.a_fragmen_masterstaf, container, false);

        Button input = (Button) v.findViewById(R.id.inputstaf);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), input_staff.class));
            }
        });
        Button cari = (Button) v.findViewById(R.id.lihat);
        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(getContext(), Cari_staf.class);
                myIntent.putExtra("next_action", "111");
                startActivity(myIntent);
            }
        });
        Button ubah = (Button) v.findViewById(R.id.ubahstaf);
        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), Cari_siswa.class));
            }
        });
        Button hapus = (Button) v.findViewById(R.id.hapusstaf);
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), hapus_siswa.class));
            }
        });

        return v;
    }

}
