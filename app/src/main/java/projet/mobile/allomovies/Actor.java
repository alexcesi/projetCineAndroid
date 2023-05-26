package projet.mobile.allomovies;

public class Actor {
    private int id;
    private String name;
    private String profilePath;

    public Actor(int id, String name, String profilePath) {
        this.id = id;
        this.name = name;
        this.profilePath = profilePath;
    }

    public Actor(String actorName, String profilePath) {
        this.name = actorName;
        this.profilePath = profilePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }
}