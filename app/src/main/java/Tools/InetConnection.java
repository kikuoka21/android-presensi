package Tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

public class InetConnection {
    public boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = connManager.getActiveNetworkInfo();

            // check pertama untuk memeriksa koneksi internet
            connected = ((netInfo != null) && netInfo.isAvailable() && netInfo.isConnected());

            if (connected) {
                InetAddress ipAddr = InetAddress.getByName("google.co.id");
                connected = !ipAddr.equals("");
            }
        } catch (Exception ex) {
//            Log.v("inetOnline", ex.getMessage() == null ? "Unknow error" : ex.getMessage());
        }
//        return connected;
        return true;
    }
}
