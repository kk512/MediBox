package in.co.medibox.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import in.co.medibox.R;
import in.co.medibox.activity.Activity_Login;
import in.co.medibox.activity.Activity_Register_MedicalShop;
import in.co.medibox.activity.Activity_Register_User;

public class GCMIntentService extends IntentService {

    private static final String TAG = "GCM Tutorial::Service";

    private SharedPreferences mMediPref;

    // Use your PROJECT ID from Google API into SENDER_ID
    public static final String SENDER_ID = "689464982336";

    public GCMIntentService() {
        super(SENDER_ID);
    }


    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "onRegistered: registrationId=" + registrationId);

        Activity_Register_User.regId = registrationId;
        Activity_Register_MedicalShop.regId = registrationId;
        Activity_Login.REGID = registrationId;
    }


    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "onUnregistered: registrationId=" + registrationId);
    }

    protected void onMessage(Context context, Intent data) {
        mMediPref = this.getSharedPreferences("MEDIAPP", MODE_PRIVATE);
        if (!mMediPref.getString("fullName", "").equalsIgnoreCase("")) {
            String message;
            // Message from PHP server
            message = data.getStringExtra("message");
            // Open a new activity called GCMMessageView
            Intent intent = new Intent(this, Activity_Login.class);
            // Pass data to the new activity
            intent.putExtra("message", message);
            // Starts the activity on notification click
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // Create the notification with a notification builder
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Dawai Order")
                    .setContentText(message).setContentIntent(pIntent)
                    .getNotification();
            // Remove the notification on click
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // Play default notification sound
            notification.defaults |= Notification.DEFAULT_SOUND;

            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(R.string.app_name, notification);

            {
                // Wake Android Device when notification received
                PowerManager pm = (PowerManager) context
                        .getSystemService(Context.POWER_SERVICE);
                final PowerManager.WakeLock mWakelock = pm.newWakeLock(
                        PowerManager.FULL_WAKE_LOCK
                                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
                mWakelock.acquire();

                // Timer before putting Android Device to sleep mode.
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() {
                        mWakelock.release();
                    }
                };
                timer.schedule(task, 5000);
            }

        }
    }

    protected void onError(Context arg0, String errorId) {
        Log.e(TAG, "onError: errorId=" + errorId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }
}