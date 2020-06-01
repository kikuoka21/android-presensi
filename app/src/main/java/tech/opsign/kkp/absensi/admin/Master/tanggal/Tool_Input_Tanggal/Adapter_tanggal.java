package tech.opsign.kkp.absensi.admin.Master.tanggal.Tool_Input_Tanggal;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import tech.opsign.kkp.absensi.R;

public class Adapter_tanggal extends RecyclerView.Adapter<Adapter_tanggal.MyViewHolder> {
    private List<Model_tanggal> arrayListData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_row_hari_libur, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_tanggal b = arrayListData.get(position);
        holder.tanggal.setText(b.tanggal);
        holder.ket.setText(b.keterangan);
//        #97CE68
        if (position % 2 == 1) {
//            holder.bg.setBackgroundColor(0xffe68a00);
            holder.bg.setBackgroundColor(0xffcc7a00);
//            holder.bg.setBackgroundColor(0xffE8526D);
        } else
//            holder.bg.setBackgroundColor(0xffC15CF5);
            holder.bg.setBackgroundColor(0xff3B7E87);

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tanggal, ket;
        TableLayout bg;

        MyViewHolder(View v) {
            super(v);
            tanggal = v.findViewById(R.id.tanggal);
            ket = v.findViewById(R.id.keterangan);
            bg = v.findViewById(R.id.latar);

        }

    }

    public Adapter_tanggal(List<Model_tanggal> arrayListData) {
        this.arrayListData = arrayListData;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
