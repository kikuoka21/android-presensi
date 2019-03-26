//      ____  __________   __  ______  __
//     / __ \/_  __/  _/  / / / / __ )/ /
//    / / / / / /  / /   / / / / __  / /
//   / /_/ / / / _/ /   / /_/ / /_/ / /___
//  /_____/ /_/ /___/   \____/_____/_____/
//
// Copyright © 2018 - Fathalfath30.
// Created On 9/2/2018
// Email : fathalfath30@gmail.com
// Follow me on GithHub : https://github.com/Fathalfath30
//
// For the brave souls who get this far: You are the chosen ones,
// the valiant knights of programming who toil away, without rest,
// fixing our most awful code. To you, true saviors, kings of men,
//
// I say this: never gonna give you up, never gonna let you down,
// never gonna run around and desert you. Never gonna make you cry,
// never gonna say goodbye. Never gonna tell a lie and hurt you.
//

package Tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.net.InetAddress;

public class Utilities {
    public static void codeerror(final Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Informasi")
                .setCancelable(false)
                .setMessage("Telah terjadi kesalahan. (code:" + message + ")")
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ((Activity) context).finish();
            }
        })
                .show();


    }

    /**
     * Memeriksa koneksi apakah hp android terkoneksi dengan internet atau tidak
     * dan juga apakah dapat mengakses server budi luhur.
     *
     * @param context context
     * @return boolean status koneksi
     */
    public boolean inetOnline(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = connManager.getActiveNetworkInfo();

            // check pertama untuk memeriksa koneksi internet
            connected = ((netInfo != null) && netInfo.isAvailable() && netInfo.isConnected());

            if (connected) {
                // melakukan double check ke server budi luhur
                InetAddress ipAddr = InetAddress.getByName("budiluhur.ac.id");
                connected = !ipAddr.equals("");
            }
        } catch (Exception ex) {
//            Log.v("inetOnline", ex.getMessage() == null ? "Unknown error" : ex.getMessage());
        }
        return connected;
    }

    /**
     * Menampilkan pesan dialog yang sudah di setting secara default
     * akan menampilkan tombol "tutup" tanpa menutup aplikasi hanya
     * menutup pesan dialog
     *
     * @param context activity context
     * @param title   Judul pesan
     * @param message pesan yang akan di tampilkan
     */
    public static void showMessageBox(Context context, String title, String message) {
        try {
            AlertDialog alert = new AlertDialog.Builder(context).create();
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setCancelable(false);
            alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Tutup",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alert.show();
        }catch (Exception e){
//            Log.e("ER_", String.valueOf(e));
        }
    }





    public static String imei(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            @SuppressLint("HardwareIds")
            String imei = telephonyManager.getDeviceId();
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            //Log.e("ER_UTIL_IMEI", e.getMessage(), e);
        }
        return "";
    }
    public static int rto(){
        return 50000;
    }
}
