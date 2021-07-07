package macbeth.firebasedemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class AppShutdownService extends Service {

    public AppShutdownService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {  // This is called when the app shuts down
        super.onTaskRemoved(rootIntent);
        Log.d("FirebaseDemo", "onTaskRemoved");
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Date currentTime = Calendar.getInstance().getTime();

        // Set the timestamp field in Firebase as a String
        databaseRef.child("timestamp").setValue(currentTime.toString());
        Log.d("FirebaseDemo", "Setting time to "+currentTime);
        stopSelf(); // App is gone so we might as well go too...
    }

}