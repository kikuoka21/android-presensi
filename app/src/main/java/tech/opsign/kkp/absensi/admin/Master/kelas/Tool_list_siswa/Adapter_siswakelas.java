package tech.opsign.kkp.absensi.admin.Master.kelas.Tool_list_siswa;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter_siswakelas extends RecyclerView.Adapter<Adapter_siswakelas.MyViewHolder> {
    private List<Model_siswakelas> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_row_siswa_level, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_siswakelas b = arrayListData.get(position);
        holder.nis.setText(b.nis);
        holder.nama.setText(b.nama);
        if (b.level.equals("1")) {
            holder.bg.setBackgroundColor(0xffe68a00);
            holder.pengurus.setVisibility(View.VISIBLE);
        } else{

            holder.bg.setBackgroundColor(0xff3Fbf00);
            holder.pengurus.setVisibility(View.GONE);
        }


    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nis, nama, pengurus;
        TableLayout bg;

        MyViewHolder(View v) {
            super(v);
            nis = (TextView) v.findViewById(R.id.row_nis);
            nama = (TextView) v.findViewById(R.id.row_nama);
            pengurus = (TextView) v.findViewById(R.id.pngrus_kelas);
            bg = (TableLayout) v.findViewById(R.id.latar);

        }

    }

    public Adapter_siswakelas(List<Model_siswakelas> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
