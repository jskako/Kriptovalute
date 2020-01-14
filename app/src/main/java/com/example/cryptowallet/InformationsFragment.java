package com.example.cryptowallet;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class InformationsFragment extends Fragment {


    public InformationsFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_informations, container, false);
        return view;
    }

    private void OnClick(final View rootView, TextView info) {
        setText(info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Thank You for supporting me :).");
            }
        });
    }

    private void setText(TextView info){
        info.setText("FROM: gaming.ingrs@gmail.com\nTO: anyonewithgreatheart@heart.com\nSUBJECT: Something sweet about me\n\nHey You,\n\nI am not company.\nI am just regular user who love coding.\nAt least I think I love it. But I love to eat for sure.\nAnyway, I am trying to create most user-friendly and secure wallet.\nIf You don't believe me, and You shouldn't, software is open-source and You can find it on GitHub.\nPlease note that I am not responsible for anything, use it on Your own free will.\nI, and community, will try to fix any bug that You report me on mail:\n\ngaming.ingrs@gmail.com\n\nThank You for Your time and may the software be with You.\n\nYou are always welcome to tip me: https://paypal.me/cryptowallet");
    }

    private void showToast(String msg){
        Toast.makeText(getActivity(),msg.trim(),Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView info = view.findViewById(R.id.informations);
        OnClick(view, info);
    }

}
