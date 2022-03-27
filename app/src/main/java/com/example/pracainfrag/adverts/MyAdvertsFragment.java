package com.example.pracainfrag.adverts;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MyAdvertsFragment extends Fragment {

    TextView noAdverts;
    RecyclerView myAdvertsList;
    ProgressBar myProgress;
    private AdvertAdapter advertAdapter;
    private RecyclerView.LayoutManager layoutManager;
    FindTripsViewModel viewModel;

    Button addBTN,searchBTN;

    public MyAdvertsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FindTripsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_adverts, container, false);

        DoStuffActivity doStuffActivity = (DoStuffActivity) (getActivity());

        ActionBar actionBar = doStuffActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        doStuffActivity.toolbarTitle.setText("Moje Ogłoszenia");

        doStuffActivity.navigationView.getMenu().getItem(2).setChecked(true);

        myAdvertsList = view.findViewById(R.id.myAdvertsList);
        addBTN = view.findViewById(R.id.addBTN);
        searchBTN = view.findViewById(R.id.searchBTN);
        myProgress = view.findViewById(R.id.myProgress);
        noAdverts = view.findViewById(R.id.noAdvertsTV);

        addBTN.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer,new AddingAdvertFragment()).commit();
        });
        searchBTN.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new FiltersFragment()).addToBackStack("myAdverts").commit();
        });


        viewModel.findTrips(FirebaseAuth.getInstance().getCurrentUser().getUid());

        viewModel.getAdverts().observe(getViewLifecycleOwner(), adverts -> {
            if(!adverts.isEmpty()) {
                showMyAdverts(adverts);
                viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                        myProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE));
            }
            else{
                myAdvertsList.setVisibility(View.INVISIBLE);
                noAdverts.setVisibility(View.VISIBLE);
                myProgress.setVisibility(View.GONE);
            }
        });

        return view;
    }


    void showMyAdverts(List<AdvertModel> adverts){

        myAdvertsList.setVisibility(View.VISIBLE);
        noAdverts.setVisibility(View.GONE);

        advertAdapter = new AdvertAdapter(adverts, getContext(),getActivity().getSupportFragmentManager());

        myAdvertsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        myAdvertsList.setLayoutManager(layoutManager);
        myAdvertsList.setAdapter(advertAdapter);
    }
}