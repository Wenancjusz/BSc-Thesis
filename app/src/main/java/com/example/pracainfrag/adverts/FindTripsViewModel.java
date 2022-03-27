package com.example.pracainfrag.adverts;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FindTripsViewModel extends ViewModel {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<AdvertModel>> adverts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public void findTrips(long when, String origin, String destination, int maxPrice) {
        isLoading.setValue(true);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                        Log.d("Anonymous Login", " Successful");
                    else
                        Log.d("Anonymous Login", " Unuccessful");
                }
            });

        database.collection("Adverts")
                .whereEqualTo("departureCity", origin)
                .whereEqualTo("destinationCity", destination)
                .whereLessThanOrEqualTo("departureTime", new Timestamp(when + 3600*3, 0))
                .whereGreaterThanOrEqualTo("departureTime", new Timestamp(when-3600,0))
                .orderBy("departureTime")
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);

                    List<AdvertModel> results = task.getResult()
                            .getDocuments()
                            .stream()
                            .filter(d -> d.getLong("price").intValue() <= maxPrice)
                            .map(d -> {

                                return new AdvertModel(
                                        d.getString("id"),
                                        d.getString("driverID"), d.getString("departureCity"),
                                        d.getString("destinationCity"),d.getTimestamp("departureTime"),
                                        d.getString("driverPhone"), d.getString("brand"),
                                        d.getString("model"),
                                        d.getString("color"), d.getString("driver"),
                                        d.getString("type"), d.getLong("price").intValue(),
                                        d.getLong("seats").intValue());
                            })
                            .collect(Collectors.toList());

                    adverts.setValue(results);
                });
    }

    public void findTrips(String driverID) {
        isLoading.setValue(true);

        database.collection("Adverts")
                .whereEqualTo("driverID", driverID)
                .orderBy("departureTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);

                    List<AdvertModel> results = task.getResult()
                            .getDocuments()
                            .stream()
                            .map(d -> {

                                return new AdvertModel(
                                        d.getString("id"),
                                        d.getString("driverID"), d.getString("departureCity"),
                                        d.getString("destinationCity"),d.getTimestamp("departureTime"),
                                        d.getString("driverPhone"), d.getString("brand"),
                                        d.getString("model"),
                                        d.getString("color"), d.getString("driver"),
                                        d.getString("type"), d.getLong("price").intValue(),
                                        d.getLong("seats").intValue());
                            })
                            .collect(Collectors.toList());

                    adverts.setValue(results);
                });
    }

    public LiveData<List<AdvertModel>> getAdverts() {
        return adverts;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
