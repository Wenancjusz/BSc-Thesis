package com.example.pracainfrag.adverts;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.R;
import com.example.pracainfrag.adverts.modules.DirectionFinder;
import com.example.pracainfrag.adverts.modules.DirectionFinderListener;
import com.example.pracainfrag.adverts.modules.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdvertFragment extends Fragment implements  OnMapReadyCallback, DirectionFinderListener {

    TextView leavingCityTV, destinationCityTV, dateTV, timeTV, brandTV, modelTV,
            carColorTV, driverTV,carTypeTV, priceTV, seatsTV;

    Button contactBTN;

    DoStuffActivity doStuffActivity;

    ActionBar actionBar;
    AdvertModel advertModel;

    private MapView mMapView;
    private GoogleMap mMap;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    private String origin, destination;

    public AdvertFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advert, container, false);

        leavingCityTV = view.findViewById(R.id.leavingCity);
        destinationCityTV = view.findViewById(R.id.destinationTV);
        dateTV = view.findViewById(R.id.dateTV);
        timeTV = view.findViewById(R.id.timeTV);
        priceTV = view.findViewById(R.id.priceTV);
        brandTV = view.findViewById(R.id.brandTV);
        modelTV = view.findViewById(R.id.modelTV);
        driverTV = view.findViewById(R.id.driverNameTV);
        carColorTV = view.findViewById(R.id.carColorTV);
        carTypeTV = view.findViewById(R.id.typeTV);
        seatsTV = view.findViewById(R.id.seatsTV);
        contactBTN = view.findViewById(R.id.contactBTN);


        doStuffActivity = (DoStuffActivity) (getActivity());
        actionBar = doStuffActivity.getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);


        doStuffActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        doStuffActivity.toolbarTitle.setText("Ogłoszenie");

        doStuffActivity.toggle.setDrawerIndicatorEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mMapView = view.findViewById(R.id.mapView);
        initGoogleMap(savedInstanceState);


        String leavingCity, destinationCity, date, time, brand, model, color, type, driver, phone;
        int price, seats;

        advertModel = (AdvertModel) getArguments().getSerializable("advertModel");

        leavingCity = advertModel.getDepartureCity();
        destinationCity = advertModel.getDestinationCity();

        Date timestampToDate = advertModel.getDepartureTime().toDate();
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
        String datetime = dateformat.format(timestampToDate.getTime());
        time = datetime;

        dateformat = new SimpleDateFormat("dd-MM-yyyy");
        datetime = dateformat.format(timestampToDate.getTime());
        date = datetime;

        brand = advertModel.getBrand();
        model = advertModel.getModel();
        driver = advertModel.getDriver();
        color = advertModel.getColor();
        type = advertModel.getType();
        price = advertModel.getPrice();
        seats = advertModel.getSeats();
        phone = advertModel.getDriverPhone();

        origin = leavingCity;
        destination = destinationCity;


        leavingCityTV.append(leavingCity);
        destinationCityTV.append(destinationCity);
        dateTV.append(date);
        timeTV.append(time);
        brandTV.append(brand);
        modelTV.append(model);
        carColorTV.append(color);
        carTypeTV.append(type);
        driverTV.append(driver);
        priceTV.append(String.valueOf(price));
        seatsTV.append(String.valueOf(seats));


        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("driverID: ",advertModel.getDriverID());

        if(currentUserID.equals(advertModel.getDriverID())){
            contactBTN.setVisibility(View.GONE);
            setHasOptionsMenu(true);
        }
        else{
            setHasOptionsMenu(false);
        }


        contactBTN.setOnClickListener(v->{
           AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Kontakt do kierowcy")
                    .setMessage("Numer telefonu: "+ phone)
                    .setPositiveButton("Ok", null).create();
            dialog.show();


        });


        return view;
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.advert_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_edit_advert:
                AddingAdvertFragment addingAdvertFragment = new AddingAdvertFragment();
                Bundle data = new Bundle();
                data.putSerializable("advertModel",advertModel);
                addingAdvertFragment.setArguments(data);
                doStuffActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, addingAdvertFragment).addToBackStack(null).commit();
                return true;

            case R.id.action_delete_advert:

                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Usuń ogłoszenie")
                        .setMessage("Czy na pewno chcesz usunąć ogłoszenie?")
                        .setPositiveButton("Usuń", (dialogInterface, i) -> {
                            FirebaseFirestore mDb = FirebaseFirestore.getInstance();
                            mDb.collection("Adverts")
                                    .whereEqualTo("id", advertModel.getId())
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (!task.getResult().getDocuments().isEmpty()) {
                                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                            DocumentReference documentReference = document.getReference();
                                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Advert Deleted: ", documentReference.toString());
                                                    doStuffActivity.onBackPressed();
                                                    doStuffActivity.onBackPressed();
                                                }
                                            });
                                        }
                                    });
                        })
                        .setNegativeButton("Anuluj", null).create();

                dialog.show();

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle("MapViewBundleKey");
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle("MapViewBundleKey", mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        DirectionFinder findDirection = new DirectionFinder( this, origin, destination);

        try {
            findDirection.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDirectionFinderStart() {

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        //progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        float zoom = 0;

        for (Route route : routes) {

            if(route.distance.value<150000){zoom = 7.8f;}
            else if(route.distance.value<330000){zoom = 7;}
            else if(route.distance.value<530000){zoom = 6;}
            else {zoom = 5.3f;}


            LatLng center = new LatLng(((route.startLocation.latitude+route.endLocation.latitude)/2.0), ((route.startLocation.longitude+route.endLocation.longitude)/2.0));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

}