package awalk.app.smartvalvetest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {

    private static final String ADDVALVEDIALOG = "addValveDialog";

    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private ProgressDialog mProgressDialog;
    public Context context;
    private FirebaseRecyclerAdapter mAdapter;
    private Utils utils = new Utils();
    private DatabaseReference databaseRef,valveRef,mRef;
    private ValueEventListener valveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        context = this;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser!=null) {
            mUserId = mFirebaseUser.getUid();
            showProgressDialog("Fetching Data");
        }
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserTest").child(mUserId);
        valveRef = databaseRef.child("ValvesTest");
        RecyclerView recycler = (RecyclerView) findViewById(R.id.ValvesRecyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, 2);
            }
        });
        mAdapter = new FirebaseRecyclerAdapter<Valve, ValveViewHolder>(Valve.class, R.layout.valve_list_layout, ValveViewHolder.class, mRef) {
            @Override
            public void populateViewHolder(final ValveViewHolder valveViewHolder, final Valve valveData, final int position) {
                //hideProgressDialog();
                valveViewHolder.setName(valveData.getValveName());
                valveViewHolder.setStatus(utils.decodeStatus(valveData.getValveStatus()));
                valveViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("test",getRef(position).getKey());
                        Intent intent = new Intent(context,ValveActivity.class);
                        intent.putExtra("key",getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        recycler.setAdapter(mAdapter);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                if (!dataSnapshot.hasChildren())
                    Log.d("test","empty");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                Log.d("test","error");
                if(databaseError.getCode()==DatabaseError.NETWORK_ERROR){
                    notifyUser("Not connected to Internet");
                }
            }
        });
        FloatingActionButton addValButton = (FloatingActionButton) findViewById(R.id.addValButton);
        addValButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddValveAlertDialog dialog = new AddValveAlertDialog();
                dialog.show(fragmentManager, ADDVALVEDIALOG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyUser("Signing out!");
                                startActivity(new Intent(UserActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public void notifyUser(String message){
        Snackbar.make(findViewById(R.id.UserActivity),message,Snackbar.LENGTH_SHORT).show();
    }

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }
}
