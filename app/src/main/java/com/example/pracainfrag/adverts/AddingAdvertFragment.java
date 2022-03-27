package com.example.pracainfrag.adverts;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pracainfrag.DoStuffActivity;
import com.example.pracainfrag.R;
import com.example.pracainfrag.account.AccountFragmentViewModel;
import com.example.pracainfrag.account.changePasswordFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddingAdvertFragment extends Fragment {

    EditText departureET, arrivalET, departureDateET, passengersET, priceET, departureTimeET;
    Button cancelBTN, addBTN;
    AdvertModel advertModel;
    //Advert advert = new Advert();

    public FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private long selectedDate, selectedTime;

    public AddingAdvertFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_advert, container, false);

        departureTimeET = view.findViewById(R.id.departureTimeETAdd);
        departureDateET = view.findViewById(R.id.dateDepartureET);
        departureET = view.findViewById(R.id.departureET);
        arrivalET = view.findViewById(R.id.arrivalET);
        passengersET = view.findViewById(R.id.passengersET);
        priceET = view.findViewById(R.id.priceET);


        cancelBTN = view.findViewById(R.id.cancelBTN);
        addBTN = view.findViewById(R.id.addBTN);

        AppCompatActivity myActivity = (AppCompatActivity)getActivity();
        ActionBar actionBar = myActivity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        DoStuffActivity doStuffActivity = (DoStuffActivity) (getActivity());
        doStuffActivity.navigationView.getMenu().getItem(1).setChecked(true);
        doStuffActivity.toolbarTitle.setText("Dodaj ogłoszenie");

        SimpleDateFormat dateformat;
        Calendar c = Calendar.getInstance();

        mDb = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            advertModel = (AdvertModel) getArguments().getSerializable("advertModel");
            Date date = advertModel.getDepartureTime().toDate();
            dateformat = new SimpleDateFormat("HH:mm");
            String datetime = dateformat.format(date.getTime());
            departureTimeET.setText(datetime);
            dateformat = new SimpleDateFormat("dd-MM-yyyy");
            datetime = dateformat.format(date.getTime());
            departureDateET.setText(datetime);
            departureET.setText(advertModel.getDepartureCity());
            arrivalET.setText(advertModel.getDestinationCity());
            passengersET.setText(String.valueOf(advertModel.getSeats()));
            priceET.setText(String.valueOf(advertModel.getPrice()));

            selectedTime = date.getTime()/1000;
            selectedTime %= 86400;
            selectedDate = date.getTime()/1000 - selectedTime;

            doStuffActivity.toolbarTitle.setText("Edytuj ogłoszenie");
            addBTN.setText("Zapisz");
        } else {
            advertModel = new AdvertModel();

            dateformat = new SimpleDateFormat("HH:mm");
            String currentTime = dateformat.format(c.getTime());
            departureTimeET.setText(currentTime);

            dateformat = new SimpleDateFormat("dd-MM-yyyy");
            String currentDate = dateformat.format(c.getTime());
            departureDateET.setText(currentDate);

            long currentTimeSeconds = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() / 1000;
            selectedTime = currentTimeSeconds % 86400;
            selectedDate = currentTimeSeconds - selectedTime;
        }



        departureDateET.setOnClickListener(v -> {
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
                departureDateET.setText(dateFormat.format(materialDatePicker.getSelection()));
            });
        });

        final int hour, min;

        dateformat = new SimpleDateFormat("HH");
        hour = Integer.parseInt(dateformat.format(c.getTime()));

        dateformat = new SimpleDateFormat("mm");
        min = Integer.parseInt(dateformat.format(c.getTime()));

        departureTimeET.setOnClickListener(v -> {

            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selectedTime = (hourOfDay-1) * 3600L + minute * 60L;
                    departureTimeET.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                }
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, min, true);
            timePickerDialog.show();
        });

        AccountFragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(AccountFragmentViewModel.class);

            addBTN.setOnClickListener(v -> {
                    if (isItFilled(departureET) && isItFilled(arrivalET) && isItFilled(departureDateET)
                            && isItFilled(passengersET) && isItFilled(priceET)) {
                        if (isTimeCorrect(selectedDate + selectedTime, getContext())) {
                            advertModel.setDepartureCity(departureET.getText().toString());
                            advertModel.setDestinationCity(arrivalET.getText().toString());
                            advertModel.setSeats(Integer.parseInt(passengersET.getText().toString()));
                            advertModel.setPrice(Integer.parseInt(priceET.getText().toString()));
                            advertModel.setDepartureTime(new Timestamp(selectedDate + selectedTime, 0));
                            advertModel.setBrand(viewModel.getCarBrand().getValue());
                            advertModel.setModel(viewModel.getCarModel().getValue());
                            advertModel.setColor(viewModel.getCarColor().getValue());
                            advertModel.setType(viewModel.getCarType().getValue());
                            advertModel.setDriverPhone(viewModel.getPhone().getValue());
                            advertModel.setDriver(viewModel.getName().getValue() + " (" +
                                    viewModel.getLogin().getValue() + ")");
                            advertModel.setDriverID(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //Schowanie klawiatury
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                            if (getArguments() == null) {
                                createNewAdvert(advertModel, getContext());
                            } else {
                                DocumentReference advertRef = FirebaseFirestore.getInstance().collection("Adverts")
                                        .document(advertModel.getId());

                                advertRef.update("departureCity", advertModel.getDepartureCity());
                                advertRef.update("destinationCity", advertModel.getDestinationCity());
                                advertRef.update("seats", advertModel.getSeats());
                                advertRef.update("price", advertModel.getPrice());
                                advertRef.update("departureTime", advertModel.getDepartureTime());
                            }

                            if(getArguments() == null)
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, new MyAdvertsFragment()).commit();
                            else
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "Uzupełnij dane!", Toast.LENGTH_SHORT).show();
                    }

            });

            cancelBTN.setOnClickListener(v -> {
                    getActivity().onBackPressed();
            });


        return view;
    }

    private boolean isTimeCorrect(long time, Context context) {
        long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
        if(time < currentTime - 5*60) {
            Toast.makeText(context, "Czas wyjazdu niepoprawny!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isItFilled(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    public void createNewAdvert(AdvertModel advert,Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newAdvertRef = db
                .collection("Adverts")
                .document();

        advert.setId(newAdvertRef.getId());

        newAdvertRef.set(advert).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Dodano ogłoszenie", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Nie udało się dodać ogłoszenia", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}