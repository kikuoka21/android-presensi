package tech.opsign.kkp.absensi.siswa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Tools.Utilities;
import Tools.appKey;
import id.ac.budiluhur.sim.R;

public class DashboardFragment extends Fragment {

    private appKey key;
    private View v;
    private SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            getActivity().setTitle(R.string.home);
            v =inflater.inflate(R.layout.halamanutama,container, false);

           key = new appKey();
           sp = v.getContext().getSharedPreferences(key.key(9145), 0x0000);
           getnama();

        return v;
    }

    private void getnama() {
        try{
//            JSONObject obj = new JSONObject(sp.getString(key.key(1102), ""));/**/
            ((TextView)v.findViewById(R.id.textView14)).setText(sp.getString(key.key(1102), ""));
        }catch (Exception e){
            ((TextView)v.findViewById(R.id.textView14)).setText("-");
            Utilities.codeerror(getContext(), "ER0011");
        }
    }


}
