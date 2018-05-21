package mxi.com.styleswiperbusiness.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import mxi.com.styleswiperbusiness.Activities.Splash;
import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.R;

public class GCMNotificationIntentService extends IntentService {


    static int i = 0;

    CommanClass cc;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            cc = new CommanClass(getApplicationContext());
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.e("String extras: ", "Data : " + extras.toString());

                if (!extras.getString("gcm.notification.title").equals("")) {
                    //  sendNotification(extras.getString("gcm.notification.title"), extras.getString("gcm.notification.message"));
                } else {
                }
                Log.e("####", extras.getString("gcm.notification.message"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

//    private void sendNotification(String title, String message) {
//
//
//        NotificationManager notificationManager = (NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent notificationIntent;
//        notificationIntent = new Intent(this, Splash.class);
//        notificationIntent.putExtra("title", title);
//        notificationIntent.putExtra("message", message);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent intent =
//                PendingIntent.getActivity(this, i, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder builder = new Notification.Builder(GCMNotificationIntentService.this);
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setContentInfo("FOQOH")
//                .setSubText(message)
//                .setAutoCancel(true)
//                .setContentIntent(intent);
//
//        Notification notification = builder.getNotification();
//
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        notification.defaults |= Notification.DEFAULT_LIGHTS;
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        notificationManager.notify(R.mipmap.ic_launcher, notification);
//
//    }


}
