package com.example.honahlappchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.honahlappchat.Adapter.UserAdapter;
import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityUserAcivityBinding;
import com.example.honahlappchat.models.UsersM;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class userAcivity extends AppCompatActivity {

    private ActivityUserAcivityBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        binding = ActivityUserAcivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        loadUser();
        setListener();
        statusColor();

    }

    private void statusColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.MainStatus,this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.MainStatus,this.getTheme()));
        }
    }


    private void setListener(){
        binding.ImageBack.setOnClickListener(v -> onBackPressed());
    }

    //ham de load cac user ve
    private void loadUser(){
        Loading(true);

        // ket noi voi realtime database cau firebasefirestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // mo colletion tren fire base
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                   Loading(false);
                   // phan nay cho snapshot chua hieu ro
                    // currentId
                   String currentId = preferenceManager.getString(Constants.KEY_USER_ID);
                   if (task.isSuccessful() && task.getResult() != null){
                       List<UsersM> users = new ArrayList<>();
                       //lap de lay thong tin users ve nhung khong hieu ro cac dung cua task va query
                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                           if (currentId.equals(queryDocumentSnapshot.getId())){
                               continue;
                           }
                           UsersM usersM = new UsersM();
                           usersM.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                           usersM.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                           usersM.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                           usersM.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                           users.add(usersM);

                       }
                       if (users.size() > 0){
                           UserAdapter userAdapter = new UserAdapter(users);
                           binding.usersRecycleView.setAdapter(userAdapter);
                           binding.usersRecycleView.setVisibility(View.VISIBLE);
                       }else {
                           showErrorMess();
                       }
                   }else {
                       showErrorMess();
                   }
                });

    }

    private void showErrorMess(){
        binding.TextErrorMess.setText(String.format("%s","No user available"));
        binding.TextErrorMess.setVisibility(View.VISIBLE);
    }


    //ProgessBar
    private void Loading(Boolean beloading){
        if (beloading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}