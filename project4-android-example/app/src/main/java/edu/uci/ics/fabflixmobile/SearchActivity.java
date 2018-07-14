package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

    }


    public void goToRed(View view) {

        Intent goToIntent = new Intent(this, LoginActivity.class);

        startActivity(goToIntent);
    }
    public void connectToTomcat(final View view) {

        final Map<String, String> params = new HashMap<String, String>();
        String search_term = ((EditText) findViewById(R.id.search_message)).getText().toString();
        params.put("title", search_term);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:8443/project2-push/api/android_search_result",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        goToBlue(view,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };
        queue.add(loginRequest);
        return;
    }

    public void goToBlue(View view,String message) {
        Intent goToIntent = new Intent(this, ListViewActivity.class);
        goToIntent.putExtra("search_message", message);

        startActivity(goToIntent);
    }

}
