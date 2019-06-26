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
import tech.opsign.kkp.absensi.admin.Master.siswa.Cari_siswa;
import tech.opsign.kkp.absensi.admin.Master.tanggal.input_tanggal;

public class master_tanggal extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("List Menu Master Tanggal");
        v = inflater.inflate(R.layout.a_fragmen_mastertanggal, container, false);

        Button input = (Button) v.findViewById(R.id.input_tanggal);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(getContext(), input_tanggal.class);
                myIntent.putExtra("next_action", "111");
                startActivity(myIntent);
            }
        });
//        Button cari = (Button) v.findViewById(R.id.lihat_tanggal);
//        cari.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(getContext(), Cari_siswa.class));
//            }
//        });
//        Button ubah = (Button) v.findViewById(R.id.ubah_tanggal);
//        ubah.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent myIntent = new Intent(getContext(), input_tanggal.class);
//                myIntent.putExtra("next_action", "222");
//                startActivity(myIntent);
//            }
//        });
        Button hapus = (Button) v.findViewById(R.id.hapus_tanggal);
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), input_tanggal.class);
                myIntent.putExtra("next_action", "333");
                startActivity(myIntent);
            }
        });

        return v;
    }

}
