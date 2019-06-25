

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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.EntityReference;

import java.net.InetAddress;
import java.security.spec.ECField;
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
                return "berubah";
            }
        } catch (Exception e) {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Permission Request")
                    .setMessage("Wajib Memberikan Akses")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
            return "gagal";
        }
    }


    public static int rto() {
        return 6000;
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
            return "tidak diketahui";
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
            String balikan =  bulan(kedua.format(c.getTime())) + " - ";

            kedua = new SimpleDateFormat("yyyy");
            balikan = balikan + kedua.format(c.getTime());

            return balikan;

        } catch (Exception e) {
            return "tidak diketahui";
        }
    }

    public static String status_kehadiran(String stat) {
        switch (stat.substring(0,1)) {
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
        switch (hari.substring(0,3)) {
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
