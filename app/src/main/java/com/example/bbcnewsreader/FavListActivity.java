package com.example.bbcnewsreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FavListActivity extends AppCompatActivity {
    private ListView favList;
    private FavListAdapter favAdapter;
    private List<News> favs = new ArrayList<News>();
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list);

        Toolbar tBar = findViewById(R.id.toolbar);
        tBar.setTitle(getString(R.string.fav_list_activity));
        setSupportActionBar(tBar);

        favAdapter = new FavListAdapter();
        favList = findViewById(R.id.favList);
        favList.setAdapter(favAdapter);

        isTablet = findViewById(R.id.fragmentLocation) != null;

        loadDataFromDatabase();

        Intent data = getIntent();
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            String title = bundle.getString("title");
            String description = bundle.getString("description");
            String link = bundle.getString("link");
            long pubDate = bundle.getLong("pubDate");
            addNews(title, description, link, pubDate);
        }

        SwipeRefreshLayout refresher = findViewById(R.id.favRefresher);
        refresher.setOnRefreshListener( () -> refresher.setRefreshing(false) );

        favAdapter.notifyDataSetChanged();
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
            .setMessage(getString(R.string.help_dialog_content_fav_list))
            .setPositiveButton(R.string.help_dialog_positive_button, (click, arg) -> { })
            .create()
            .show();
    }

    private void loadDataFromDatabase() {
        dbOpenHelper = new DBOpenHelper(this);
        db = dbOpenHelper.getWritableDatabase();

        String [] columns = {DBOpenHelper.COL_ID, DBOpenHelper.COL_TITLE, DBOpenHelper.COL_DESCRIPTION, DBOpenHelper.COL_LINK, DBOpenHelper.COL_PUBDATE};
        Cursor results = db.query(false, DBOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        int idColumnIndex = results.getColumnIndex(DBOpenHelper.COL_ID);
        int titleColumnIndex = results.getColumnIndex(DBOpenHelper.COL_TITLE);
        int descriptionColumnIndex = results.getColumnIndex(DBOpenHelper.COL_DESCRIPTION);
        int linkColumnIndex = results.getColumnIndex(DBOpenHelper.COL_LINK);
        int pubdateColumnIndex = results.getColumnIndex(DBOpenHelper.COL_PUBDATE);

        results.moveToFirst();
        while (results.moveToNext()) {
            long id = results.getLong(idColumnIndex);
            String titleTxt = results.getString(titleColumnIndex);
            String descriptionTxt = results.getString(descriptionColumnIndex);
            String linkTxt = results.getString(linkColumnIndex);
            Date pubDate = new Date(results.getLong(pubdateColumnIndex));

            News news = new News(id, titleTxt, descriptionTxt, linkTxt, pubDate);
            favs.add(news);
        }
    }

    private void addNews(String title, String description, String link, long pubDate) {
        boolean checkSame = true;
        for (News item : favs) {
            if (title.equals(item.getTitle())) {
                checkSame = false;
            }
        }
        if (checkSame) {
            long id = 0;
            ContentValues cValues = new ContentValues();
            cValues.put(DBOpenHelper.COL_TITLE, title);
            cValues.put(DBOpenHelper.COL_DESCRIPTION, description);
            cValues.put(DBOpenHelper.COL_LINK, link);
            cValues.put(DBOpenHelper.COL_PUBDATE, pubDate);
            id = db.insert(DBOpenHelper.TABLE_NAME, null, cValues);

            News news = new News(id, title, description, link, new Date(pubDate));
            favs.add(news);
        }
    }

    private void removeNews(int pos, long id){
        favs.remove(pos);
        db.delete(DBOpenHelper.TABLE_NAME, DBOpenHelper.COL_ID + "= ?", new String[] {Long.toString(id)});
        favAdapter.notifyDataSetChanged();
        Toast.makeText(this, getText(R.string.fav_remove_action), Toast.LENGTH_SHORT).show();
    }

    private class FavListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (favs.isEmpty()) {
                return 0;
            }
            return favs.size();
        }

        @Override
        public News getItem(int position) {
            return (News) favs.get(position);
        }

        @Override
        public long getItemId(int position) {
            long id = getItem(position).getId();
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView = convertView;
            LayoutInflater inflater = getLayoutInflater();
            News currentNews = getItem(position);

            newView = inflater.inflate(R.layout.fav_item_layout, parent, false);
            ImageView favEmpty = newView.findViewById(R.id.favEmpty);
            TextView favTitle = newView.findViewById(R.id.favTitle);
            TextView favDate = newView.findViewById(R.id.favDate);
            TextView favDescription = newView.findViewById(R.id.favDescription);

            favTitle.setClickable(true);
            favTitle.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='"+currentNews.getLink()+"'> "+currentNews.getTitle()+" </a>";
            favTitle.setText(Html.fromHtml(text));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            favDate.setText(sdf.format(currentNews.getPubDate()));
            favDescription.setText(currentNews.getDescription());

            favEmpty.setOnClickListener( (click) -> {
                removeNews(position, currentNews.getId());
            });

            Intent nextActivity = new Intent(getApplicationContext(), DetailActivity.class);
            favDescription.setOnClickListener( (click) -> {
                SimpleDateFormat originSdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                String originDateTxt = originSdf.format(currentNews.getPubDate());
                Bundle dataToPass = new Bundle();
                dataToPass.putString("title", currentNews.getTitle());
                dataToPass.putString("pubDate", originDateTxt);
                dataToPass.putString("link", currentNews.getLink());
                dataToPass.putString("description", currentNews.getDescription());

                if (isTablet) {
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(dataToPass);
                    detailFragment.setTablet(isTablet);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, detailFragment)
                            .commit();
                } else {
                    nextActivity.putExtras(dataToPass);
                    startActivity(nextActivity);
                }
            });

            return newView; 
        }

        private String generateDate(String datetime) {
            String result = "";
            String[] arrOfDate = datetime.split(",");
            if (arrOfDate.length > 1) {
                result = arrOfDate[1].substring(1, 12);
            }
            return result;
        }
    }
}