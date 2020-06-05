package tech.opsign.kkp.absensi.siswa.tool_presensi;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter_presensi extends RecyclerView.Adapter<Adapter_presensi.MyViewHolder> {
    private List<Model_presensi> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.s_row_presensi, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_presensi b = arrayListData.get(position);
        holder.tgl.setText(b.tanggal);
        holder.stat.setText(b.stat);
        holder.ket.setText(b.ket);
        if (b.ket.equals("")&& b.stat.substring(0, 1).equals("H"))
            holder.rowket.setVisibility(View.GONE);
         else
            holder.rowket.setVisibility(View.VISIBLE);

        switch (b.stat.substring(0, 1)) {

            case "A":
                holder.bg.setBackgroundColor(0xffffcccc);
                break;
            case "I":
                holder.bg.setBackgroundColor(0xfff5fa9e);
                break;
            case "S":
                holder.bg.setBackgroundColor(0xffc0dcf2);
                break;
            case "T":
                holder.bg.setBackgroundColor(0xffbbf7d5);
                break;
            default:
                holder.bg.setBackgroundColor(0xffffffff);
                break;
        }


    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tgl,stat, ket;
        TableRow rowket;
        TableLayout bg;

        MyViewHolder(View v) {
            super(v);
            tgl = (TextView) v.findViewById(R.id.tgl);
            stat = (TextView) v.findViewById(R.id.stat);
            ket = (TextView) v.findViewById(R.id.ket);
            rowket = (TableRow) v.findViewById(R.id.row_ket);
            bg = (TableLayout) v.findViewById(R.id.bgroudnd_lap);

        }

    }

    public Adapter_presensi(List<Model_presensi> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
