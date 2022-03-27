package com.example.pracainfrag.account;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditAccountDataFragment extends Fragment {

    AccountModel accountModel;

    public EditAccountDataFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_account_data, container, false);

        Button cancel,save;

        EditText loginET, nameET, emailET, phoneET;

        DoStuffActivity doStuffActivity = (DoStuffActivity) (getActivity());

        doStuffActivity.toolbarTitle.setText("Edytuj dane konta");

        cancel = view.findViewById(R.id.cancelBTN);
        save = view.findViewById(R.id.saveBTN);

        loginET = view.findViewById(R.id.loginET);
        nameET = view.findViewById(R.id.nameET);
        emailET = view.findViewById(R.id.emailET);
        phoneET = view.findViewById(R.id.phoneET);

        doStuffActivity.toggle.setDrawerIndicatorEnabled(false);
        doStuffActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_go_back);
        doStuffActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AccountFragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(AccountFragmentViewModel.class);

//        accountModel = (AccountModel) givenData.getSerializable("accountModel");

        loginET.setText(viewModel.getLogin().getValue());
        emailET.setText(viewModel.getEmail().getValue());
        nameET.setText(viewModel.getName().getValue());
        phoneET.setText(viewModel.getPhone().getValue());

        save.setOnClickListener(v->{

            String login, email,name,phone;
            login = loginET.getText().toString();
            email = emailET.getText().toString();
            name = nameET.getText().toString();
            phone = phoneET.getText().toString();

            RegisterFragment registerFragment = new RegisterFragment(getContext());

            if(registerFragment.isLoginVerified(login) && registerFragment.isEmailVerified(email)
                    && registerFragment.isNameVerified(name)) {

                DocumentReference accountRef = FirebaseFirestore.getInstance().collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                if(viewModel.getLogin().getValue() != login || viewModel.getName().getValue() != name) {

                    accountRef.update("login", login);
                    accountRef.update("name",name);
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseFirestore.getInstance().collection("Adverts")
                            .whereEqualTo("driverID",userID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(int i = 0;i<task.getResult().getDocuments().size();i++){
                                        DocumentReference advertRef = task.getResult().getDocuments().get(i).getReference();
                                        advertRef.update("driver",name + " (" + login + ")");
                                    }
                                }
                            });
                }

                if(viewModel.getPhone().getValue() != phone) {
                    accountRef.update("phone", phone);
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseFirestore.getInstance().collection("Adverts")
                            .whereEqualTo("driverID",userID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(int i = 0;i<task.getResult().getDocuments().size();i++){
                                        DocumentReference advertRef = task.getResult().getDocuments().get(i).getReference();
                                        advertRef.update("driverPhone",phone);
                                    }
                                }
                            });
                }

                if(viewModel.getEmail().getValue() != email){
                    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseAuth.getInstance().getCurrentUser().updateEmail(email);
                        accountRef.update("email",email);
                    }
                    else {
                        AccountFragment accountFragment = new AccountFragment();
                        accountFragment.getPasswordDialog (false,getLayoutInflater(), getContext());
                    }
                }

                viewModel.fetchAccountData(); // odświeżenie danych po updacie w bazie
                Toast.makeText(requireContext(), "Dane zostały zmienione!", Toast.LENGTH_SHORT).show();
                //Schowanie klawiatury
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                doStuffActivity.onBackPressed();
            }
            else{
                Toast.makeText(getContext(), "Dane niepoprawne!", Toast.LENGTH_SHORT).show();
            }


        });


        cancel.setOnClickListener(v->{
            doStuffActivity.onBackPressed();
        });

        return view;
    }

}