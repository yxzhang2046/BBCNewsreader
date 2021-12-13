package com.example.bbcnewsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavListActivity extends AppCompatActivity {
    private ListView favList;
    private FavListAdapter favAdapter;
    private List<News> favs = new ArrayList<News>();
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list);

        favAdapter = new FavListAdapter();
        favList = findViewById(R.id.favList);
        favList.setAdapter(favAdapter);

        loadDataFromDatabase();

        Intent data = getIntent();
        Bundle bundle = data.getExtras();
        String title = bundle.getString("title");
        String description = bundle.getString("description");
        String link = bundle.getString("link");
        long pubDate = bundle.getLong("pubDate");
        addNews(title, description, link, pubDate);

//        favs.add(new News( 1,"123456789 123456789 123 456 789 123 456 789 123 456 789", "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 ", "https://github.com/yxzhang2046/BBCNewsreader", "sdf, 12 Dec 2021"));
//        favs.add(new News(2,"title2", "des2", "https://github.com/yxzhang2046/BBCNewsreader", "sdf, 12 Dec 2021"));
//        favs.add(new News(3,"title3", "des3", "https://github.com/yxzhang2046/BBCNewsreader", "sdf, 12 Dec 2021"));

        SwipeRefreshLayout refresher = findViewById(R.id.favRefresher);
        refresher.setOnRefreshListener( () -> refresher.setRefreshing(false) );

        favAdapter.notifyDataSetChanged();
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
            Date pubdate = new Date(results.getLong(pubdateColumnIndex));

            News news = new News(id, titleTxt, descriptionTxt, linkTxt, pubdate);
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

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            favDate.setText(sdf.format(currentNews.getPubDate()));
            favDescription.setText(currentNews.getDescription());

            favEmpty.setOnClickListener( (click) -> {
                removeNews(position, currentNews.getId());
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