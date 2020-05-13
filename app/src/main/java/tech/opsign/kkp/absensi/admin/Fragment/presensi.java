package tech.opsign.kkp.absensi.admin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tech.opsign.kkp.absensi.R;
import tech.opsign.kkp.absensi.admin.Presensi.Carikelas_tanggal;
import tech.opsign.kkp.absensi.admin.Presensi.cari_kelas_smester;

public class presensi extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("List Menu Laporan");
        v = inflater.inflate(R.layout.a_fragmen_presensi, container, false);

        Button perhari = (Button) v.findViewById(R.id.perhari);
        perhari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), Carikelas_tanggal.class);
                myIntent.putExtra("next_action", "111");
                myIntent.putExtra("ubah", "n");
                startActivity(myIntent);
            }
        });
        Button perbulan = (Button) v.findViewById(R.id.perbulan);
        perbulan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), Carikelas_tanggal.class);
                myIntent.putExtra("next_action", "222");
                startActivity(myIntent);
            }
        });
        Button persmester = (Button) v.findViewById(R.id.persmester);
        persmester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), cari_kelas_smester.class));

            }
        });

        return v;
    }

}
