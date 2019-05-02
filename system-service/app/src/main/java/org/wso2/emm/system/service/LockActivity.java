package org.wso2.emm.system.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.verifone.utilities.Log;

import org.wso2.emm.system.service.utils.Constants;

/**
 * This activity is used to lock the device.
 * LockActivty only works if agent is registered as the device owner.
 *
 */
public class LockActivity extends Activity {

    private static final String TAG = LockActivity.class.getSimpleName();
    private TextView adminMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);
        adminMessage = (TextView) findViewById(R.id.admin_message);
        Bundle extras = getIntent().getExtras();

        boolean isLocked = extras.getBoolean(Constants.IS_LOCKED);
        if (isLocked) {
            enablePinnedActivity();
            if (extras.getString(Constants.ADMIN_MESSAGE) != null && !extras.getString(Constants.ADMIN_MESSAGE).isEmpty()) {
                adminMessage.setText(extras.getString(Constants.ADMIN_MESSAGE));
            }
        } else {
            disablePinnedActivity();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enablePinnedActivity() {
        startLockTask();
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.i(TAG, "Hard lock is enabled");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void disablePinnedActivity() {
        adminMessage.setText(getResources().getString(R.string.txt_unlock_activity));
        stopLockTask();
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.i(TAG, "Hard lock is disabled");
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Device is locked", Toast.LENGTH_LONG).show();
    }

}
