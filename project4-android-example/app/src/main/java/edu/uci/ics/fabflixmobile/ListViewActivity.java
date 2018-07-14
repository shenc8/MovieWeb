package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListViewActivity extends ActionBarActivity {

    private int current_page = 1;
    private int number_movies = 5;
    private final ArrayList<Movie> movies = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String results = bundle.getString("search_message");
        Button previous = (Button)findViewById(R.id.previous);
        Button next = (Button)findViewById(R.id.next);
        Log.d("result",results);
        JSONArray info_list = null;
        try {
            info_list = new JSONArray(results);
            for (int i =0;i<info_list.length();i++)
            {
                JSONObject movie_info = info_list.getJSONObject(i);
                String title = movie_info.getString("title");
                Integer year = movie_info.getInt("year");
                String director = movie_info.getString("director");
                String genres = movie_info.getString("genres_list");
                String stars = movie_info.getString("stars_name");
                movies.add(new Movie(title,year,director,genres,stars));
            }
        }catch (JSONException e)
        {
            Log.d("Error Message",e.getMessage());
        }
        int value = number_movies;
        ArrayList<Movie> new_movies = new ArrayList<>();
        if (movies.size()<number_movies)
        {
            value = movies.size();
        }
        int num = current_page-1;
        for (int i=num*number_movies;i<value;i++)
        {
            new_movies.add(movies.get(i));
        }
        setContentView(R.layout.activity_listview);
        MovieListViewAdapter adapter = new MovieListViewAdapter(new_movies, this);

        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Go to Single Movie Page: %s", movie.getTitle());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                goToNext(view,new Movie(movie.getTitle(),movie.getYear(),movie.getDirector(),movie.getGenres(),movie.getStars()));
            }
        });

    }

    public void goToNext(View view, Movie movie) {
        Intent goToIntent = new Intent(this, SingleMovieActivity.class);
        goToIntent.putExtra("title", movie.getTitle());
        goToIntent.putExtra("year", movie.getYear());
        goToIntent.putExtra("director", movie.getDirector());
        goToIntent.putExtra("genres",movie.getGenres());
        goToIntent.putExtra("stars",movie.getStars());
        startActivity(goToIntent);
    }
    public void previous_page(View view)
    {

        if (current_page>1)
        {
            setContentView(R.layout.activity_listview);
            current_page-=1;
            int num = current_page-1;
            ArrayList<Movie> new_movies = new ArrayList<>();
            for (int i=num*number_movies;i<num*number_movies+number_movies;i++)
            {
                new_movies.add(movies.get(i));
            }
            MovieListViewAdapter adapter = new MovieListViewAdapter(new_movies, this);
            ListView listView = (ListView)findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie = movies.get(position);
                    String message = String.format("Go to Single Movie Page: %s", movie.getTitle());
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    goToNext(view,new Movie(movie.getTitle(),movie.getYear(),movie.getDirector(),movie.getGenres(),movie.getStars()));
                }
            });
        }
    }
    public void next_page(View view)
    {

        if (current_page*number_movies<movies.size())
        {
            setContentView(R.layout.activity_listview);
            current_page+=1;
            int num = current_page-1;
            ArrayList<Movie> new_movies = new ArrayList<>();
            int value = movies.size()-num*number_movies;
            if (value>number_movies)
            {
                value = num*number_movies+number_movies;
            }
            else
            {
                value +=num*number_movies;
            }
            for (int i=num*number_movies;i<value;i++)
            {
                new_movies.add(movies.get(i));
            }
            MovieListViewAdapter adapter = new MovieListViewAdapter(new_movies, this);
            ListView listView = (ListView)findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie = movies.get(position);
                    String message = String.format("Go to Single Movie Page: %s", movie.getTitle());
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    goToNext(view,new Movie(movie.getTitle(),movie.getYear(),movie.getDirector(),movie.getGenres(),movie.getStars()));
                }
            });
        }
    }
    public void goToRed(View view) {

        Intent goToIntent = new Intent(this, LoginActivity.class);

        startActivity(goToIntent);
    }
}
