package com.example.pracainfrag.adverts;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FiltersFragment extends Fragment {

    TextView toolbarTitle;
    EditText time, date, departure, arrival, price;
    Button filterButton;

    private FindTripsViewModel viewModel;

    private long selectedDate, selectedTime;

    Calendar c;
    SimpleDateFormat dateformat;
    boolean mIsRestoredFromBackstack;
    Date selectedDepartureTime = new Date();

    public FiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = Calendar.getInstance();
        mIsRestoredFromBackstack = false;
        viewModel = new ViewModelProvider(requireActivity()).get(FindTripsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters, container, false);

        toolbarTitle = view.findViewById(R.id.toolbarTitle);

        time = view.findViewById(R.id.departureTimeET);
        date = view.findViewById(R.id.dateDepartureET);
        departure = view.findViewById(R.id.departureET);
        arrival = view.findViewById(R.id.arrivalET);
        price = view.findViewById(R.id.priceET);
        filterButton = view.findViewById(R.id.filterButton);

        AppCompatActivity myActivity = (AppCompatActivity) getActivity();

        ActionBar actionBar = myActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        DoStuffActivity doStuffActivity = (DoStuffActivity) (getActivity());


        doStuffActivity.navigationView.getMenu().getItem(0).setChecked(true);
        doStuffActivity.toolbarTitle.setText("Znajdź przejazd");

        long currentTimeSeconds = Calendar.getInstance(TimeZone.getTimeZone("UTC+1")).getTimeInMillis() / 1000;
        selectedTime = currentTimeSeconds % 86400;
        selectedDate = currentTimeSeconds - selectedTime;


        dateformat = new SimpleDateFormat("HH:mm");
        String datetime = dateformat.format(c.getTime());
        time.setText(datetime);

        dateformat = new SimpleDateFormat("dd-MM-yyyy");
        datetime = dateformat.format(c.getTime());
        date.setText(datetime);

        date.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Data wyjazdu");

            CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                    .setValidator(new CalendarConstraints.DateValidator() {
                        @Override
                        public boolean isValid(long date) {
                            long currentTimeMillis = System.currentTimeMillis();
                            long todayMillis = currentTimeMillis % 86400000;
                            return date >= currentTimeMillis - todayMillis;
                        }

                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(Parcel parcel, int i) {
                        }
                    }).build();

            MaterialDatePicker<Long> materialDatePicker = builder
                    .setCalendarConstraints(calendarConstraints)
                    .build();

            materialDatePicker.show(getFragmentManager(), "DATE_PICKER");

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                selectedDate = selection / 1000;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                date.setText(dateFormat.format(materialDatePicker.getSelection()));

//                date.setText(materialDatePicker.getHeaderText());
            });
        });

        final int hour, min;

        dateformat = new SimpleDateFormat("HH");
        hour = Integer.parseInt(dateformat.format(c.getTime()));

        dateformat = new SimpleDateFormat("mm");
        min = Integer.parseInt(dateformat.format(c.getTime()));

        time.setOnClickListener(v -> {

            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selectedTime = (hourOfDay-1) * 3600L + minute * 60L;
                    time.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                }
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, min, true);
            timePickerDialog.show();
        });

        filterButton.setOnClickListener(v -> {
            if (validateInput() && isTimeCorrect(selectedDepartureTime.getTime()/1000,getContext())) {
                //Schowanie klawiatury
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                filter(departure.getText().toString(),
                        arrival.getText().toString(), Integer.parseInt(price.getText().toString()));
            }
        });

        return view;
    }

    private boolean validateInput() {
        if (!price.getText().toString().isEmpty()) {
            if (Integer.parseInt(price.getText().toString()) < 1) {
                Toast.makeText(getContext(), "Cena nie może być mniejsza niż 1!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(getContext(), "Cena nie może być pusta!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (departure.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Miasto wyjazdu nie może być puste!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (arrival.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Miasto docelowe nie może być puste!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isTimeCorrect(long time, Context context) {
        long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
        if(time < currentTime-5*60) {
            Toast.makeText(context, "Czas wyjazdu niepoprawny!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void filter( String departure, String arrival, int price) {
        selectedDepartureTime.setTime((selectedDate + selectedTime)*1000);
        viewModel.findTrips(selectedDepartureTime.getTime()/1000, departure, arrival, price);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new AdvertListFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsRestoredFromBackstack = true;
        selectedDepartureTime.setTime((selectedTime + selectedDate)*1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mIsRestoredFromBackstack) {
            selectedTime = selectedDepartureTime.getTime()/1000;
            selectedTime %= 86400;
            selectedDate = selectedDepartureTime.getTime()/1000 - selectedTime;

            dateformat = new SimpleDateFormat("HH:mm");
            String datetime = dateformat.format(selectedDepartureTime.getTime());
            time.setText(datetime);

            dateformat = new SimpleDateFormat("dd-MM-yyyy");
            datetime = dateformat.format(selectedDepartureTime.getTime());
            date.setText(datetime);
        }
    }
}