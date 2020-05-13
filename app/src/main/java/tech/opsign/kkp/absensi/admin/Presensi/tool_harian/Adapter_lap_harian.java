package tech.opsign.kkp.absensi.admin.Presensi.tool_harian;

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

public class Adapter_lap_harian extends RecyclerView.Adapter<Adapter_lap_harian.MyViewHolder> {
    private List<Model_lap_harian> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_row_lap_harian, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_lap_harian b = arrayListData.get(position);
        holder.nis.setText(b.nis);
        holder.nama.setText(b.nama);
        holder.stat.setText(b.stat);
        holder.ket.setText(b.ket);
        if (b.ket.equals(""))
            holder.rowket.setVisibility(View.GONE);
         else
            holder.rowket.setVisibility(View.VISIBLE);

        switch (b.stat.substring(0, 1)) {
            case "H":
                holder.bg.setBackgroundColor(0xff4dff4d);
                break;
            case "L":
                holder.bg.setBackgroundColor(0xff4dff4d);
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
        TextView nis, nama, stat, ket;
        TableRow rowket;
        TableLayout bg;

        MyViewHolder(View v) {
            super(v);
            nis = (TextView) v.findViewById(R.id.nis);
            nama = (TextView) v.findViewById(R.id.nama);
            stat = (TextView) v.findViewById(R.id.stat);
            ket = (TextView) v.findViewById(R.id.ket);
            rowket = (TableRow) v.findViewById(R.id.row_ket);
            bg = (TableLayout) v.findViewById(R.id.bgroudnd_lap);

        }

    }

    public Adapter_lap_harian(List<Model_lap_harian> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
