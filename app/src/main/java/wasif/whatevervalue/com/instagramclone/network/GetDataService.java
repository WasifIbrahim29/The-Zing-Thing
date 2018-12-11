package wasif.whatevervalue.com.instagramclone.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import wasif.whatevervalue.com.instagramclone.flip.sample.model.Friend;

/**
 * Created by praka on 12/24/2017.
 */

public interface GetDataService {

    @GET("/WasifIbrahim29/Retrofit-Data/Friend")
    Call<List<Friend>> getAllFriends();
}
