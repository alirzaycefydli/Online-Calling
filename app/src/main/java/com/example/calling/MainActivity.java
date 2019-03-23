package com.example.calling;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.calling.Adapters.AllUserAdapters;
import com.example.calling.Models.User;
import com.example.calling.Start.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CallListener {

    //widgets
    private RecyclerView recyclerView;

    //vars
    private FirebaseAuth mAuth;
    private FirebaseUser current_user;
    private DatabaseReference userRef;

    private SinchClient sinchClient;
    private Call call;
    private List<User> userList;
    private AllUserAdapters adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userList = new ArrayList<>();

        recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(current_user.getUid())
                .applicationKey("")
                .applicationSecret("")
                .environmentHost("")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, final Call incomingCall) {
                AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Calling");
                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        call.hangup();
                    }
                });
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Pick", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        call=incomingCall;
                        call.answer();
                        call.addCallListener(new CallListener() {
                            @Override
                            public void onCallProgressing(Call call) {

                            }

                            @Override
                            public void onCallEstablished(Call call) {

                            }

                            @Override
                            public void onCallEnded(Call endedCall) {
                                call = null;
                                endedCall.hangup();
                            }

                            @Override
                            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

                            }
                        });
                    }
                });
                alert.show();
            }
        });

        sinchClient.start();

        fetchAllUsers();
    }

    public void callUsers(User user){

        if (call == null){
            call=sinchClient.getCallClient().callUser(user.getUser_id());
            call.addCallListener(this);

            openCallerDialog(call);
        }
    }

    private void openCallerDialog(final Call callDialog) {

        AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
        alert.setTitle("Alert");
        alert.setMessage("Calling");
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Hang up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callDialog.hangup();
            }
        });

        alert.show();
    }

    private void fetchAllUsers() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }

                adapter = new AllUserAdapters(MainActivity.this,userList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (current_user == null) {
            Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onCallProgressing(Call call) {

    }

    @Override
    public void onCallEstablished(Call establishedCall) {
        Toast.makeText(this, "Call is established", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCallEnded(Call endedCall) {
        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
        call = null;
        endedCall.hangup();
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {

    }
}
