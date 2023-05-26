package projet.mobile.allomovies;

import java.util.ArrayList;
import java.util.List;

public class TVShow {
    private int id;
    private String name;
    private String overview;
    private String poster_path;
    private List<String> actorList;

    public TVShow(int id, String name, String overview, String poster_path) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.poster_path = poster_path;
        this.actorList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterUrl() {
        String baseUrl = "https://image.tmdb.org/t/p/original";
        return baseUrl + poster_path;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getActorList() {
        return actorList;
    }

    public void setActorList(List<String> actorList) {
        this.actorList = actorList;
    }
}