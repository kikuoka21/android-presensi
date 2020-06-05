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

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.s_row_presensi, parent, false);
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
            case "H":
                holder.bg.setBackgroundColor(0xff4dff4d);
                break;
            case "L":
                holder.bg.setBackgroundColor(0xffeeeeee);
                break;
            case "A":
                holder.bg.setBackgroundColor(0xffff8080);
                break;
            case "I":
                holder.bg.setBackgroundColor(0xffe9f53d);
                break;
            case "S":
                holder.bg.setBackgroundColor(0xff81b9e4);
                break;
            default:
                holder.bg.setBackgroundColor(0xffcccccc);
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

    public Adapter_presensi_parent(List<Model_presensi_parent> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
