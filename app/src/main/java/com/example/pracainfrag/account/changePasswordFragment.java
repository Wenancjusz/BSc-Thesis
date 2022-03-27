package com.example.pracainfrag.account;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.R;
import com.example.pracainfrag.notLoggedIn.RegisterFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePasswordFragment extends Fragment {

    public changePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button changePassword,cancel;

        EditText actualPasswordET, newPasswordET,confirmNewPasswordET;

        changePassword = view.findViewById(R.id.changePasswordBTN);
        cancel = view.findViewById(R.id.cancelBTN);

        actualPasswordET = view.findViewById(R.id.actualPasswordET);
        newPasswordET = view.findViewById(R.id.newPasswordET);
        confirmNewPasswordET = view.findViewById(R.id.editTextConfirmPassword);

        DoStuffActivity doStuffActivity = (DoStuffActivity) (getActivity());
        doStuffActivity.toolbarTitle.setText("Zmiana hasła");

        ActionBar actionBar = doStuffActivity.getSupportActionBar();

        changePassword.setOnClickListener(v->{
            if(!actualPasswordET.getText().toString().isEmpty()) {
                    RegisterFragment registerFragment = new RegisterFragment(getContext()); // pozwala na wykorzystanie tej samej metody co przy rejestracji
                    if(!newPasswordET.getText().toString().isEmpty() && !confirmNewPasswordET.getText().toString().isEmpty()) {
                        if (registerFragment.isPasswordVerified(newPasswordET.getText().toString(), confirmNewPasswordET.getText().toString())) {
                            AccountFragment accountFragment = new AccountFragment();
                            accountFragment.reAuthUser(actualPasswordET.getText().toString());

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(newPasswordET.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Password Update", "new pswd = " + newPasswordET.getText().toString());
                                            }
                                        }
                                    });
                            Toast.makeText(getContext(), "Hasło zostało zmienione!", Toast.LENGTH_SHORT).show();

                            actionBar.setDisplayHomeAsUpEnabled(true);
                            actionBar.setHomeButtonEnabled(true);
                            ((DoStuffActivity)requireActivity()).drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, new AccountFragment()).commit();

                            //schowanie klawiatury
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                    else
                        Toast.makeText(getContext(), "Podaj nowe hasło!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Podaj aktualne hasło!", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v->{
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            ((DoStuffActivity)requireActivity()).drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AccountFragment()).commit();
        });


        return view;
    }

}