package unicommsapp.application.com.unicomppsapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import unicommsapp.application.com.unicomppsapp.R;
import unicommsapp.application.com.unicomppsapp.Utils.UIUtils;
import unicommsapp.application.com.unicomppsapp.Utils.WebserviceGetUtil;
import unicommsapp.application.com.unicomppsapp.Utils.WebservicePostUtil;
import unicommsapp.application.com.unicomppsapp.adapter.CategoryAdapter;
import unicommsapp.application.com.unicomppsapp.adapter.CategorySpinnerAdapter;
import unicommsapp.application.com.unicomppsapp.adapter.ContestAdapter;
import unicommsapp.application.com.unicomppsapp.models.Category;
import unicommsapp.application.com.unicomppsapp.models.Contest;


public class ContestFragment extends ListFragment {

    private ProgressDialog dialogue;
    private ProgressDialog contestDialogue;

    private String contestUrl = "http://capp.velho.xyz/api/contest/all";
    private String newContestUrl = "http://capp.velho.xyz/api/contest/new";
    private String voteUrl = "http://capp.velho.xyz/api/vote/new";
    private String newContestJson = "";
    private String voteJson = "";
    private String newContestTitle="";
    private String newContestDescription="";
    private String newContestStatus = "";
    private String newContestCreatedDate = "";
    private String newContestUpdateDated = "";
    private String rating = "";
    private String responseStr = "";
    private String newContestResponseStr = "";
    private String voteResponseStr = "";
    private String userId = "";

    private ListView listView;
    private List<Category> categoryList;
    private List<Category> tempCategoryList = new ArrayList<Category>();
    private List<Contest> contestList;

    private CategoryAdapter categoryAdapter;
    private ContestAdapter  contestAdapter;

    private int currentContestId = 0;
    private int newContestCategory;
    private int newContestCtgId = 0;

    private Handler wsHandler;
    private Handler newContestHandler;
    private Handler voteHandler = new Handler();

    private Bundle bundle;

    private PopupWindow popupWindow;
    private PopupWindow contestViewPopupWindow;

    private ImageButton veryPoorImgBtn;
    private ImageButton poorImgBtn;
    private ImageButton veryGoodImgBtn;
    private ImageButton goodImgBtn;
    private ImageButton excellentImgBtn;

