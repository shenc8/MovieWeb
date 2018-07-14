package edu.uci.ics.fabflixmobile;

public class Movie {
    private String title;
    private Integer year;
    private String director;
    private String genres;
    private String stars;
    public Movie(String title, int year,String director, String genres,String stars) {
        this.director=director;
        this.genres=genres;
        this.stars=stars;
        this.title=title;
        this.year=year;
    }

    public String getDirector() {
        return director;
    }

    public Integer getYear() {
        return year;
    }

    public String getStars() {
        return stars;
    }

    public String getTitle() {
        return title;
    }

    public String getGenres() {
        return genres;
    }
}
