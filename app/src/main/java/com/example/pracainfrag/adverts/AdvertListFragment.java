package com.example.pracainfrag.adverts;

import android.os.Bundle;

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

import com.example.pracainfrag.R;
import com.example.pracainfrag.DoStuffActivity;

import java.util.List;

public class AdvertListFragment extends Fragment {

    TextView noAdsFound;
    Button filters;

    private RecyclerView recyclerView;
    private AdvertAdapter advertAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;

    private FindTripsViewModel viewModel;

    public AdvertListFragment() {
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

        View view = inflater.inflate(R.layout.fragment_advert_list, container, false);

        filters = view.findViewById(R.id.chngFiltersBTN);
        recyclerView = view.findViewById(R.id.myAdvertsList);
        progressBar = view.findViewById(R.id.progress);
        noAdsFound = view.findViewById(R.id.noAdvertsFoundTV);
        DoStuffActivity doStuffActivity = (DoStuffActivity) (getActivity());
        doStuffActivity.toolbarTitle.setText("Ogłoszenia");

        filters.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().popBackStackImmediate()
                        /*.replace(R.id.fragmentContainer, new FiltersFragment())
                        .commit()*/
        );

        viewModel.getAdverts().observe(getViewLifecycleOwner(), adverts -> showDatabase(adverts));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        return view;
    }

    protected void showDatabase(List<AdvertModel> adverts) {
        if(!adverts.isEmpty()) {
            noAdsFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            advertAdapter = new AdvertAdapter(adverts, getContext(), getActivity().getSupportFragmentManager());
            advertAdapter.notifyDataSetChanged();

            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getContext());

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(advertAdapter);
        }
        else {
            noAdsFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }
}