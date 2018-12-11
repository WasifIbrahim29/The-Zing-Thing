package wasif.whatevervalue.com.instagramclone.flip.sample.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Friend {

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }



    private int avatar;
    private String nickname;
    private int background;
    private List<String> interests= new ArrayList<>();





    public Friend(int avatar, String nickname, int background, String interest) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.background = background;
        //interests.addAll(Arrays.asList(interest));
    }

    public int getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public int getBackground() {
        return background;
    }

    public List<String> getInterests() {
        return interests;
    }
}
