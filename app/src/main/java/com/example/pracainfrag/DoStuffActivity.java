package com.example.pracainfrag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pracainfrag.account.AccountFragment;
import com.example.pracainfrag.account.AccountFragmentViewModel;

import com.example.pracainfrag.account.EditAccountDataFragment;
import com.example.pracainfrag.account.EditCarDataFragment;
import com.example.pracainfrag.account.changePasswordFragment;
import com.example.pracainfrag.adverts.AddingAdvertFragment;
import com.example.pracainfrag.adverts.AdvertFragment;
import com.example.pracainfrag.adverts.FiltersFragment;
import com.example.pracainfrag.adverts.MyAdvertsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DoStuffActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public ActionBarDrawerToggle toggle;
    ActionBar actionBar;
    androidx.appcompat.widget.Toolbar toolbar;
    public TextView toolbarTitle, loginDrawer;

    Fragment currFragment;

    AccountFragmentViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_stuff);
        viewModel = new ViewModelProvider(this).get(AccountFragmentViewModel.class);

        ///////////////////////////////////////////////////////////////   TOOLBAR i Actionbar

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);

        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);

        navigationDrawer();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new AccountFragment()).addToBackStack("account").commit();

    }

    @Override
    public void onBackPressed() {
        currFragment =  getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (currFragment instanceof changePasswordFragment) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AccountFragment()).commit();
            return;
        }

        if (currFragment instanceof AdvertFragment || currFragment instanceof EditCarDataFragment || currFragment instanceof EditAccountDataFragment) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        else {
            if (currFragment instanceof AccountFragment)
                signOut();
            else {
                Intent intent = new Intent(DoStuffActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }


    }

    public void signOut(){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Wyloguj się")
                    .setMessage("Czy na pewno chcesz sie wylogować?")
                    .setPositiveButton("Wyloguj", (dialogInterface, i) -> {
                        FirebaseAuth.getInstance().signOut();
                        loggedOut(true);
                    })
                    .setNegativeButton("Anuluj", null).create();
            dialog.show();
    }

    public void loggedOut(boolean accountExists) {
        Intent startActivity;
        startActivity = new Intent(DoStuffActivity.this, MainActivity.class);
        startActivity(startActivity);
        if(accountExists)
            Toast.makeText(getApplicationContext(), "Wylogowano", Toast.LENGTH_SHORT).show();
    }

    public void navigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        navigationView = findViewById(R.id.nav_view);

        loginDrawer = navigationView.getHeaderView(0).findViewById(R.id.login_drawer);

        AccountFragmentViewModel vm;
        vm = new ViewModelProvider(this).get(AccountFragmentViewModel.class);

        loginDrawer.setText(vm.getLogin().toString());

        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                onBackPressed();
            }
        });

        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        currFragment =  getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        switch(item.getItemId()){

            case R.id.nav_filters:
                if(currFragment instanceof FiltersFragment)
                    break;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new FiltersFragment()).addToBackStack("filters").commit();
                break;

            case R.id.nav_add_advert:
                if(currFragment instanceof AddingAdvertFragment)
                    break;
                if(viewModel.getIsDriver().getValue().booleanValue())
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new AddingAdvertFragment()).addToBackStack("AddAdvert").commit();
                else
                    Toast.makeText(this, "Nie jesteś kierowcą! Dodaj pojazd!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_my_adverts:
                if(currFragment instanceof MyAdvertsFragment)
                    break;
                if(viewModel.getIsDriver().getValue().booleanValue())
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new MyAdvertsFragment()).addToBackStack("account").commit();
                else
                    Toast.makeText(this, "Nie jesteś kierowcą! Dodaj pojazd!", Toast.LENGTH_SHORT).show();

                break;

            case R.id.nav_account:
                if(currFragment instanceof AccountFragment)
                    break;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new AccountFragment()).addToBackStack("account").commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}