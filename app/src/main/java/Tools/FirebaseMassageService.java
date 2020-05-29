package Tools;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import tech.opsign.kkp.absensi.SplashScreen;

public class FirebaseMassageService extends FirebaseMessagingService {
    String TAG = "ER__";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.e(TAG, "token2: " + FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived " );
        remoteMessage.getData();
        sendnotification(remoteMessage);

    }


    private void sendnotification(RemoteMessage remoteMessage) {
//

        try{
            Map<String, String> data = remoteMessage.getData();
            String tittle = data.get("title");
            String content = data.get("content");
            Log.e("ER", String.valueOf(data));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFCATION_CHANEL_ID = "EDMTDev";
//            if(!data.get("nama").equals("")){
            if(data.containsKey("chanel")){
                NOTIFCATION_CHANEL_ID = data.get("chanel");
            }

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

            Intent result =new Intent(this, SplashScreen.class);
            PendingIntent resultpendingIntent = PendingIntent.getActivities(this, 1, new Intent[]{result}, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFCATION_CHANEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setTicker("Hearty365")
                    .setContentTitle(tittle)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(resultpendingIntent)
                    .setContentInfo("info");
            Log.e(TAG, "onMessageReceived5 " );
            notificationManager.notify(0, notificationBuilder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
