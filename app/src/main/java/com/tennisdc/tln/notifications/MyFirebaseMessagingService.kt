package com.tennisdc.tln.notifications


import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tennisdc.tln.Constants
import com.tennisdc.tln.R
import com.tennisdc.tln.SingleFragmentActivity
import com.tennisdc.tln.common.Prefs


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val notificationManager: NotificationManager? = null
    lateinit var mPref : Prefs.AppData

    override fun onCreate() {
        super.onCreate()
        mPref = Prefs.AppData(this)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed groups: " + token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        mPref.deviceToken = token
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data.get("text"))
            sendNotification(remoteMessage.data.get("text"))
        }

        /*  if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
*/

        /* if (remoteMessage.getData().size() > 0 || remoteMessage.getNotification() != null) {
            Log.e("MESSAGE", remoteMessage.getData().get("push_type"));
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getData().get("push_type"), remoteMessage.getData().get("userid"));
        }*/
    }

    private fun sendNotification(message: String?) {

        mPref.notificationCount = mPref.notificationCount + 1
        val intent = Intent(Constants.UPDATE_NOTIFICATION_COUNT)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)


//
        val intentMain = Intent(this, SingleFragmentActivity::class.java)
        intentMain.putExtra("notification", true)
        var pendingIntent = PendingIntent.getActivity(this, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT, intentMain, 0)
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intentMain, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
        } else {
            PendingIntent.getActivity(this, 0, intentMain, PendingIntent.FLAG_ONE_SHOT)
        }

        var defaultSoundUri: Uri? = null
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        /** */
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = getString(R.string.app_name)
        val notificationBuilder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val mChannel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(mChannel)
            notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentTitle(resources.getString(R.string.app_name))
                    .setContentText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setSound(defaultSoundUri)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT)))
                    .setContentIntent(pendingIntent).setOngoing(false).setVibrate(longArrayOf(300, 300))
        } else {
            notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setContentTitle(resources.getString(R.string.app_name))
                    .setContentText(Html.fromHtml(message))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setChannelId(channelId)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(message)))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent).setOngoing(false).setVibrate(longArrayOf(300, 300))
        }

        val notificationID = System.currentTimeMillis().toInt()
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private val TAG = "MyFirebaseMsgService"
        val NOTIFICATION = "notification"
        private val NOTIFICATION_ID_KEY = "NOTIFICATION_ID"
        private val CALL_SID_KEY = "CALL_SID"
        private val VOICE_CHANNEL = "default"
    }
}