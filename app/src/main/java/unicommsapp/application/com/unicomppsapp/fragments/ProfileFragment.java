package unicommsapp.application.com.unicomppsapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import unicommsapp.application.com.unicomppsapp.MainActivity;
import unicommsapp.application.com.unicomppsapp.R;


public class ProfileFragment extends Fragment {

    private TextView textView2;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "ProfileFragment";


    private String userName = "";
    private String userCoins = "0";
    private String userEmail = "";
    private String userGroup = "Community Member";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("com.unicomms.app", Context.MODE_PRIVATE);
        userName = prefs.getString("name", null);
        String idToken = prefs.getString("id_token", null);

        TextView text = (TextView) rootView.findViewById(R.id.profile_name);
        text.setText(userName);

        new GetUserProfileTask().execute(idToken);

        return rootView;
    }

    public class GetUserProfileTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... idToken) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(getString(R.string.backend_url) + "/api/profile/full?id_token=" + idToken[0])
                            //.post(body)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String responseData = response.body().string();
                JSONObject json = new JSONObject(responseData);

                userCoins = json.getString("coins");
                userEmail = json.getString("email");



                Log.d(TAG, responseData);
                return true;
            } catch (IOException e) {
                Log.d(TAG, e.toString());
                return false;
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(TAG, "Profile fetched succesfully");

                TextView coinsAmount = (TextView) getView().getRootView().findViewById(R.id.coins_amount);
                coinsAmount.setText(userCoins);
                TextView email = (TextView) getView().getRootView().findViewById(R.id.profile_email);
                email.setText(userEmail);
                TextView group = (TextView) getView().getRootView().findViewById(R.id.profile_group);
                group.setText(userGroup);
            } else {
                Log.d(TAG, "Profile fetching failed");
            }
        };
    }


}
