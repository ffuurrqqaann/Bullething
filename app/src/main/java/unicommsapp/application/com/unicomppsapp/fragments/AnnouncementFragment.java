package unicommsapp.application.com.unicomppsapp.fragments;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import unicommsapp.application.com.unicomppsapp.R;
import unicommsapp.application.com.unicomppsapp.Utils.DateUtil;
import unicommsapp.application.com.unicomppsapp.Utils.UIUtils;
import unicommsapp.application.com.unicomppsapp.Utils.WebserviceGetUtil;
import unicommsapp.application.com.unicomppsapp.Utils.WebservicePostUtil;
import unicommsapp.application.com.unicomppsapp.adapter.AnnouncementAdapter;
import unicommsapp.application.com.unicomppsapp.models.Announcements;
import unicommsapp.application.com.unicomppsapp.models.Contest;


public class AnnouncementFragment extends ListFragment {

    private String announcementUrl;
    private String responseStr = "";

    private ProgressDialog dialogue;

    private Handler announcementHandler = new Handler();

    private ListView listView;

    private Bundle bundle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_announcement, container, false);

        this.bundle = savedInstanceState;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences prefs = getActivity().getSharedPreferences("com.unicomms.app", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);

        announcementUrl =  "http://capp.velho.xyz/api/announcements/user/"+userId;

        Log.d("url", announcementUrl);

        dialogue = UIUtils.showDialogue(dialogue, getContext());

        MyDownloadThread myDownloadThread = new MyDownloadThread();
        myDownloadThread.run();

        return rootView;
    }

    public class MyDownloadThread extends Thread {
        @Override
        public void run() {
            try {
                WebserviceGetUtil wsUtil = new WebserviceGetUtil();
                responseStr = wsUtil.run(announcementUrl);

                Log.d("response str", responseStr);

                announcementHandler.post(new AnnouncementRunnable());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AnnouncementRunnable implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            AnnouncementDownloadComplete();
            dialogue.dismiss();
        }
    }

    private void AnnouncementDownloadComplete() {
        List<Announcements> announcements = getAnnouncementsList();

        this.listView = getListView();
        this.listView.setFastScrollEnabled(true);

        final AnnouncementAdapter adapter = new AnnouncementAdapter(getActivity().getApplication().getApplicationContext(), announcements);
        this.listView.setAdapter(adapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Show contest view popup
                final View announcementPopupView = getLayoutInflater(bundle).inflate(R.layout.announcement_popup_view, null);

                final PopupWindow announcementViewPopupWindow = new PopupWindow(announcementPopupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                announcementViewPopupWindow.setFocusable(true);

                // If you need the PopupWindow to dismiss when when touched outside
                announcementViewPopupWindow.setBackgroundDrawable(new ColorDrawable());
                announcementViewPopupWindow.showAtLocation(announcementPopupView, Gravity.CENTER, 0, 0);

                final TextView announcementTitle        = (TextView) announcementPopupView.findViewById(R.id.announcementViewTitle);
                final TextView announcementDescription  = (TextView) announcementPopupView.findViewById(R.id.announcementViewMessage);
                final TextView announcementPublishDate  = (TextView) announcementPopupView.findViewById(R.id.announcementPublishDate);
                final TextView announcementExpireDate   = (TextView) announcementPopupView.findViewById(R.id.announcementExpireDate);

                final Button cancel = (Button) announcementPopupView.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        announcementViewPopupWindow.dismiss();
                    }
                });

                Announcements tempAnnouncement = adapter.getItem(position);

                announcementTitle.setText(tempAnnouncement.getTitle());
                announcementDescription.setText(tempAnnouncement.getMessage());
                announcementPublishDate.setText(tempAnnouncement.getPublish());
                announcementExpireDate.setText(tempAnnouncement.getExpire());
            }
        });
    }

    private List<Announcements> getAnnouncementsList () {

        List<Announcements> tempAnnouncementsList = new ArrayList<Announcements>();

        try{
            JSONArray jsonArray = new JSONArray(responseStr);

            tempAnnouncementsList = new ArrayList<Announcements>();

            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Announcements announcement = new Announcements();

                announcement.setId(obj.getInt("id"));
                announcement.setTitle(obj.getString("title"));
                announcement.setMessage(obj.getString("message"));
                announcement.setPublish(obj.getString("publish"));
                announcement.setExpire(obj.getString("expire"));

                tempAnnouncementsList.add(announcement);
            }
        } catch ( Exception e ) {
            Toast.makeText(getActivity().getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return tempAnnouncementsList;
    }

}
