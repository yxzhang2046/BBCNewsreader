package com.example.bbcnewsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsListActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ListView newsList;
    private NewsListAdapter newsAdapter;
    private List<News> news = new ArrayList<News>();

    private final int FAV_NEWS_REQUEST = 501;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        newsAdapter = new NewsListAdapter();
        newsList = findViewById(R.id.newsList);
        newsList.setAdapter(newsAdapter);

        BBCNewsQuery bbsNewsQuery = new BBCNewsQuery();
        bbsNewsQuery.execute("http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");

        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener( () -> refresher.setRefreshing(false) );
    }

    private class BBCNewsQuery extends AsyncTask<String, Integer, String> {
        private final String XML_FIRST_TAG = "rss";
        private final String XML_SECOND_TAG = "channel";
        private final String XML_NEWS_TAG = "item";

        @Override
        protected String doInBackground(String... args) {

            try {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                publishProgress(25);

                if (args[0].contains("xml")) {
                    //Reading XML
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    xpp.setInput(response, "UTF-8");
                    xpp.nextTag();
                    publishProgress(50);
                    news = readFeed(xpp);
                    publishProgress(100);
                }
            }
            catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";
        }

        // Find the item from rss.channel in the feed
        private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
            List news = new ArrayList();

            parser.require(XmlPullParser.START_TAG, null, XML_FIRST_TAG);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(XML_SECOND_TAG)) {
                    parser.require(XmlPullParser.START_TAG, null, XML_SECOND_TAG);
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        String itemName = parser.getName();
                        // Starts by looking for the item tag
                        if (itemName.equals(XML_NEWS_TAG)) {
                            news.add(readNews(parser));
                        } else {
                            skip(parser);
                        }
                    }
                }
            }
            return news;
        }

        // Parses the contents of a news.
        private News readNews(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, XML_NEWS_TAG);
            String title = null;
            String description = null;
            String link = null;
            String pubDate = null;
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("title")) {
                    title = readTitle(parser);
                } else if (name.equals("description")) {
                    description = readDescription(parser);
                } else if (name.equals("link")) {
                    link = readLink(parser);
                } else if (name.equals("pubDate")) {
                    pubDate = readPubDate(parser);
                } else {
                    skip(parser);
                }
            }
            return new News(title, description, link, generateDate(pubDate));
        }

        private Date generateDate(String datetime) {
            String formatStr = "EEE, d MMM yyyy HH:mm:ss z";
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr, Locale.getDefault());
            ParsePosition pp1 = new ParsePosition(0);
            Date date = formatter.parse(datetime, pp1);

            return date;
        }

        // Processes title tags in the feed.
        private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "title");
            String title = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "title");
            return title;
        }

        // Processes description tags in the feed.
        private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "description");
            String description = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "description");
            return description;
        }

        // Processes link tags in the feed.
        private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "link");
            String link = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "link");
            return link;
        }

        // Processes pubDate tags in the feed.
        private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "pubDate");
            String pubDate = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "pubDate");
            return pubDate;
        }

        // For the tags title, description, link and pubDate, extracts their text values.
        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }

        //Skip the tags we don't need
        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            Log.i("Progress Update", values[0].toString());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("Post Execute", s);
            if(news.size() > 0) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            newsAdapter.notifyDataSetChanged();
        }
    }

    private class NewsListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (news.isEmpty()) {
                return 0;
            }
            return news.size();
        }

        @Override
        public News getItem(int position) {
            return (News) news.get(position);
        }

        @Override
        public long getItemId(int position) {
            long id = position;
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView = convertView;
            LayoutInflater inflater = getLayoutInflater();
            News currentNews = getItem(position);

            newView = inflater.inflate(R.layout.news_item_layout, parent, false);
            ImageView newsFav = newView.findViewById(R.id.newsFav);
            TextView newsTitle = newView.findViewById(R.id.newsTitle);
            TextView newsDate = newView.findViewById(R.id.newsDate);
            TextView newsDescription = newView.findViewById(R.id.newsDescription);

            newsTitle.setClickable(true);
            newsTitle.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='"+currentNews.getLink()+"'> "+currentNews.getTitle()+" </a>";
            newsTitle.setText(Html.fromHtml(text));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String newsDateTxt = sdf.format(currentNews.getPubDate());
            long newsDateStamp = currentNews.getPubDate().getTime();
            newsDate.setText(newsDateTxt);
            newsDescription.setText(currentNews.getDescription());

            Intent favListPage = new Intent(getApplicationContext(), FavListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("title", currentNews.getTitle());
            bundle.putString("description", currentNews.getDescription());
            bundle.putString("link", currentNews.getLink());
            bundle.putString("pubDate", newsDateTxt);
            bundle.putLong("pubDate", newsDateStamp);
            favListPage.putExtras(bundle);
            newsFav.setOnClickListener( (click) -> {
                startActivity(favListPage);
            });

            return newView;
        }

    }
}