package tech.opsign.kkp.absensi.admin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Master.siswa.Cari_siswa;
import tech.opsign.kkp.absensi.admin.Master.siswa.hapus_siswa;
import tech.opsign.kkp.absensi.admin.Master.siswa.input_siswa;

public class master_siswa extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("List Menu Master Siswa");
        v = inflater.inflate(R.layout.a_fragmen_mastersiswa, container, false);

        Button input = (Button) v.findViewById(R.id.inputsiswa);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), input_siswa.class));
            }
        });
        Button cari = (Button) v.findViewById(R.id.carisiswa);
        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(getContext(), Cari_siswa.class);
                myIntent.putExtra("next_action", "111");
                startActivity(myIntent);
            }
        });
        Button ubah = (Button) v.findViewById(R.id.ubahsiswa);
        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), Cari_siswa.class);
                myIntent.putExtra("next_action", "222");
                startActivity(myIntent);
            }
        });
        Button pass = (Button) v.findViewById(R.id.ubahpasssiswa);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), Cari_siswa.class);
                myIntent.putExtra("next_action", "333");
                startActivity(myIntent);
            }
        });
        Button hapus = (Button) v.findViewById(R.id.hapussiswa);
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), hapus_siswa.class));
            }
        });

        return v;
    }

}
