package mx.com.cesarcorona.directorio.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;

import mx.com.cesarcorona.directorio.R;


/**
 * Created by Gerardo on 14/2/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {// c98WgE5WjvU:APA91bFnzz50a1rtJjtYuRkS8P430FS0h6vFIq9vAEdCUr4nVYyE3PzF0rE2SdoDjVOMKC9S5wcCbhtPs56l1GozbSU8qg3oLfqytD5pE9R_n1NFyx_C-kGMNuK86JNRCrn9kF1WC9AE

    private static final String TAG = "MyFirebaseMsgService";
    Bitmap bitmap;
    String imageUri = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //imageUri will contain URL of the image to be displayed with Notification
        imageUri = remoteMessage.getData().get("image");

        //To get a Bitmap image from the URL received
        bitmap = getBitmapfromUrl(imageUri);
//        sendNotificationImage(remoteMessage.getNotification(), bitmap, remoteMessage.getData());
        sendNotification(remoteMessage.getNotification(), remoteMessage.getData());
        if (bitmap == null){
            sendNotification(remoteMessage.getNotification(), remoteMessage.getData());
        }else {
            sendNotificationImage(remoteMessage.getNotification(), bitmap, remoteMessage.getData());
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     *
     */
    private void sendNotification(RemoteMessage.Notification notification,Map<String, String> data) {
//        Intent intent = new Intent(this, LogInActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        int requestID = (int) System.currentTimeMillis();

        String channelId = "fcm_default_channel";

        String intentType = data.get("tipo");
        Intent notificationIntent;
        PendingIntent contentIntent;
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String titulo = "";
        String mensaje = "";

        NotificationCompat.Builder notificationBuilder = null;
        if(notification != null){

            titulo = notification.getTitle();
            mensaje = notification.getBody();
                    notificationBuilder =  new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    // .setContentIntent(contentIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }



            //notificationIntent  = new Intent(getApplicationContext(), InvitationsActivity.class);
            //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //contentIntent = PendingIntent.getActivity(this, requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);




        if(data != null){
             titulo = data.get("titulo");
            mensaje = data.get("mensaje");
            imageUri = data.get("imagen");
            if(imageUri== null){
                notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(titulo)
                                .setContentText(mensaje)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            }else{
                bitmap = getBitmapfromUrl(imageUri);
                notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setLargeIcon(bitmap)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(titulo)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                .setContentText(mensaje)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            }



        }







        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotificationImage(RemoteMessage.Notification notification, Bitmap image , Map<String, String> data){
        String channelId = "fcm_default_channel";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(image)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification.getTitle())
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                        .setContentText(notification.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

}
