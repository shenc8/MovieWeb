package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        Toast.makeText(this, "Welcome to Fablix", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_red, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectToTomcat(final View view) {

        // Post request form data
        final Map<String, String> params = new HashMap<String, String>();
        String username = ((EditText) findViewById(R.id.user_message)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_message)).getText().toString();
        params.put("user", username);
        params.put("password", password);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:8443/project2-push/api/android-login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {
                            result = new JSONObject(response);
                            Log.d("response", response);
                            if (result.get("status").equals("fail"))
                            {((TextView) findViewById(R.id.http_response)).setText("Login Information Incorrect");}
                            else
                            {
                                goToNext(view);
                            }

                        }catch (JSONException e)
                        {
                            Log.d("error message",e.getMessage());
                        }

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


    public void goToNext(View view) {
        Intent goToIntent = new Intent(this, SearchActivity.class);
        startActivity(goToIntent);
    }

}
