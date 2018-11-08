package com.finalproject.child;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.child.Class.Child;
import com.finalproject.child.Class.DeviceData;
import com.finalproject.child.Class.StatService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button qrScanner, startMonitor;
    private EditText name, age;
    private TextView codeView;
    ProgressDialog progressDialog;
    Calendar calendar;

    Context context;
    Intent serviceIntent;
    private StatService statService;

    private Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrScanner = findViewById(R.id.qr_scanner_btn);
        startMonitor = findViewById(R.id.start_moitor_btn);
        name = findViewById(R.id.child_name_et);
        age = findViewById(R.id.age_et);
        codeView = findViewById(R.id.link_code_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Linking Device....");
        statService = new StatService(getContext());

        if (!hasPermission()){
            Log.i("Error Message","User may not allow the access");
            Toast.makeText(this,"Give Permission Please", Toast.LENGTH_LONG).show();
           final AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setView(R.layout.alert_dialog);
           builder.setTitle("Permission");
           builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
               }
           });
           builder.show();
        }




        qrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        startMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMyServiceRunning(statService.getClass())) {
                    progressDialog.show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    Child child = new Child(name.getText().toString(), Build.MODEL, getDateTime(), age.getText().toString(), DeviceData.getDeviceId());

                    String key = reference.child("Device").child(DeviceData.getUserId()).push().getKey();
                    reference.child("Device").child(DeviceData.getUserId()).child(key).setValue(child);

                    DeviceData.key = codeView.getText().toString();
                    if (!TextUtils.isEmpty(DeviceData.key)) {
                        serviceIntent = new Intent(getContext(), statService.getClass());
                        serviceIntent.putExtra("user", DeviceData.getUserId());
                        serviceIntent.putExtra("device", DeviceData.getDeviceId());
                        startService(serviceIntent);
                    }

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Log.e("Scan Result", "Cancelled Scan");
            } else {
                Log.e("Scan Status", "Scanned");
                codeView.setText(result.getContents());

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    public String getDateTime() {
        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(calendar.getTime());

    }

    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

}
