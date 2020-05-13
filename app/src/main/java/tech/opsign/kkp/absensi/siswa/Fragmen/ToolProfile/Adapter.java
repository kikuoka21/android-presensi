package tech.opsign.kkp.absensi.siswa.Fragmen.ToolProfile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    private List<Model> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.s_row_kelas, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model b = arrayListData.get(position);
        holder.nama.setText(b.nama);
        holder.kelas.setText(b.kelas);
        if (position % 2 == 1) {
            holder.bg.setBackgroundColor(0xffE8526D);
        } else
            holder.bg.setBackgroundColor(0xffC15CF5);

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama, kelas;
        TableLayout bg;

        MyViewHolder(View v) {
            super(v);
            nama = (TextView) v.findViewById(R.id.namarow);
            kelas = (TextView) v.findViewById(R.id.kelas);
            bg = (TableLayout) v.findViewById(R.id.latar);

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
