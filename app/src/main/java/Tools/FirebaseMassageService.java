package Tools;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import tech.opsign.kkp.absensi.R;

public class FirebaseMassageService extends FirebaseMessagingService {
    String TAG = "ER__";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
//        Log.e(TAG, "token2: " + FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived " );
        if (remoteMessage.getData() != null) {
            sendnotification(remoteMessage);

        }

    }

    private void sendnotification(RemoteMessage remoteMessage) {
//

        try{
            Map<String, String> data = remoteMessage.getData();
            String tittle = data.get("title");
            Log.e(TAG, "onMessageReceived 2" );
            String content = data.get("content");
            Log.e("ER", String.valueOf(data));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFCATION_CHANEL_ID = "EDMTDev";
            Log.e(TAG, "onMessageReceived3 " );

            Log.e(TAG, "onMessageReceived " +(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                @SuppressLint("WrongConstant")
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFCATION_CHANEL_ID, "EDMT Notification", NotificationManager.IMPORTANCE_MAX);
                notificationChannel.setDescription("ED<TDev chanel for app test FCM");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);

            }
            Log.e(TAG, "onMessageReceived4 " );
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFCATION_CHANEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setTicker("Hearty365")
                    .setContentTitle(tittle)
                    .setContentText(content)
                    .setContentInfo("info");
            Log.e(TAG, "onMessageReceived5 " );
            notificationManager.notify(0, notificationBuilder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e(TAG, "onMessageReceived6 " );
    }
}
