package macbeth.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etMajor;
    private TextView tvTimestamp;

    private ArrayAdapter<User> recordListAdapter;
    private DatabaseReference databaseRef;

    private List<User> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get fields for later use
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etMajor = findViewById(R.id.et_major);
        tvTimestamp = findViewById(R.id.tv_timestamp);

        // Setup List View
        records = new ArrayList<>();
        ListView lvRecords = findViewById(R.id.lv_records);
        recordListAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, records);
        lvRecords.setAdapter(recordListAdapter);

        // Setup Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();
        registerFirebaseRecordListener2(); // Used to read records from /records/*
        registerFirebaseTimestampListener(); // Used to read timestamp from /timestamp

        // Register Button Click
        Button bAddToFirebase = findViewById(R.id.b_add_to_firebase);
        bAddToFirebase.setOnClickListener((view)-> {
            addRecord();
            updateTimeStamp();
        });
    }

    /**
     * Demonstrate the ability to write an object to a list in Firebase
     */
    private void addRecord() {
        // Create a new unique key for the record
        String key = databaseRef.child("records").push().getKey();

        // Create a new User record and populate from the user interface
        User user = new User(etFirstName.getText().toString(),
                             etLastName.getText().toString(),
                             etMajor.getText().toString());

        // Write the data to the new key
        databaseRef.child("records").child(key).setValue(user);
        Log.d("FirebaseDemo", "Written record to Firebase key = " + key);
    }

    /**
     * Demonstrate the ability to write something to a single field (not a list) in Firebase
     */
    private void updateTimeStamp() {
        Date currentTime = Calendar.getInstance().getTime();

        // Set the timestamp field in Firebase as a String
        databaseRef.child("timestamp").setValue(currentTime.toString());
        Log.d("FirebaseDemo", "Wrote updated timestamp to Firebase: "+currentTime);
    }

    /**
     * Demonstrate the ability to read records from a list in Firebase
     */
    private void registerFirebaseRecordListener() {
        // The ChildEventListener is used to read from a list in Firebase.
        // This will automatically read all records (via onChildAdded) and then
        // the appropriate function when something changes.  Note that when the addRecord
        // function is called above it will trigger Firebase to call onChildAdded below after the
        // database is updated.
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Convert to a User Class and add to ArrayList
                records.add(snapshot.getValue(User.class));

                // Update the List View
                recordListAdapter.notifyDataSetChanged();

                Log.d("FirebaseDemo", "Received record from Firebase key = " + snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // We could change the value in our records list here
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // We could remove the value in our records list here
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // Apply listener to the /records/* list in firebase
        databaseRef.child("records").addChildEventListener(childEventListener);
    }

    /**
     * Demonstrate an alternative way of reading the records from the list.  Intead of reading
     * one record at a time from the firebase list, we will read all of the them.  This is the same
     * approach as registerFirebaseTimestampListener().
     */
    private void registerFirebaseRecordListener2() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Loop through of each of the elements of the list (children) and convert
                // to User objects.  Since we are receiving all of them, we need to clear our array first.
                records.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    records.add(child.getValue(User.class));
                    Log.d("FirebaseDemo", "Received record from Firebase key = " + child.getKey());
                }

                // Update the List View
                recordListAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // Apply listener to the /records/* list in firebase
        databaseRef.child("records").addValueEventListener(valueEventListener);
    }

    /**
     * Demonstrate the ability to read a single field from firebase.
     */
    private void registerFirebaseTimestampListener() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Convert to a string
                String timestamp = snapshot.getValue(String.class);

                // Update the view (its possible the first time app is used that
                // the timestamp won't exist in Firebase yet so we will receive a null
                if (timestamp != null) {
                    tvTimestamp.setText("Last Updated On: " + timestamp);
                }

                Log.d("FirebaseDemo","Received Updated Timestamp from Firebase: "+timestamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // Apply listener to /timestamp in firebase
        databaseRef.child("timestamp").addValueEventListener(valueEventListener);
    }


}
