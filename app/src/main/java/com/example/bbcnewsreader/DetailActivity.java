package com.example.bbcnewsreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    final String defaultTitle = "";
    final String defaultLink = "";
    final String defaultPubDate = "";
    final String defaultDescription = "";
    Bundle dataToPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dataToPass = getIntent().getExtras();

        SharedPreferences sharedPref = getSharedPreferences("newsDetail", Context.MODE_PRIVATE);
        String titleValue = sharedPref.getString("title", defaultTitle);
        String descriptionValue = sharedPref.getString("description", defaultDescription);
        String linkValue = sharedPref.getString("link", defaultLink);
        String pubDateValue = sharedPref.getString("pubDate", defaultPubDate);

        if(dataToPass == null && !titleValue.equals(defaultTitle)) {
            dataToPass = new Bundle();
            dataToPass.putString("title", titleValue);
            dataToPass.putString("description", descriptionValue);
            dataToPass.putString("link", linkValue);
            dataToPass.putString("pubDate", pubDateValue);
        }

        Toolbar tBar = findViewById(R.id.toolbar);
        tBar.setTitle(getString(R.string.news_detail_activity));
        setSupportActionBar(tBar);

        DetailFragment detailFragment = new DetailFragment();
        if (dataToPass != null) {
            detailFragment.setArguments(dataToPass);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, detailFragment)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPref = getSharedPreferences("newsDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("title", dataToPass.getString("title"));
        editor.putString("description", dataToPass.getString("description"));
        editor.putString("link", dataToPass.getString("link"));
        editor.putString("pubDate", dataToPass.getString("pubDate"));
        editor.commit();
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
                .setMessage(getString(R.string.help_dialog_content_news_detail))
                .setPositiveButton(R.string.help_dialog_positive_button, (click, arg) -> { })
                .create()
                .show();
    }
}