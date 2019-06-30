package Domain;

import android.graphics.Bitmap;

import java.util.HashMap;

public class Group {

    private String name;
    private String imageUrl;
    private Quest currQuest;
    HashMap<String, User> members;

    public Group(String name) {
        this.name = name;
        this.members = new HashMap<>();
        this.imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRu4yOIEfFSmk849MP71E3V38Fv1PpU_bq_0i06Z-G8-fxWjxqd8Q";
        members = new HashMap<>();
    }

    public String getName() {return name;}

    public void setName(String s) {name = s;}

    public void setImageUrl(String bm) {this.imageUrl = bm;}

    public String getImageUrl() {return imageUrl;}

    public Quest getCurrentQuest() {return currQuest;}

    public void beginGroupQuest(Quest q) {
        //TO DO
    }

    public User getMember(String username) {return members.get(username);}

    public boolean hashMember(String username) {return members.containsKey(username);}

    public void addMember(User u) {members.put(u.getUsername(), u);}
}
