

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

import androidx.core.app.ActivityCompat;

import android.os.Build;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
//                ((Activity) context).finish();
            }
        })
                .show();


    }


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
        } catch (Exception e) {
//            Log.e("ER_", String.valueOf(e));
        }
    }


    public static String imei(final Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.CAMERA
                };
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);
            }
            @SuppressLint("HardwareIds")
            String imei = telephonyManager.getDeviceId();
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return imei2();
            }
        } catch (Exception e) {

            return "gak ke gnerate imei";
        }
    }

    private static String imei2() {
        return Build.VERSION.SDK_INT + "_" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
    }


    public static int rto() {
        return 10000;
    }

    public static boolean checkjson(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String gettanggal(String tanggal) {
        try {
            DateFormat kedua, df = new SimpleDateFormat("yyyy-MM-dd");


            Date date = df.parse(tanggal);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            kedua = new SimpleDateFormat("EEEE");
//           Log.e("ER_asdj,awd", df.format(c.getTime()));
            String balikan = hari_ini(kedua.format(c.getTime())) + ", ";
            kedua = new SimpleDateFormat("dd");
            balikan = balikan + kedua.format(c.getTime()) + " ";
            kedua = new SimpleDateFormat("MMMM");
            balikan = balikan + bulan(kedua.format(c.getTime())) + " ";

            kedua = new SimpleDateFormat("yyyy");
            balikan = balikan + kedua.format(c.getTime());

            return balikan;

        } catch (Exception e) {
            return "tidak diketahui";
        }
    }

    public static String ubahan_thn_ajrn(String a) {
        return a + "/" + (Integer.parseInt(a) + 1);
    }

    public static String gettgl_lahir(String tanggal) {
        try {
            DateFormat kedua, df = new SimpleDateFormat("yyyy-MM-dd");


            Date date = df.parse(tanggal);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
//           Log.e("ER_asdj,awd", df.format(c.getTime()));
            kedua = new SimpleDateFormat("dd");
            String balikan = kedua.format(c.getTime()) + " ";
            kedua = new SimpleDateFormat("MMMM");
            balikan = balikan + bulan(kedua.format(c.getTime())) + " ";

            kedua = new SimpleDateFormat("yyyy");
            balikan = balikan + kedua.format(c.getTime());

            return balikan;

        } catch (Exception e) {
            return tanggal;
        }
    }

    public static String tgl_bulan(String tanggal) {
        try {
            DateFormat kedua, df = new SimpleDateFormat("yyyy-MM-dd");


            Date date = df.parse(tanggal);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
//           Log.e("ER_asdj,awd", df.format(c.getTime()));
            kedua = new SimpleDateFormat("dd");
            String balikan = kedua.format(c.getTime()) + " ";
            kedua = new SimpleDateFormat("MMMM");
            balikan = balikan + bulan(kedua.format(c.getTime()));

            return balikan;

        } catch (Exception e) {
            return "tidak diketahui";
        }
    }

    public static String bln_thn(String tanggal) {
        try {
            DateFormat kedua, df = new SimpleDateFormat("yyyy-MM");


            Date date = df.parse(tanggal);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
//           Log.e("ER_asdj,awd", df.format(c.getTime()));
            kedua = new SimpleDateFormat("MMMM");
            String balikan = bulan(kedua.format(c.getTime())) + " - ";

            kedua = new SimpleDateFormat("yyyy");
            balikan = balikan + kedua.format(c.getTime());

            return balikan;

        } catch (Exception e) {
            return "tidak diketahui";
        }
    }

    public static String status_kehadiran(String stat) {


            switch (stat.substring(0, 1)) {
                case "L":
                    return "Libur";

                case "A":
                    return "Alpha";

                case "I":
                    return "Izin";

                case "S":
                    return "Sakit";

                case "H":
                    return "Hadir";

                case "T":
                    return "Telat";

                default:
                    return stat;
            }
    }


    private static String hari_ini(String hari) {
        switch (hari.substring(0, 3)) {
            case "Sun":
                return "Minggu";

            case "Mon":
                return "Senin";

            case "Tue":
                return "Selasa";

            case "Wed":
                return "Rabu";

            case "Thu":
                return "Kamis";

            case "Fri":
                return "Jumat";

            case "Sat":
                return "Sabtu";

            default:
                return hari;
        }
    }

    private static String bulan(String bulan) {
        switch (bulan.substring(0, 3)) {
            case "Jan":
                return "Januari";

            case "Feb":
                return "Februari";

            case "Mar":
                return "Maret";

            case "Apr":
                return "April";

            case "May":
                return "Mei";

            case "Jun":
                return "Juni";

            case "Jul":
                return "Juli";

            case "Aug":
                return "Agustus";

            case "Sep":
                return "September";

            case "Oct":
                return "Oktober";

            case "Nov":
                return "November";

            case "Des":
                return "Desember";

            default:
                return bulan;
        }
    }


}
