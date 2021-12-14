package com.example.bbcnewsreader;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button loadBtn;

    public static final String ACTIVITY_NAME = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tBar = findViewById(R.id.toolbar);
        tBar.setTitle(getString(R.string.main_activity));
        setSupportActionBar(tBar);

        DrawerLayout draw = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, draw, tBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        draw.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView activityTitle = (TextView) header.findViewById(R.id.activityTitle);
        activityTitle.setText(getString(R.string.app_name));
        TextView versionNumber = (TextView) header.findViewById(R.id.versionNumber);
        versionNumber.setText(getString(R.string.version_number));

        loadBtn = findViewById(R.id.loadButton);
        Intent newsListPage = new Intent(this, NewsListActivity.class);
        loadBtn.setOnClickListener((click) -> {
            startActivity(newsListPage);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String message = null;
        Intent switchPage = new Intent();

        switch (item.getItemId()) {
            case R.id.item_main:
                message = "Switch to " + getString(R.string.main_page);
                switchPage = new Intent(this, MainActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_news_list:
                message = "Switch to " + getString(R.string.news_list_page);
                switchPage = new Intent(this, NewsListActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_news_detail:
                message = "Switch to " + getString(R.string.news_detail_page);
                switchPage = new Intent(this, DetailActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_fav_list:
                message = "Switch to " + getString(R.string.fav_list_page);
                switchPage = new Intent(this, FavListActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_help:
                message = "Open " + getString(R.string.page_help);
                showDialog();
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.help_dialog_title)
                .setMessage(getString(R.string.help_dialog_content_main))
                .setPositiveButton(R.string.help_dialog_positive_button, (click, arg) -> { })
                .create()
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String message = null;
        Intent switchPage = new Intent();
        switch (item.getItemId()) {
            case R.id.item_main:
                message = "Switch to " + getString(R.string.main_page);
                switchPage = new Intent(this, MainActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_news_list:
                message = "Switch to " + getString(R.string.news_list_page);
                switchPage = new Intent(this, NewsListActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_news_detail:
                message = "Switch to " + getString(R.string.news_detail_page);
                switchPage = new Intent(this, DetailActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_fav_list:
                message = "Switch to " + getString(R.string.fav_list_page);
                switchPage = new Intent(this, FavListActivity.class);
                startActivity(switchPage);
                break;
            case R.id.item_help:
                message = "Open " + getString(R.string.page_help);
                showDialog();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
            Log.e(ACTIVITY_NAME, "In function: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(ACTIVITY_NAME, "In function: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(ACTIVITY_NAME, "In function: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(ACTIVITY_NAME, "In function: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(ACTIVITY_NAME, "In function: onDestroy");
    }
}