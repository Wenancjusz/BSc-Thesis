package com.example.pracainfrag.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragmentViewModel extends ViewModel {
    private FirebaseFirestore database;
    private FirebaseAuth auth;

    private final MutableLiveData<String> login = new MutableLiveData<>();
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> phone = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDriver = new MutableLiveData<>(false);

    private final MutableLiveData<String> carBrand = new MutableLiveData<>();
    private final MutableLiveData<String> carModel = new MutableLiveData<>();
    private final MutableLiveData<String> carType = new MutableLiveData<>();
    private final MutableLiveData<String> carColor = new MutableLiveData<>();



    public void fetchData() {
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) return;
        String userId = user.getUid();
        fetchAccountData();
        fetchCarData();
    }

    public void fetchAccountData(){
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) return;
        String userId = user.getUid();
        database.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();

                    login.setValue(document.getString("login"));
                    name.setValue(document.getString("name"));
                    email.setValue(document.getString("email"));
                    phone.setValue(document.getString("phone"));
                    isDriver.setValue(document.getBoolean("is_driver"));
//                    Log.d("ViewModelTag: ",isDriver.getValue().toString());
                });
    }

    public void fetchCarData(){
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) return;
        String userId = user.getUid();
        database.collection("Cars")
                .whereEqualTo("ownerID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().getDocuments().isEmpty() == false) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        carBrand.setValue(document.getString("brand"));
                        carModel.setValue(document.getString("model"));
                        carType.setValue(document.getString("carType"));
                        carColor.setValue(document.getString("carColor"));
                    }
                });
    }

    public LiveData<String> getLogin() {
        return login;
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public LiveData<Boolean> getIsDriver() {
        return isDriver;
    }

    public LiveData<String> getCarBrand() {
        return carBrand;
    }

    public LiveData<String> getCarModel() {
        return carModel;
    }

    public LiveData<String> getCarType() {
        return carType;
    }

    public LiveData<String> getCarColor() {
        return carColor;
    }

    public void setIsDriver(Boolean isDriver) {this.isDriver.setValue(isDriver);}

    public void setDatabase(FirebaseFirestore database) {
        this.database = database;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }
}
