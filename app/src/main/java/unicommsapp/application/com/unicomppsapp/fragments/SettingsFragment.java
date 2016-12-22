package unicommsapp.application.com.unicomppsapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import unicommsapp.application.com.unicomppsapp.R;
import unicommsapp.application.com.unicomppsapp.Utils.UIUtils;
import unicommsapp.application.com.unicomppsapp.Utils.WebserviceGetUtil;
import unicommsapp.application.com.unicomppsapp.Utils.WebservicePostUtil;
import unicommsapp.application.com.unicomppsapp.Utils.WebservicePutUtil;
import unicommsapp.application.com.unicomppsapp.models.Contest;


public class SettingsFragment extends Fragment {

    private String updateSettingUrl = "http://capp.velho.xyz/api/settings/update/user/";
    private String userSettingResponseStr = "";
    private String updateSettingResponseStr = "";
    private static final String TAG = "ProfileFragment";
    private String updateSettingJson = "";
    private String idToken;
    private String idUser;

    private ProgressDialog progressDialog;

    private Switch announcementSwitch;

    private Handler updateSettingHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        announcementSwitch = (Switch)rootView.findViewById(R.id.switch_announcements);
        announcementSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = UIUtils.showDialogue(progressDialog, getContext());
                updateSettingUrl = updateSettingUrl.concat(idUser);

                Log.d(TAG, "update url is "+ updateSettingUrl);
                JSONObject tempJson = null;
                try{
                    tempJson = new JSONObject();

                    tempJson.put("setting", "1");
                    tempJson.put("status", announcementSwitch.isChecked());
                } catch( Exception e ) {
                    Toast.makeText(getActivity().getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                updateSettingJson = tempJson.toString();

                MyDownloadThread updateSettingDownloadThread = new MyDownloadThread();
                updateSettingDownloadThread.run();
            }
        });

        progressDialog = UIUtils.showDialogue(progressDialog, getContext());

        SharedPreferences prefs = getActivity().getSharedPreferences("com.unicomms.app", Context.MODE_PRIVATE);
        idToken = prefs.getString("id_token", null);
        idUser  = prefs.getString("id", null);

        new CheckUserTask().execute(idToken);

        return rootView;
    }


    public class CheckUserTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... idToken) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(getString(R.string.backend_url) + "/api/settings/check/user?id_token=" + idToken[0])
                            //.post(body)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                userSettingResponseStr = response.body().string();

                Log.d(TAG, userSettingResponseStr);
                return true;
            } catch (IOException e) {
                Log.d(TAG, e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                progressDialog.dismiss();

                try{
                    JSONArray jsonArray = new JSONArray(userSettingResponseStr);

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        int setting     = obj.getInt("setting");
                        boolean status  = obj.getBoolean("status");

                        if( setting==1 && status ) {
                            announcementSwitch.setChecked(true);
                        }
                    }
                } catch ( Exception e ) {
                    Toast.makeText(getActivity().getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "User settings fetching failed");
            }
        };
    }


    public class MyDownloadThread extends Thread {
        @Override
        public void run() {
            try {

                WebservicePutUtil wsUtil = new WebservicePutUtil();
                updateSettingResponseStr = wsUtil.run(updateSettingUrl, updateSettingJson);

                updateSettingHandler.post(new UserSettingsRunnable());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class UserSettingsRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            userSettingsDownloadComplete();
        }
    }

    private void userSettingsDownloadComplete() {
        progressDialog.dismiss();
        Toast.makeText(getActivity().getApplication().getApplicationContext(), updateSettingResponseStr, Toast.LENGTH_LONG).show();
    }

}
