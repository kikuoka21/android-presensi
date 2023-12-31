package tech.opsign.kkp.absensi.Parent.tool_presensi_parent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter_presensi_parent extends RecyclerView.Adapter<Adapter_presensi_parent.MyViewHolder> {
    private List<Model_presensi_parent> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.p_row_presensi, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_presensi_parent b = arrayListData.get(position);
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
            tgl = v.findViewById(R.id.tgl);
            stat = v.findViewById(R.id.stat);
            ket = v.findViewById(R.id.ket);
            rowket = v.findViewById(R.id.row_ket);
            bg = v.findViewById(R.id.bgroudnd_lap);

        }

    }

    public Adapter_presensi_parent(List<Model_presensi_parent> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
