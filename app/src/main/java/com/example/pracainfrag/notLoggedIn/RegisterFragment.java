package com.example.pracainfrag.notLoggedIn;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pracainfrag.R;
import com.example.pracainfrag.account.AccountModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterFragment extends Fragment{

    Button register;
    EditText loginET, passwordET, confPasswordET, emailET, nameET, phoneET;
    TextView signIn, registerTV;
    Context context;

    AccountModel accountModel;

    private static final String TAG = "RegisterActivity";

    public FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public RegisterFragment() {
        // Required empty public constructor
    }
    public RegisterFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        register = view.findViewById(R.id.signUpButton);
        signIn = view.findViewById(R.id.loginTextView);
        registerTV = view.findViewById(R.id.registerTV);
        loginET = view.findViewById(R.id.editTextLogin);
        passwordET = view.findViewById(R.id.editTextPassword);
        confPasswordET = view.findViewById(R.id.editTextConfirmPassword);
        emailET = view.findViewById(R.id.editTextEmail);
        nameET = view.findViewById(R.id.editTextName);
        phoneET = view.findViewById(R.id.editTextPhone);

        onConfigurationChanged(getActivity().getResources().getConfiguration());

        mDb = FirebaseFirestore.getInstance();

        signIn.setOnClickListener(v->{
            if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>1)
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            else
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.welcomeFragmentContainer, new LoginFragment()).addToBackStack(null).commit();
        });

        register.setOnClickListener(v->{

            if(!isEmpty(loginET.getText().toString()) && !isEmpty(passwordET.getText().toString())
                    && !isEmpty(confPasswordET.getText().toString()) && !isEmpty(emailET.getText().toString())
                    && !isEmpty(nameET.getText().toString()) && !isEmpty(phoneET.getText().toString())){

                accountModel = new AccountModel();

                String password,confPassword;

                accountModel.setLogin(loginET.getText().toString());
                accountModel.setEmail(emailET.getText().toString());
                accountModel.setName(nameET.getText().toString());
                accountModel.setPhoneNumber(phoneET.getText().toString());

                password = passwordET.getText().toString();
                confPassword = confPasswordET.getText().toString();

                if (isLoginVerified(accountModel.getLogin()) && isPasswordVerified(password, confPassword) &&
                        isEmailVerified(accountModel.getEmail()) && isNameVerified(accountModel.getName())
                        && isPhoneVerified(accountModel.getPhone())) {

                    registerNewEmail(emailET.getText().toString(), loginET.getText().toString(),
                            passwordET.getText().toString(), nameET.getText().toString(), phoneET.getText().toString());
                }

            } else {
                Toast.makeText(getContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public boolean isLoginVerified(String login) {
        if(login.length()>=3) {
            String loginPattern = "^[A-Za-z0-9_-]*$";
            if(login.matches(loginPattern)) {
                return true;
            }
            else
                Toast.makeText(context, "Login musi składać się z samych cyfr i liter!", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(context, "Login musi zawierać przynajmniej 3 znaki!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean isEmailVerified(String email) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        Toast.makeText(context, "Niepoprawny adres e-mail!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean isPasswordVerified(String password, String confPassword) {
        if(password.length()>=8){

            if (!password.matches("(.*[a-z].*)")) {
                Toast.makeText(context, "Hasło musi zawierać przynajmniej jedną małą literę", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!password.matches("(.*[A-Z].*)")) {
                Toast.makeText(context, "Hasło musi zawierać przynajmniej jedną wielką literę", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!password.matches("(.*[0-9].*)")) {
                Toast.makeText(context, "Hasło musi zawierać przynajmniej jedną cyfrę", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!password.matches("(.*[!@#&()–[{}]:;',?/*~$^+=<>].*)")) {
                Toast.makeText(context, "Hasło musi zawierać przynajmniej jeden znak specjalny", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Czy hasła są takie same
            if(password.hashCode() == confPassword.hashCode()){
                return true;
            }
            else {
                Toast.makeText(context, "Podane hasła muszą być takie same!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(context, "Hasło musi składać się z przynajmniej 8 znaków!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isNameVerified(String name) {
        if(name.length()>1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase(); //Ustawienie pierwszej litery na wielką, niezależnie co wprowadził użytkownik
            return true;
        }
        return false;
    }

    private boolean isPhoneVerified(String phoneNumber) {
        if(!Patterns.PHONE.matcher(phoneNumber).matches() && (phoneNumber.length() <= 9||phoneNumber.length() > 12)) {
            Toast.makeText(getContext(), "Numer telefonu niepoprawny!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void registerNewEmail(final String email, String login, String password, String name, String phone){

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            AccountModel user = new AccountModel();
                            user.setEmail(email);
                            user.setLogin(login);
                            user.setName(name);
                            user.setPhoneNumber(phone);
                            user.setIs_driver(false);
                            user.setUser_id(FirebaseAuth.getInstance().getUid());

                            DocumentReference newUserRef = mDb
                                    .collection(getString(R.string.collection_users))
                                    .document(FirebaseAuth.getInstance().getUid());

                            newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Bundle extras = new Bundle();
                                        extras.putString("email",email);
                                        extras.putString("password",password);

                                        LoginFragment loginFragment = new LoginFragment();

                                        loginFragment.setArguments(extras);

                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.welcomeFragmentContainer, loginFragment).addToBackStack(null).commit();

                                        Toast.makeText(getContext(), "Zarejestrowano!", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(getContext(), "Nie udało się zapisać danych!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                        else{
                            String exception = task.getException().toString();

                            if(exception.contains("is already in use by another account"))
                                Toast.makeText(getContext(), "Istnieje konto powiązane z tym adresem e-mail!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), "Nie udało się stworzyć konta! " + exception, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) registerTV.getLayoutParams();

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.verticalBias = 0.006f;
        }
        else {
            params.verticalBias = 0.076f;
        }
        registerTV.setLayoutParams(params);
        super.onConfigurationChanged(newConfig);
    }

}