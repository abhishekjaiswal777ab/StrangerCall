package com.example.strangercall.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.strangercall.R;
import com.example.strangercall.databinding.ActivityMainBinding;
import com.example.strangercall.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    long coins=0;
    String[] permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
    private int requestCode=1;
    User use;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        database.getReference().child("profiles")
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         use= snapshot.getValue(User.class);
                        coins = use.getCoins();

                        binding.coins.setText("You have: " + coins);
                        Glide.with(MainActivity.this)
                                .load(use.getProfile())
                                .into(binding.profilePicture);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionsGranted())
                {
                if (coins > 5) {
                    Intent intent=new Intent(MainActivity.this,ConnectingActivity.class);
                    intent.putExtra("profile",use.getProfile());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this,"Insufficient Coins",Toast.LENGTH_SHORT).show();
                }
            }
                else
                {
                    askPermissions();
                }
            }
        });
    }
        void askPermissions(){
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }

        private boolean isPermissionsGranted() {
            for(String permission : permissions ){
                if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }

            return true;
        }


    }
