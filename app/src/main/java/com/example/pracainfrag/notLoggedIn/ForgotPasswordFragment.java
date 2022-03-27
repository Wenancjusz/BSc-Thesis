package com.example.pracainfrag.notLoggedIn;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pracainfrag.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    EditText emailET;
    Button remindme;

    public ForgotPasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance){

        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        emailET = view.findViewById(R.id.remindPasswordEmail);
        remindme = view.findViewById(R.id.remindPasswordBTN);

        Context context = getActivity().getApplicationContext();

        remindme.setOnClickListener(v->{
            String email = emailET.getText().toString();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Wysłano email z nowym hasłem!", Toast.LENGTH_SHORT).show();
                                Log.d("Password reset email", "Email sent.");
                            }
                            else{
                                Toast.makeText(context, "Wystąpił błąd!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        });

        return view;
    }
}