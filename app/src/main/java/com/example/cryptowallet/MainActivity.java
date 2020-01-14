package com.example.cryptowallet;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static MainActivity ins;
    private FirebaseAuth auth = FirebaseAuth.getInstance(); //Get Firebase auth instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewPager);
        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new LoginFragment());
        pagerAdapter.addFragmet(new RegisterFragment());
        pagerAdapter.addFragmet(new InformationsFragment());
        viewPager.setAdapter(pagerAdapter);
    }

    class AuthenticationPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentList = new ArrayList<>();

        public AuthenticationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragmet(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }


    public static MainActivity  getInstace(){
        return ins;
    }

    public void updateTheTextView(String fullName, String mail, String password) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Database(fullName, mail, password);
            }
        });
    }

    //Unos usera s vanjskog servisa
    private void Database(String fullName, String mail, String password){
        auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            insertUserData(user, fullName);
                            Log.d("CreateUser", "createUserWithEmail:success");
                            user.sendEmailVerification();
                            showToast("User successfully created, please verify your email address.");
                            //insertUserData();
                            //ChangeFrag();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CreateUser", "createUserWithEmail:failure", task.getException());
                            showToast("Authentication failed.");
                        }
                    }
                });
    }

    private void insertUserData(FirebaseUser user, String fullName){
        // Add a new document with a generated ID
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", "");
        newUser.put("surname", "");
        newUser.put("address", "");
        newUser.put("city", "");
        newUser.put("zip", "");
        newUser.put("phoneNumber", "");
        newUser.put("photoUrl", "");
        newUser.put("displayName", fullName.trim());
        db.collection("users").document(uid).set(newUser);
    }

    private void showToast(String msg){
        Toast.makeText(this,msg.trim(),Toast.LENGTH_SHORT).show();
    }
}
