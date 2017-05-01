package awalk.app.smartvalvetest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * Created by diksha on 1/5/17.
 */

public class AddValveAlertDialog extends DialogFragment {

    private String userID;
    private String userName;

    private String valveID;
    private String valveName;

    private final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser;

    private void setUpViews(View dialogLayout){
        ((EditText)dialogLayout.findViewById(R.id.valveNameEditText))
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        valveName = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

        ((EditText)dialogLayout.findViewById(R.id.valveIDEditText))
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        valveID = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser !=null)
            userID = firebaseUser.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialogLayout = layoutInflater.inflate(R.layout.activity_add_dialog, null);
        builder.setMessage("Enter your Valve details");
        builder.setView(dialogLayout)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Valve valve = new Valve();

                        valve.setValveName(valveName);
                        valve.setValveState(0);
                        valve.setValveStatus(Utils.STATUS.CLOSED.ordinal());

                        Map valveValues = valve.toMap();

                        Map childUpdates = new HashMap<>();
                        childUpdates.put("/ValvesTest/" + valveID, valveValues);
                        childUpdates.put("/UserTest/" + userID + "/" + valveID, valveValues);

                        firebase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null)
                                    Log.d("test",databaseError.getMessage());
                            }
                        });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        setUpViews(dialogLayout);

        return builder.create();
    }
}
