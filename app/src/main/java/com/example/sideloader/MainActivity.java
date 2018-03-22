package com.example.sideloader;


import android.os.Bundle;

//copied from git
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.text.MessageFormat;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
    }

    public void goToPayPalHereLauncher(View view) {
        Intent intent = new Intent(this, PayPalHereLauncher.class);
        startActivity(intent);
    }
}
