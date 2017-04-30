package awalk.app.smartvalvetest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/* valve status encoding
    Opening,0
    Opened,1
    Closing,2
    Closed,3
 */

public class ValveActivity extends AppCompatActivity {
    private DatabaseReference databaseRef, valveRef, userValveRef;
    private String key;
    private TextView valveNameText, valveStatusText, valveToggleText;
    private SwitchCompat valveToggle;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private ValueEventListener valveListener;
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valve);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        valveNameText = (TextView) findViewById(R.id.valvenametext);
        valveStatusText = (TextView) findViewById(R.id.valvestatustext);
        valveToggleText = (TextView) findViewById(R.id.valveToggleText);
        valveToggle = (SwitchCompat) findViewById(R.id.valveToggle);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            mUserId = mFirebaseUser.getUid();
            //showProgressDialog("Fetching Data");
        }
        key = getIntent().getStringExtra("key");
        databaseRef = FirebaseDatabase.getInstance().getReference();
        valveRef = databaseRef.child("ValvesTest").child(key);
        userValveRef = databaseRef.child("UserTest").child(mUserId).child(key);
        valveListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Valve valveData = dataSnapshot.getValue(Valve.class);
                if (valveData != null)
                    UpdateUI(valveData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        valveRef.addValueEventListener(valveListener);

        valveToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempstate = valveToggle.isChecked() ? 1 : 0;
                editValveStatus(tempstate);
                valveToggle.setEnabled(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        valveRef.addValueEventListener(valveListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        valveRef.removeEventListener(valveListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.valve_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.val_del:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //Setting message manually and performing action on button click
                builder.setMessage("This action cant be un-done.")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteValve(databaseRef);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Delete this Valve?");
                alert.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateUI(Valve valve) {
        valveNameText.setText(valve.getValveName());
        int tempStatus = valve.getValveStatus();
        String tempStatusString = utils.decodeStatus(tempStatus);
        if (!tempStatusString.equals(valveStatusText.getText())) {
            valveStatusText.setText(utils.decodeStatus(tempStatus));
        /*valveToggleText.setText(tempStatusString);*/
            switch (tempStatus) {
                case 0:
                    valveToggle.setEnabled(false);
                    break;
                case 1:
                    valveToggleText.setText("Close Valve");
                    valveToggle.setEnabled(true);
                    break;
                case 2:
                    valveToggle.setEnabled(false);
                    break;
                case 3:
                    valveToggleText.setText("Open Valve");
                    valveToggle.setEnabled(true);
                    break;
                case 4:
                    valveToggle.setEnabled(false);
                case 5:
                    valveToggle.setEnabled(false);
            }
        }
    }

    private void deleteValve(DatabaseReference mDatabase) {
        Map valveValues = null;
        Map childUpdates = new HashMap<>();
        childUpdates.put("/ValvesTest/" + key, valveValues);
        childUpdates.put("/UserTest/" + mUserId + "/" + key, valveValues);

        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null)
                    Log.d("test", databaseError.getMessage());
            }
        });
    }

    private void editValveStatus(int state) {
        valveRef.child("valveState").setValue(state);
        userValveRef.child("valveState").setValue(state);
    }
}
