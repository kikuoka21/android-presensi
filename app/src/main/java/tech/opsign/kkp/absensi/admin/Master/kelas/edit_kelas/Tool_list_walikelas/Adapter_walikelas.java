package tech.opsign.kkp.absensi.admin.Master.kelas.edit_kelas.Tool_list_walikelas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter_walikelas extends RecyclerView.Adapter<Adapter_walikelas.MyViewHolder> {
    private List<Model_walikelas> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_row_nip_nama, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_walikelas b = arrayListData.get(position);
        holder.nis.setText(b.nip);
        holder.nama.setText(b.nama);
        if (position%2==1)
            holder.bg.setBackgroundColor(0xffe68a00);
         else
            holder.bg.setBackgroundColor(0xff3Fbf00);



    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nis, nama;
        TableLayout bg;

        MyViewHolder(View v) {
            super(v);
            nis = (TextView) v.findViewById(R.id.row_nip);
            nama = (TextView) v.findViewById(R.id.row_nama);
            bg = (TableLayout) v.findViewById(R.id.latar);

        }

    }

    public Adapter_walikelas(List<Model_walikelas> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
