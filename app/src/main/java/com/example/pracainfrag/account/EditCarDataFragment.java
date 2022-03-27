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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditCarDataFragment extends Fragment {

    public EditCarDataFragment() {
        // Required empty public constructor
    }

    public boolean addCar = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_car_data,container,false);

        Button cancel,save;

        EditText brandET,modelET,colorET;
        Spinner typeET;

        DoStuffActivity doStuffActivity = (DoStuffActivity) (getActivity());

        cancel = view.findViewById(R.id.cancelBTN);
        save = view.findViewById(R.id.saveBTN);

        brandET = view.findViewById(R.id.brandET);
        modelET = view.findViewById(R.id.modelET);
        colorET = view.findViewById(R.id.carColorET);
        typeET = view.findViewById(R.id.carTypeET);

        AccountFragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(AccountFragmentViewModel.class);

        if(!addCar){
            doStuffActivity.toolbarTitle.setText("Edytuj dane pojazdu");
            brandET.setText(viewModel.getCarBrand().getValue());
            modelET.setText(viewModel.getCarModel().getValue());
            colorET.setText(viewModel.getCarColor().getValue());
            typeET.setSelection(getIndex(typeET, viewModel.getCarType().getValue()));
        }
        else{
            doStuffActivity.toolbarTitle.setText("Dodaj pojazd");
        }


        doStuffActivity.toggle.setDrawerIndicatorEnabled(false);
        doStuffActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_go_back);
        doStuffActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        save.setOnClickListener(v->{
            String brand,model,color,type;

            brand = brandET.getText().toString();
            model = modelET.getText().toString();
            color = colorET.getText().toString();
            type = typeET.getSelectedItem().toString();

            if(onlyLetters(brand) && onlyLetters(color)){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String ownerID = FirebaseAuth.getInstance().getCurrentUser().getUid();



                if(addCar) {
                    CarDataModel carDataModel = new CarDataModel();
                    carDataModel.setBrand(brand);
                    carDataModel.setModel(model);
                    carDataModel.setCarColor(color);
                    carDataModel.setCarType(type);
                    carDataModel.setOwnerID(ownerID);

                    db.collection("Users")
                            .document(ownerID)
                            .update("is_driver", true);

                    DocumentReference newCarRef = db.collection("Cars")
                            .document();
                    newCarRef.set(carDataModel);
                }
                else{
                    FirebaseFirestore.getInstance().collection("Cars")
                            .whereEqualTo("ownerID",ownerID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    DocumentReference carRef = task.getResult().getDocuments().get(0).getReference();

                                    carRef.update("brand",brand);
                                    carRef.update("model",model);
                                    carRef.update("carColor",color);
                                    carRef.update("carType",type);
                                }
                            });

                    FirebaseFirestore.getInstance().collection("Adverts")
                            .whereEqualTo("driverID",ownerID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(int i = 0;i<task.getResult().getDocuments().size();i++){
                                        DocumentReference advertRef = task.getResult().getDocuments().get(i).getReference();

                                        advertRef.update("brand",brand);
                                        advertRef.update("model",model);
                                        advertRef.update("color",color);
                                        advertRef.update("type",type);
                                        viewModel.fetchCarData();
                                    }

                                }
                            });

                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                doStuffActivity.onBackPressed();
            }
            else{
                Toast.makeText(getContext(), "Niepoprawne dane!", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v->{
            doStuffActivity.onBackPressed();
        });
        return view;
    }

    private boolean onlyLetters(String text) {
        String pattern = "^[A-Za-z_-]*$";
        if(text.matches(pattern) && !text.isEmpty())
            return true;
        return false;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }
}