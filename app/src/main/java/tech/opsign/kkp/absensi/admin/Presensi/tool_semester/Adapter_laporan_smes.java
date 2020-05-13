package tech.opsign.kkp.absensi.admin.Presensi.tool_semester;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter_laporan_smes extends RecyclerView.Adapter<Adapter_laporan_smes.MyViewHolder> {
    private List<Model_laporan_smes> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_row_lap_smes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_laporan_smes b = arrayListData.get(position);
        holder.nis.setText("("+b.nis+") "+b.nama);
        holder.hadir.setText(b.hadir);
        holder.alpha.setText(b.alpha);
        holder.sakit.setText(b.sakit);
        holder.izin.setText(b.izin);

        if (position % 2 == 1) {
            holder.bg.setBackgroundColor(0xff49b7e3);
//            holder.bg.setBackgroundColor(0xffE8526D);
        } else
//            holder.bg.setBackgroundColor(0xffC15CF5);
            holder.bg.setBackgroundColor(0xff51e44a);

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nis, hadir, alpha, sakit, izin;
        TableLayout bg;

        MyViewHolder(View v) {
            super(v);
            nis = (TextView) v.findViewById(R.id.row_nis);
            hadir = (TextView) v.findViewById(R.id.chadir);
            alpha = (TextView) v.findViewById(R.id.calpha);
            sakit = (TextView) v.findViewById(R.id.csakit);
            izin = (TextView) v.findViewById(R.id.cizin);
            bg = (TableLayout) v.findViewById(R.id.latar);

        }

    }

    public Adapter_laporan_smes(List<Model_laporan_smes> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
