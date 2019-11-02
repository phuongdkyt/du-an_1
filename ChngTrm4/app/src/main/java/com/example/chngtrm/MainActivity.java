package com.example.chngtrm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView t1, t2,t3,t4,t5;
    private Toolbar toolbar;
    private ToggleButton toggleButton;
    private MediaPlayer mediaPlayer;
    private static  final  int REQUEST_CODE=1000;
    private static final int REQUEST_CODE_EXAMPLE = 0x9345;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String mLastUpdateDay;
    private String dulieusdt,dulieusdtnow;
     private static int d=0;
    private Boolean checksensor=false;
    private final  String API_KEY="AIzaSyA9Z90IURAnMIvJEBEHF70bhh7oCAFv11Y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestPermission();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
        t1 = findViewById(R.id.hienthi);
        t2 = findViewById(R.id.vitri);
        t2.setOnClickListener(this);
        t3= findViewById(R.id.time1);
        t4= findViewById(R.id.ketnoimang);
        t5= findViewById(R.id.hienthisdt);
        toggleButton=findViewById(R.id.battat);
        toggleButton.setOnCheckedChangeListener(this);
        toolbar=findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        readFromInternal();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Cảm biến không tồn tại!", Toast.LENGTH_SHORT).show();
        }
        mLastUpdateDay = DateFormat.getDateTimeInstance().format(new Date());
        t3.setText(mLastUpdateDay);
        if(isOnline()) t4.setText("Trạng thái mạng: ON");
        else  t4.setText("Trạng thái mạng: OFF");
        if(dulieusdtnow==null) t5.setText("Bạn chưa nhập SĐT,vui lòng nhập SĐT");
        else t5.setText("SĐT:"+dulieusdtnow);
}
    @Override
    protected void onStart() {
        super.onStart();

    }
    public  void buildLocationCallback()
    {

       locationCallback = new LocationCallback() {

    @Override
    public  void onLocationResult(LocationResult locationResult) {
        for(Location location:locationResult.getLocations())
        {
            t2.setText("("+location.getLatitude()+"."+location.getLongitude()+")");
        }
      }
};
    }

   @SuppressLint("RestrictedApi")
   public void buildLocationRequest()
   {
       locationRequest=new LocationRequest();
       locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       locationRequest.setInterval(5000);
       locationRequest.setFastestInterval(3000);
       locationRequest.setSmallestDisplacement(10);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Float giatri = event.values[0];
        if(checksensor==true) {
            if (giatri == 0) {
                d++;
                t1.setText("Bị phát hiện");
                if (d <= 10) {
                    if (d == 1) Canhbaomp3();
                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        Guisms(
                                                "https://www.google.com/maps/place/" + location.getLatitude() + "," + location.getLongitude() + "/" + "  SOS!");
                                    } else Guisms2();
                                }

                            });
                }
            } else {
               t1.setText("Không phát hiện");
                // Log.d("Gía trị=",giatri+"");
            }
       }
    }
    private void Canhbaomp3() {
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.canhbaomp3);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
    private void Guisms(String dulieu) {
        try {
            String dl=String.valueOf(dulieu);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(dulieusdtnow, null,dl, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), dulieusdtnow+"Không gửi được tin nhắn"+dulieu, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private void Guisms2() {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(dulieusdtnow, null,"Cảnh báo mất máy", null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Không gửi được tin nhắn", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }
   private void RequestPermission()
    {
        if(checkPermission(Manifest.permission.SEND_SMS)||checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)||checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
        }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION
                           },
                    REQUEST_CODE);
        }
    }
    public boolean isOnline() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mLastUpdateDay = DateFormat.getDateTimeInstance().format(new Date());
        t3.setText(mLastUpdateDay);
        if(isOnline()) t4.setText("Trạng thái mạng: ON");
        else  t4.setText("Trạng thái mạng: OFF");
        buildLocationRequest();
        buildLocationCallback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.my_menu,menu);
      return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    int id=item.getItemId();
        switch (id)
        {
           case  R.id.itsdt:
            Intent i=new Intent(MainActivity.this,Themsdt_Activity.class);
            i.putExtra("sdt",dulieusdtnow);
            startActivityForResult(i,REQUEST_CODE_EXAMPLE);
            break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_EXAMPLE) {
            if(resultCode == Activity.RESULT_OK) {

                String result = data.getStringExtra("key1");
                dulieusdt=result;
                dulieusdtnow=dulieusdt;
                t5.setText("SĐT:"+dulieusdt);
                writeInternal();

            } else {
            }
        }
    }
    public void writeInternal() {
        if(dulieusdt!=null) {
            try {
                OutputStream os = openFileOutput("chuthich_in.txt", MODE_PRIVATE);
                String string = dulieusdt;
                os.write(string.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void readFromInternal(){
        try {
            InputStream is = openFileInput("chuthich_in.txt");
            int size = is.available();
            byte data[] = new byte[size];
            is.read(data);
            is.close();
            String s = new String(data); //s chứa dữ liệu đọc từ file
            dulieusdtnow=s;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            checksensor = true;
            t1.setText("Không phát hiện");
        } else {
            checksensor = false;
            t1.setText("Đã tắt cảm biến");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vitri:
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/" + location.getLatitude()
                                    + "," + location.getLongitude() + "/"));
                            startActivity(intent);
                        }


                });
                break;
        }
    }
}
