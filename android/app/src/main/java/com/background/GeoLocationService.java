package com.background;

import android.app.Service;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Build;
import android.os.IBinder;
import android.annotation.TargetApi;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.graphics.BitmapFactory;
import android.app.PendingIntent;
import android.support.annotation.Nullable;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;

public class GeoLocationService extends Service {
  public static final String FOREGROUND = "com.background.location.FOREGROUND";
  private static int GEOLOCATION_NOTIFICATION_ID = 12345689;
  LocationManager locationManager = null;
  LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      GeoLocationService.this.sendMessage(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
  };

  @Override
  @TargetApi(Build.VERSION_CODES.M)
  public void onCreate() {
    locationManager = getSystemService(LocationManager.class);

    int permissionCheck = ContextCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_FINE_LOCATION);
    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 100, locationListener);
    }
  }

  private void sendMessage(Location location) {
    try {
      Intent intent = new Intent("GeoLocationUpdate");
      intent.putExtra("message", location);
      LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDestroy() {
    locationManager.removeUpdates(locationListener);
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    startForeground(GEOLOCATION_NOTIFICATION_ID, getCompatNotification());
    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private Notification getCompatNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    String str = "Is using your location in the background";
    builder
      //.setSmallIcon(R.drawable.ic_gps_icon)
      .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
      .setContentTitle("App Name")
      .setContentText(str)
      .setTicker(str)
      .setWhen(System.currentTimeMillis());
    Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
    PendingIntent contentIntent = PendingIntent.getActivity(this, 1000, startIntent, 0);
    builder.setContentIntent(contentIntent);
    return builder.build();
  }
}
