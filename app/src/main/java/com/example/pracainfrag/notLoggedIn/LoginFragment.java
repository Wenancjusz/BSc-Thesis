package com.example.pracainfrag.notLoggedIn;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pracainfrag.R;
import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.account.AccountModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class LoginFragment extends Fragment {

    EditText email,password;
    Button signIn, forgotPassword;
    TextView register, loginTV;
    View view;

    private FirebaseAuth.AuthStateListener mAuth;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        loginTV = view.findViewById(R.id.loginTV);
        email = view.findViewById(R.id.emailTV);
        password = view.findViewById(R.id.passwordET);
        signIn = view.findViewById(R.id.signInBTN);
        forgotPassword = view.findViewById(R.id.fPasswordBTN);
        register = view.findViewById(R.id.loginTextView);

        onConfigurationChanged(getActivity().getResources().getConfiguration());

        setupFirebaseAuth();

        Bundle extras = getArguments();

        if(extras != null) {
            String emailPassed, passwordPassed;
            emailPassed = extras.getString("email");
            passwordPassed = extras.getString("password");
            email.setText(emailPassed);
            password.setText(passwordPassed);
        }

        signIn.setOnClickListener(v->{
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
            if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    signIn();
            }
            else {
                Toast.makeText(getContext(), "Uzupełnij dane!", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPassword.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.welcomeFragmentContainer, new ForgotPasswordFragment()).addToBackStack("login").commit();

        });

        register.setOnClickListener(v -> {
            if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>1)
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            else
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.welcomeFragmentContainer, new RegisterFragment()).addToBackStack("login").commit();
        });

        return view;
    }

    private void setupFirebaseAuth(){
        mAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    DocumentReference userRef = db.collection(getString(R.string.collection_users))
                            .document(user.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                AccountModel user = task.getResult().toObject(AccountModel.class);
                            }
                        }
                    });


                }
            }
        };
    }


    public void signIn()
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getContext(), "Zalogowano!", Toast.LENGTH_SHORT).show();
                        Intent startActivity;
                        startActivity = new Intent(getActivity(), DoStuffActivity.class);
                        startActivity.putExtra("mode","account");
                        startActivity(startActivity);

                }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getContext(), "Niepoprawny e-mail lub hasło!", Toast.LENGTH_SHORT).show();
                        }
                });
    }
}