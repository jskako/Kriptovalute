package com.example.cryptowallet;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance(); //Get Firebase auth instance;

    public LoginFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    private void OnClick(final TextView mail, final TextView password, final View rootView) {

        Button login = rootView.findViewById(R.id.btn_login);
        Button resendMail = rootView.findViewById(R.id.resendMail);
        Button forgetPassword = rootView.findViewById(R.id.forgetPassword);
        Switch switchLanguage = rootView.findViewById(R.id.language);
        resendMail.setVisibility(View.INVISIBLE);
        checkSwitch(switchLanguage);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Mail = mail.getText().toString().trim();
                String Password = password.getText().toString().trim();
                closeKeyboard(getActivity());

                if (Mail.equals(null) || Mail.equals("") || Password.equals(null) || Password.equals("")) {
                    if (Password.equals(null) || Password.equals("")){
                        showToast("Password can't be blank!");
                    }

                    if (Mail.equals(null) || Mail.equals("")){
                        showToast("Mail can't be blank!");
                    }
                } else {
                    if(Mail.contains("@") && Mail.contains(".")){
                        Database(Mail, Password, resendMail);
                    }else{
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

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Mail = mail.getText().toString().trim();
                closeKeyboard(getActivity());

                if (Mail.equals(null) || Mail.equals("")) {
                    if (Mail.equals(null) || Mail.equals("")){
                        showToast("Mail can't be blank!");
                    }
                } else {
                    if(Mail.contains("@") && Mail.contains(".")){
                        auth.sendPasswordResetEmail(Mail.trim());
                        showToast("Recovery mail has been sent.");
                        disableButton(forgetPassword);
                    }else{
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

        switchLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setLanguage("de");
                    switchLanguage.setText("Deutsch");
                    showToast("Dein Set Deutsch für Deine Sprache.");
                } else {
                    setLanguage("");
                    switchLanguage.setText("English");
                    showToast("Your set English for Your language.");
                }
            }
        });
    }

    private void checkSwitch(Switch switchLanguage){
        if(switchLanguage.getText().toString().trim().equals("Deutsch")){
            switchLanguage.setChecked(true);
        }
    }

    private void disableButton(Button dis){
        dis.setEnabled(false);
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dis.setEnabled(true);
                    }
                });
            }
        }, 30000);
    }

    private void Database(String mail, String password, Button resendMail){
        auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SWES", "signInWithEmail:success");
                            String username = cutUser(mail.trim());
                            FirebaseUser user = auth.getCurrentUser();
                            if(!user.isEmailVerified()) {
                                showToast("Please verify your email address.");
                                resendMail.setVisibility(View.VISIBLE);
                                resendMail(resendMail);

                            }else{
                                showToast("Welcome "+username.trim());
                                Intent i = new Intent(getActivity(), UserProfile.class);
                                startActivity(i);
                                ((Activity) getActivity()).overridePendingTransition(0, 0);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            showToast("Authentication failed.");
                            Log.w("SWEF", "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    private void resendMail(Button resendMail){
        resendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                user.sendEmailVerification();
                showToast("An email has been sent. Please check your SPAM box.");
                resendMail.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String cutUser(String mail){
        List<String> list = new ArrayList<String>(Arrays.asList(mail.split("@")));
        return list.get(0);
    }

    private void showToast(String msg){
        Toast.makeText(getActivity(),msg.trim(),Toast.LENGTH_SHORT).show();
    }

    public void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setLanguage(String language){
        Locale myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView mail = view.findViewById(R.id.et_email);
        final TextView password = view.findViewById(R.id.et_password);

        OnClick(mail, password, view);
    }
}
