package com.example.cryptowallet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance(); //Get Firebase auth instance;

    public RegisterFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    private void OnClick(final TextView displayName, final TextView mail, final TextView password, final TextView passwordR,final View rootView) {

        Button login = rootView.findViewById(R.id.btn_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DisplayName = displayName.getText().toString().trim();
                String Mail = mail.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String PasswordR = passwordR.getText().toString().trim();
                closeKeyboard(getActivity());
                boolean passCheck = true;

                if (DisplayName.equals(null) || DisplayName.equals("") || Mail.equals(null) || Mail.equals("") || Password.equals(null) || Password.equals("") || PasswordR.equals(null) || PasswordR.equals("")) {

                    if (PasswordR.equals(null) || PasswordR.equals("")){
                        showToast("Repeated password can't be blank!");
                    }
                    if (Password.equals(null) || Password.equals("")){
                        showToast("Password can't be blank!");
                    }
                    if (Mail.equals(null) || Mail.equals("")){
                        showToast("Mail can't be blank!");
                    }
                    if (DisplayName.equals(null) || DisplayName.equals("")){
                        showToast("Display Name can't be blank!");
                    }
                } else {
                    passCheck = passwordCheck(Password);
                    if(Mail.contains("@") && Mail.contains(".")&& Password.equals(PasswordR) && passCheck){
                        Database(Mail, Password);
                    }else{
                        if(!Password.equals(PasswordR)){
                            showToast("Passwords are not equal!");
                        }
                        if(!Mail.contains(".")){
                            showToast("You are missing . character at mail!");
                        }
                        if(!Mail.contains("@")){
                            showToast("You are missing @ character at mail!");
                        }
                    }
                }
            }
        });
    }

    private boolean passwordCheck(String password){

        if(password.trim().length()<8){//Check string len
            showToast("Password cannot be less than 8 characters");
            return false;
        }

        char ch; //Check if string has one capital and lowercase letter
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean digitFlag = false;
        for(int i=0;i < password.length();i++) {
            ch = password.charAt(i);
            if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            } else if (Character.isDigit(ch)){
                digitFlag = true;
            }
        }
        if(!capitalFlag){
            showToast("Password has to have at least one capital letter ");
            return false;
        }
        if(!lowerCaseFlag){
            showToast("Password has to have at least one lower case letter ");
            return false;
        }
        if(!digitFlag){
            showToast("Password has to have at least one digit");
            return false;
        }
        return true;
    }

    private void Database(String mail, String password){
        auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            insertUserData(user);
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

    private void ChangeFrag(){
        // Create new fragment and transaction
        LoginFragment newFragment = new LoginFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.swipeRight, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void insertUserData(FirebaseUser user){
        final TextView displayName = view.findViewById(R.id.et_name);
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
        newUser.put("displayName", displayName.getText().toString().trim());
        db.collection("users").document(uid).set(newUser);
    }

    private void showToast(String msg){
        Toast.makeText(getActivity(),msg.trim(),Toast.LENGTH_SHORT).show();
    }

    public void closeKeyboard(Activity activity) {
        view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView displayName = view.findViewById(R.id.et_name);
        final TextView mail = view.findViewById(R.id.et_email);
        final TextView password = view.findViewById(R.id.et_password);
        final TextView passwordR = view.findViewById(R.id.et_repassword);

        OnClick(displayName, mail,password, passwordR, view);
    }

}
