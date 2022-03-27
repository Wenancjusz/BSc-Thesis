package com.example.pracainfrag.notLoggedIn;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pracainfrag.R;
import com.example.pracainfrag.DoStuffActivity;

public class welcomeFragment extends Fragment {

    Button login,register;

    public welcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        login = view.findViewById(R.id.loginBTN);
        register = view.findViewById(R.id.registerButton);

        login.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.welcomeFragmentContainer, new LoginFragment()).addToBackStack(null).commit();
        });

        register.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.welcomeFragmentContainer, new RegisterFragment()).addToBackStack(null).commit();
        });


        return view;
    }
}