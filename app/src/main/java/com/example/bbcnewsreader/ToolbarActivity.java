package com.example.bbcnewsreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class ToolbarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        DrawerLayout draw = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, draw, tBar, 0, 0);
        draw.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        Intent chatRoomPage = new Intent(this, NewsListActivity.class);
        TextView navChat = (TextView) header.findViewById(R.id.navChat);
        navChat.setOnClickListener( (click) -> {
            startActivity(chatRoomPage);
        });

        Intent weatherForecastPage = new Intent(this, FavListActivity.class);
        TextView navWeather = (TextView) header.findViewById(R.id.navWeather);
        navWeather.setOnClickListener( (click) -> {
            startActivity(weatherForecastPage);
        });

        Intent loginPage = new Intent(this, MainActivity.class);
        TextView navLogin = (TextView) header.findViewById(R.id.navLogin);
        navLogin.setOnClickListener( (click) -> {
            startActivity(loginPage);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String message = null;

        switch (item.getItemId()) {
            case R.id.item_twitter:
                message = "You clicked on Twitter";
                break;
            case R.id.item_facebook:
                message = "You clicked on Facebook";
                break;
            case R.id.item_instagram:
                message = "You clicked on Instagram";
                break;
            case R.id.item_linkedin:
                message = "You clicked on the overflow menu";
                break;
            case R.id.navChat:
                message = "You clicked on navChat";
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }
}