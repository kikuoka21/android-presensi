package tech.opsign.kkp.absensi.admin.Fragment.ToolDashboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    private List<Model> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fragmen_admin, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model b = arrayListData.get(position);
        holder.nis.setText(b.nis);
        holder.nama.setText(b.nama);
        holder.kelas.setText(b.kelas);
        holder.alasan.setText(b.alasan);


    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nis, nama, kelas, alasan, mulai, selsai, lama_waktu;

        MyViewHolder(View v) {
            super(v);
            nis = (TextView) v.findViewById(R.id.nis);
            nama = (TextView) v.findViewById(R.id.namarow);
            kelas = (TextView) v.findViewById(R.id.kelas);
            alasan = (TextView) v.findViewById(R.id.alasan);

        }

    }

    public Adapter(List<Model> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
