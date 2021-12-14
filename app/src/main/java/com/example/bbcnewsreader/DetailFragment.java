package com.example.bbcnewsreader;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {

    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "pubdate";
    private static final String ARG_PARAM3 = "link";
    private static final String ARG_PARAM4 = "description";

    private boolean isTablet;
    private String title;
    private String pubdate;
    private String link;
    private String description;

    public DetailFragment() {
        // Required empty public constructor
    }

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_detail, container, false);

        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            pubdate = getArguments().getString(ARG_PARAM2);
            link = getArguments().getString(ARG_PARAM3);
            description = getArguments().getString(ARG_PARAM4);

            TextView detailTitle = result.findViewById(R.id.detailTitle);
            TextView detailPubdate = result.findViewById(R.id.detailPubdate);
            TextView detailDescription = result.findViewById(R.id.detailDescription);

            detailTitle.setClickable(true);
            detailTitle.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='"+link+"'> "+title+" </a>";
            detailTitle.setText(Html.fromHtml(text));
            detailPubdate.setText(pubdate);
            detailDescription.setText(description);
        }

//        Button hideBtn = result.findViewById(R.id.hideButton);
//        hideBtn.setOnClickListener(click -> {
//            if (isTablet) {
//                ChatRoomActivity parent = (ChatRoomActivity)getActivity();
//                parent.getSupportFragmentManager()
//                        .beginTransaction()
//                        .remove(this)
//                        .commit();
//            } else {
//                EmptyActivity parent = (EmptyActivity)getActivity();
//                Intent backToChatRoomActivity = new Intent();
//                parent.setResult(Activity.RESULT_OK, backToChatRoomActivity);
//                parent.finish();
//            }
//        });

        return result;
    }
}