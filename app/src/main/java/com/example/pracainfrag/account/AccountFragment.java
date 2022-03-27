package com.example.pracainfrag.account;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.List;


public class AccountFragment extends Fragment {

    Button addBTN, editAccountBTN, editCarDataBTN, deleteCarBTN;

    TextView carInfo, nameTV, emailTV, brandTV, modelTV, carTypeTV, carColorTV,
            tripsTravelledTV, distanceTravelledTV, driverRatingTV, loginTV, phoneTV;

    DrawerLayout drawerLayout;
    ActionBar actionBar;

    FirebaseFirestore mDb;
    FirebaseUser user;

    DoStuffActivity doStuffActivity;

    AccountFragmentViewModel viewModel;

    boolean editOptions = false;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDb = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = new ViewModelProvider(requireActivity()).get(AccountFragmentViewModel.class);
        viewModel.setAuth(FirebaseAuth.getInstance());
        viewModel.setDatabase(FirebaseFirestore.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        AppCompatActivity myActivity = (AppCompatActivity) getActivity();
        actionBar = myActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        doStuffActivity = (DoStuffActivity) (getActivity());

        doStuffActivity.toolbarTitle.setText("Konto");

        setViews(view);

        doStuffActivity.navigationView.getMenu().getItem(3).setChecked(true);


        viewModel.fetchData();

        viewModel.getLogin().observe(getViewLifecycleOwner(), login -> {
            doStuffActivity.loginDrawer.setText(login);
            loginTV.setText("Nazwa użytkownika: " + login);
        });
        viewModel.getName().observe(getViewLifecycleOwner(), name -> {
            nameTV.setText("Imię: " + name);
        });
        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            emailTV.setText("E-mail: " + email);
        });
        viewModel.getPhone().observe(getViewLifecycleOwner(), phone -> {
            phoneTV.setText("Numer telefonu: " + phone);
        });
        viewModel.getIsDriver().observe(getViewLifecycleOwner(), isDriver -> {

            if (!isDriver) {
                carInfo.setVisibility(View.GONE);
                brandTV.setVisibility(View.GONE);
                modelTV.setVisibility(View.GONE);
                carTypeTV.setVisibility(View.GONE);
                carColorTV.setVisibility(View.GONE);
                editCarDataBTN.setVisibility(View.VISIBLE);
                editCarDataBTN.setText("Dodaj pojazd");
                deleteCarBTN.setVisibility(View.GONE);
            } else {
                carInfo.setVisibility(View.VISIBLE);
                brandTV.setVisibility(View.VISIBLE);
                modelTV.setVisibility(View.VISIBLE);
                carTypeTV.setVisibility(View.VISIBLE);
                carColorTV.setVisibility(View.VISIBLE);
                editCarDataBTN.setVisibility(View.GONE);
                deleteCarBTN.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getCarBrand().observe(getViewLifecycleOwner(), brand -> {
            brandTV.setText("Marka: " + brand);
        });
        viewModel.getCarModel().observe(getViewLifecycleOwner(), model -> {
            modelTV.setText("Model: " + model);
        });
        viewModel.getCarType().observe(getViewLifecycleOwner(), type -> {
            carTypeTV.setText("Typ: " + type);
        });
        viewModel.getCarColor().observe(getViewLifecycleOwner(), color -> {
            carColorTV.setText("Kolor: " + color);
        });


        editAccountBTN.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new EditAccountDataFragment()).addToBackStack("account").commit();
        });

        editCarDataBTN.setOnClickListener(v -> {

           EditCarDataFragment editCarDataFragment = new EditCarDataFragment();

           if (!viewModel.getIsDriver().getValue().booleanValue()){
                editCarDataFragment.addCar = true;
            }

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, editCarDataFragment).addToBackStack("account").commit();
        });

        deleteCarBTN.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Usuń pojazd")
                    .setMessage("Czy na pewno chcesz usunąć swój pojazd?")
                    .setPositiveButton("Usuń", (dialogInterface, i) -> {
                        mDb.collection("Cars")
                                .whereEqualTo("ownerID", user.getUid())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (!task.getResult().getDocuments().isEmpty()) {
                                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                        DocumentReference documentReference = document.getReference();
                                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Car Deleted: ", documentReference.toString());
                                                editCarDataBTN.setText("Dodaj pojazd");
                                                viewModel.setIsDriver(false);

                                                mDb.collection("Users")
                                                        .document(user.getUid())
                                                        .update("is_driver", false);
                                            }
                                        });
                                    }
                                });

                        mDb.collection("Adverts")
                                .whereEqualTo("driverID", user.getUid())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (!task.getResult().getDocuments().isEmpty()) {
                                        List<DocumentSnapshot> docs = task.getResult().getDocuments();

                                        for(int j = 0;j<docs.size();j++) {
                                            DocumentSnapshot document = task.getResult().getDocuments().get(j);
                                            DocumentReference documentReference = document.getReference();
                                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Advert Deleted: ", documentReference.toString());
                                                }
                                            });
                                        }
                                    }
                                    else
                                        Log.d("Adverts","No adverts found");
                                });
                        viewModel.setIsDriver(false);

                    })
                    .setNegativeButton("Anuluj", null).create();
            dialog.show();
        });

        return view;
    }

    private void setViews(View view) {
        addBTN = view.findViewById(R.id.addBTN);
        editAccountBTN = view.findViewById(R.id.editAccountBTN);
        editCarDataBTN = view.findViewById(R.id.editCarBTN);
        /////////////////////////////////////////////////////////////////////////User things

        loginTV = view.findViewById(R.id.loginTV);
        nameTV = view.findViewById(R.id.nameTV);
        emailTV = view.findViewById(R.id.emailTV);
        phoneTV = view.findViewById(R.id.phoneTV);

        ///////////////////////////////////////////////////////////////////////Car things
        carInfo = view.findViewById(R.id.textView3);
        brandTV = view.findViewById(R.id.brandTV);
        modelTV = view.findViewById(R.id.modelTV);
        carTypeTV = view.findViewById(R.id.carTypeTV);
        carColorTV = view.findViewById(R.id.carColorTV);
        deleteCarBTN = view.findViewById(R.id.deleteCarBTN);

        ///////////////////////////////////////////////////////////////////////Stats things
        tripsTravelledTV = view.findViewById(R.id.tripsTravelledTV);
        distanceTravelledTV = view.findViewById(R.id.distanceTravelledTV);
        driverRatingTV = view.findViewById(R.id.driverRatingTV);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.account_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_change_password:
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
                ((DoStuffActivity) requireActivity()).drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new changePasswordFragment()).commit();
                break;
            case R.id.action_edit:
                if (editAccountBTN.getVisibility() == View.VISIBLE)
                    editAccountBTN.setVisibility(View.GONE);
                else
                    editAccountBTN.setVisibility(View.VISIBLE);
                if(viewModel.getIsDriver().getValue().booleanValue()) {
                    editCarDataBTN.setText("Edytuj dane pojazdu");
                    if (editCarDataBTN.getVisibility() == View.VISIBLE)
                        editCarDataBTN.setVisibility(View.GONE);
                    else
                        editCarDataBTN.setVisibility(View.VISIBLE);
                }
                else{
                    editCarDataBTN.setText("Dodaj pojazd");
                }
                  break;
            case R.id.action_signOut:
                ((DoStuffActivity) requireActivity()).signOut();
                return true;
            case R.id.action_deleteAccount:

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Usuń konto")
                            .setMessage("Czy na pewno chcesz usunąć konto?")
                            .setPositiveButton("Usuń", (dialogInterface, i) -> {
                                deleteUserDocuments();
                            })
                            .setNegativeButton("Anuluj", null).create();
                    dialog.show();
                }
                else
                    getPasswordDialog(true,getLayoutInflater(),getContext());

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteUserDocuments() {
        mDb.collection("Cars")
                .whereEqualTo("ownerID", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        DocumentReference documentReference = document.getReference();
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Car Deleted: ", documentReference.toString());
                            }
                        });
                    }
                    else
                        Log.d("Car","No car found");
                });

        mDb.collection("Adverts")
                .whereEqualTo("driverID", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().getDocuments().isEmpty()) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();

                        for(int i = 0;i<docs.size();i++) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(i);
                            DocumentReference documentReference = document.getReference();
                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Advert Deleted: ", documentReference.toString());
                                }
                            });
                        }
                    }
                    else
                        Log.d("Adverts","No adverts found");
                });

        mDb.collection("Users").document(user.getUid()).delete().isSuccessful();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DEL_account", "User account deleted.");
                        }
                    }
                });

        ((DoStuffActivity) getActivity()).loggedOut(false);
    }

    void getPasswordDialog(boolean deletingAccount, LayoutInflater inflater, Context context) {
        AlertDialog.Builder alert;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alert = new AlertDialog.Builder(context);
        }


        if(inflater == null)
            Log.d("Inflater", "null");
        View view = inflater.inflate(R.layout.dialog_delete, null);

        TextView passwordET = view.findViewById(R.id.password);
        Button btn_delete = view.findViewById(R.id.btn_delete);

        alert.setView(view);
        alert.setCancelable(true);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordET.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Podaj hasło!", Toast.LENGTH_SHORT);
                } else {
                    if (!reAuthUser(passwordET.getText().toString()))
                        Toast.makeText(getContext(), "Hasło niepoprawne!", Toast.LENGTH_SHORT).show();
                    else{
                        if(deletingAccount) {
                            deleteUserDocuments();
                        }
                    }
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    boolean reAuthUser(String password) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        if(user.reauthenticate(credential).isSuccessful()) {
            Log.d("Re-Auth", "User re-authenticated.");
            return true;
        } else {
            Log.d("Auth", "Not reauthenticated");
            return false;
        }
    }

}