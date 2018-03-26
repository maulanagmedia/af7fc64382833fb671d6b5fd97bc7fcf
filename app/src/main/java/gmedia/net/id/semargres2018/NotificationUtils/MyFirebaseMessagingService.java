package gmedia.net.id.semargres2018.NotificationUtils;

/**
 * Created by Shin on 2/17/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.maulana.custommodul.ItemValidation;

import java.util.HashMap;
import java.util.Map;

import gmedia.net.id.semargres2018.DetailPromoActivity;
import gmedia.net.id.semargres2018.MainActivity;
import gmedia.net.id.semargres2018.MenuMyQR.NavMyQR;
import gmedia.net.id.semargres2018.R;


/**
 * Created by Shin on 2/13/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = "MyFirebaseMessaging";
    private ItemValidation iv = new ItemValidation();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getFrom());

        Map<String, String> extra = new HashMap<>();
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
            extra = remoteMessage.getData();
        }

        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification());
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(), new HashMap<String, String>(extra));
        }

    }

    private void sendNotification(String title, String body , HashMap<String, String> extra) {

        // need no change
        Intent intent = new Intent(this, MainActivity.class);
        int typeContent = 0;
        for(String key: extra.keySet()){
            if(key.trim().toUpperCase().equals("JENIS")){
                if(extra.get(key).trim().toUpperCase().equals("1")){
                    typeContent = 1;
                }else if(extra.get(key).trim().toUpperCase().equals("2")){
                    typeContent = 2;
                } else if(extra.get(key).trim().toUpperCase().equals("3")){
                    typeContent = 3;
                }
            }
        }

        if(typeContent != 5){
            switch (typeContent){
                case 1:
                    intent = new Intent(this, MainActivity.class);
                    break;
                case 2:
                    intent = new Intent(this, DetailPromoActivity.class);
                    break;
                case 3:
                    intent = new Intent(this, MainActivity.class);
                    break;
                default:
                    intent = new Intent(this, MainActivity.class);
                    break;
            }

            intent.putExtra("backto", true);
            String message = "", jml = "";
            for(String key: extra.keySet()){
                intent.putExtra(key, extra.get(key));
                if(typeContent == 3 && key.equals("message")) message = extra.get(key);
                if(typeContent == 3 && key.equals("jumlah")) jml = extra.get(key);
            }

            if(typeContent == 3){
                NavMyQR.showDialog(message, jml);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /*request code*/, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            int IconColor = getResources().getColor(R.color.color_notif);

            // Set Notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notif)
                    .setColor(IconColor)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /*Id of Notification*/, notificationBuilder.build());
        }
        /*else{

            try {
                if(iv.isServiceRunning(this, LocationUpdater.class)){
                    stopService(new Intent(this, LocationUpdater.class));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                if(!iv.isServiceRunning(this, LocationUpdater.class)){
                    startService(new Intent(this, LocationUpdater.class));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/

    }
}