    private Contest newContest = new Contest();

    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_contest, container, false);
        this.bundle = savedInstanceState;

        prefs = getActivity().getSharedPreferences("com.unicomms.app", Context.MODE_PRIVATE);
        userId = prefs.getString("id", null);

        Button contestBtn = (Button)rootView.findViewById(R.id.addContest);
        contestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View popupView = getLayoutInflater(savedInstanceState).inflate(R.layout.contest_popup, null);
                popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // If the PopupWindow should be focusable
                popupWindow.setFocusable(true);

                // If you need the PopupWindow to dismiss when when touched outside
                popupWindow.setBackgroundDrawable(new ColorDrawable());

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                //Defining the contest title edit text.
                final EditText contestTitle = (EditText) popupView.findViewById(R.id.contestTitle);

                //Defining the contest description.
                final EditText contestDescription = (EditText) popupView.findViewById(R.id.contestDescription);

                //done creating contest.
                Button btnDone = (Button) popupView.findViewById(R.id.done);
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //getting values from the controls.
                        newContestTitle = contestTitle.getText().toString();
                        newContestDescription = contestDescription.getText().toString();
                        newContestStatus = "true";

                        //preparing json string.
                        JSONObject json = null;

                        try {
                            //creating the date.
                            TimeZone tz = TimeZone.getTimeZone("UTC");
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
                            df.setTimeZone(tz);
                            newContestCreatedDate = df.format(new Date());
                            newContestUpdateDated = newContestCreatedDate;

                            json = new JSONObject();

                            json.put("title", newContestTitle);
                            json.put("description", newContestDescription);
                            json.put("creator", userId);
                            json.put("category", "1");
                            json.put("status", "true");
                            json.put("created", newContestCreatedDate);
                            json.put("updated", newContestUpdateDated);

                        } catch (Exception e) {
                            Toast.makeText(getActivity().getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        newContestJson = json.toString();

                        newContestHandler = new Handler();

                        //show loader.
                        contestDialogue = UIUtils.showDialogue(contestDialogue, getContext());

                        NewContestDownloadThread newContestDownloadThread = new NewContestDownloadThread();
                        newContestDownloadThread.start();
                    }
                });
            }
        });

        wsHandler = new Handler();

        //show loader.
        dialogue = UIUtils.showDialogue(dialogue, getContext());

        MyDownloadThread downloadThread = new MyDownloadThread();
        downloadThread.start();

        return rootView;
    }

    public class MyDownloadThread extends Thread {
        @Override
        public void run() {
            try {
                WebserviceGetUtil wsUtil = new WebserviceGetUtil();
                responseStr = wsUtil.run(contestUrl);

                wsHandler.post(new MyRunnable());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class NewContestDownloadThread extends Thread {
        @Override
        public void run() {
            try {
                WebservicePostUtil wsUtil = new WebservicePostUtil();
                newContestResponseStr = wsUtil.run(newContestUrl, newContestJson, false);

                newContestHandler.post(new NewContestRunnable());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class VoteThread extends Thread {
        @Override
        public void run() {
            try {
                WebservicePostUtil wsUtil = new WebservicePostUtil();
                voteResponseStr = wsUtil.run(voteUrl, voteJson, false);

                voteHandler.post(new VoteRunnable());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            downloadComplete();
            dialogue.dismiss();
        }
    }

    private class NewContestRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            newContestDownloadComplete();
            contestDialogue.dismiss();
        }
    }

    private class VoteRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            voteDownloadComplete();
            contestDialogue.dismiss();
        }
    }

    private void downloadComplete() {
        try {
            contestList = getContestList();

            this.listView = getListView();
            this.listView.setFastScrollEnabled(true);

            this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Show contest view popup
                    final View contestPopupView = getLayoutInflater(bundle).inflate(R.layout.contest_popup_view, null);

                    contestViewPopupWindow = new PopupWindow(contestPopupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    contestViewPopupWindow.setFocusable(true);

                    // If you need the PopupWindow to dismiss when when touched outside
                    contestViewPopupWindow.setBackgroundDrawable(new ColorDrawable());
                    contestViewPopupWindow.showAtLocation(contestPopupView, Gravity.CENTER, 0, 0);

                    final TextView cVText = (TextView)contestPopupView.findViewById(R.id.contestViewTitle);
                    final TextView cVDescription = (TextView)contestPopupView.findViewById(R.id.contestViewDescription);

                    //initializing the image buttons.
                    veryPoorImgBtn    = (ImageButton)contestPopupView.findViewById(R.id.very_poor);
                    poorImgBtn        = (ImageButton)contestPopupView.findViewById(R.id.poor);
                    veryGoodImgBtn    = (ImageButton)contestPopupView.findViewById(R.id.good);
                    goodImgBtn        = (ImageButton)contestPopupView.findViewById(R.id.very_good);
                    excellentImgBtn   = (ImageButton)contestPopupView.findViewById(R.id.excellent);

                    veryPoorImgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            veryPoorImgBtn.setImageResource( R.drawable.very_poor_pressed );

                            switch( rating ){

                                case "2":
                                    poorImgBtn.setImageResource(R.drawable.poor);
                                    break;
                                case "3":
                                    goodImgBtn.setImageResource(R.drawable.good);
                                    break;
                                case "4":
                                    veryGoodImgBtn.setImageResource(R.drawable.very_good);
                                    break;
                                case "5":
                                    excellentImgBtn.setImageResource(R.drawable.excellent);
                                    break;
                            }
                            rating = "1";
                        }
                    });

                    poorImgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            poorImgBtn.setImageResource( R.drawable.poor_pressed );
                            switch( rating ){

                                case "1":
                                    veryPoorImgBtn.setImageResource(R.drawable.very_poor);
                                    break;
                                case "3":
                                    goodImgBtn.setImageResource(R.drawable.good);
                                    break;
                                case "4":
                                    veryGoodImgBtn.setImageResource(R.drawable.very_good);
                                    break;
                                case "5":
                                    excellentImgBtn.setImageResource(R.drawable.excellent);
                                    break;
                            }
                            rating = "2";
                        }
                    });

                    goodImgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goodImgBtn.setImageResource( R.drawable.good_pressed );
                            switch( rating ){
                                case "1":
                                    veryPoorImgBtn.setImageResource(R.drawable.very_poor);
                                    break;
                                case "2":
                                    poorImgBtn.setImageResource(R.drawable.poor);
                                    break;
                                case "4":
                                    veryGoodImgBtn.setImageResource(R.drawable.very_good);
                                    break;
                                case "5":
                                    excellentImgBtn.setImageResource(R.drawable.excellent);
                                    break;
                            }
                            rating = "3";
                        }
                    });

                    veryGoodImgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            veryGoodImgBtn.setImageResource( R.drawable.very_good_pressed );
                            switch( rating ){
                                case "1":
                                    veryPoorImgBtn.setImageResource(R.drawable.very_poor);
                                    break;
                                case "2":
                                    poorImgBtn.setImageResource(R.drawable.poor);
                                    break;
                                case "3":
                                    goodImgBtn.setImageResource(R.drawable.good);
                                    break;
                                case "5":
                                    excellentImgBtn.setImageResource(R.drawable.excellent);
                                    break;
                            }
                            rating = "4";
                        }
                    });

                    excellentImgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            excellentImgBtn.setImageResource( R.drawable.excellent_pressed );
                            switch( rating ){
                                case "1":
                                    veryPoorImgBtn.setImageResource(R.drawable.very_poor);
                                    break;
                                case "2":
                                    poorImgBtn.setImageResource(R.drawable.poor);
                                    break;
                                case "3":
                                    goodImgBtn.setImageResource(R.drawable.good);
                                    break;
                                case "4":
                                    veryGoodImgBtn.setImageResource(R.drawable.very_good);
                                    break;
                            }
                            rating = "5";
                        }
                    });

                    Button submitBtn = (Button) contestPopupView.findViewById(R.id.contestSubmit);

                    submitBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JSONObject tempVoteJson = null;
                            try{
                                tempVoteJson = new JSONObject();

                                tempVoteJson.put("user_id", userId);
                                tempVoteJson.put("contest", currentContestId);
                                tempVoteJson.put("rating", rating);
                            } catch( Exception e ) {
                                Toast.makeText(getActivity().getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            voteJson = tempVoteJson.toString();

                            contestDialogue = UIUtils.showDialogue(contestDialogue, getContext());

                            VoteThread voteThread = new VoteThread();
                            voteThread.start();
                        }
                    });

                    Contest contest = (Contest) contestAdapter.getItem(position);
                    List<Contest> searchContestList = getContestList();

                    for (int i=0; i<searchContestList.size(); i++) {
                        if (searchContestList.get(i).getId()==contest.getId() ) {
                            //get contest list.
                            Contest temp = searchContestList.get(i);

                            currentContestId = temp.getId();
                            cVText.setText(temp.getTitle());
                            cVDescription.setText(temp.getDescription());
                        }
                    }
                }
            });

            contestAdapter = new ContestAdapter(getContext(), contestList);
            setListAdapter(contestAdapter);

        } catch (Exception e) {
            Toast.makeText(getActivity().getApplication().getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void newContestDownloadComplete() {
        Toast.makeText(getActivity().getApplication().getApplicationContext(), "New Contest added id "+newContestResponseStr, Toast.LENGTH_LONG).show();

        try{
            JSONArray jsonArray = new JSONArray(newContestResponseStr);

            int id = jsonArray.getJSONObject(0).getInt("id");

            newContest.setId(id);
            newContest.setTitle(newContestTitle);
            newContest.setDescription(newContestDescription);
            newContest.setStatus(true);
            newContest.setCreated(userId);
            newContest.setCreated(newContestCreatedDate);
            newContest.setUpdated(newContestUpdateDated);

        } catch (Exception e) {
            Toast.makeText(getActivity().getApplication().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

            this.contestAdapter.add(newContest);
            synchronized (this.contestAdapter) {
                this.contestAdapter.notifyDataSetChanged();
            }

        popupWindow.dismiss();
    }

    private void voteDownloadComplete() {
        Toast.makeText(getActivity().getApplication().getApplicationContext(), "Your vote has been recorded "+voteResponseStr, Toast.LENGTH_LONG).show();
        contestViewPopupWindow.dismiss();

    }

    private List<Contest> getContestList () {

        List<Contest> tempContestList = new ArrayList<Contest>();

        try{
            JSONArray jsonArray = new JSONArray(responseStr);

            tempContestList = new ArrayList<Contest>();

            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Contest contest = new Contest();

                contest.setId(obj.getInt("id"));
                contest.setTitle(obj.getString("title"));
                contest.setCategory(obj.getInt("category"));
                contest.setDescription(obj.getString("description"));
                contest.setCreator(obj.getInt("creator"));
                contest.setStatus(obj.getBoolean("status"));
                contest.setCreated(obj.getString("created"));
                contest.setUpdated(obj.getString("updated"));

                tempContestList.add(contest);
            }
        } catch ( Exception e ) {
            Toast.makeText(getActivity().getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return tempContestList;
    }
}
