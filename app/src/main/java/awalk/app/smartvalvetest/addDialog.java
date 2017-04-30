package awalk.app.smartvalvetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class addDialog extends AppCompatActivity {

    private Button create,cancel;
    private EditText name,uid;
    private View.OnClickListener clickcatcher;
    private String mUserId;
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dialog);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser!=null)
            mUserId = mFirebaseUser.getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        create = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.notok);
        name = (EditText) findViewById(R.id.nameEditText);
        uid = (EditText) findViewById(R.id.uidEditText);
        clickcatcher = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ok:
                        //mDatabase.child("ValvesTest").push().setValue(new Valve(name.getText().toString(),false));
                        addNewValve(mDatabase, uid.getText().toString(),name.getText().toString(),0,3);
                        break;
                }
                finish();
            }
        };
        create.setOnClickListener(clickcatcher);
        cancel.setOnClickListener(clickcatcher);
    }

    private void addNewValve(DatabaseReference mDatabase,String uid, String name, int state, int status) {
        String key = uid;
        Valve valve = new Valve(name,state,status);
        Map valveValues = valve.toMap();


        Map childUpdates = new HashMap<>();
        childUpdates.put("/ValvesTest/" + key, valveValues);
        childUpdates.put("/UserTest/" + mUserId + "/" + key, valveValues);

        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError!=null)
                    Log.d("test",databaseError.getMessage());
            }
        });
    }
}
