package tech.opsign.kkp.absensi.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Tools.GenKey;
import Tools.Utilities;
import tech.opsign.kkp.absensi.R;

public class DashboardFragment extends Fragment {

    private GenKey key;
    private View v;
    private SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            getActivity().setTitle(R.string.home);
            v =inflater.inflate(R.layout.halamanutama,container, false);

           key = new GenKey();
           sp = v.getContext().getSharedPreferences(key.key(9145), 0x0000);
           getnama();

        return v;
    }

    private void getnama() {
        try{
//            JSONObject obj = new JSONObject(sp.getString(GenKey.GenKey(1102), ""));/**/
            ((TextView)v.findViewById(R.id.textView14)).setText(sp.getString(key.key(1102), ""));
        }catch (Exception e){
            ((TextView)v.findViewById(R.id.textView14)).setText("-");
            Utilities.codeerror(getContext(), "ER0011");
        }
    }


}
