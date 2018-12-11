package wasif.whatevervalue.com.instagramclone.rxjava2;

import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
//import rx.Observable;


public interface GitHubService {
   // @GET("users/{user}/starred") Observable<List<GitHubRepo>> getStarredRepositories(@Path("user") String userName);

    @GET("users/{user}/repos")
    io.reactivex.Observable<List<GitHubRepo>> getStarredRepositories(@Path("user") String userName);

}
