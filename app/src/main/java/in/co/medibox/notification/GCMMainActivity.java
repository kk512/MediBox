package in.co.medibox.notification;

import android.app.Activity;
import android.os.Bundle;

import in.co.medibox.R;

public class GCMMainActivity extends Activity {
    String TAG = "GCM Tutorial::Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // GCMRegistrar.checkDevice(this);
        //GCMRegistrar.checkManifest(this);

        // Register Device Button
        /*Button regbtn = (Button) findViewById(R.id.register);

		regbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Registering device");
				// Retrive the sender ID from GCMIntentService.java
				// Sender ID will be registered into GCMRegistrar
				GCMRegistrar.register(GCMMainActivity.this,
						GCMIntentService.SENDER_ID);
			}
		});*/
    }
}