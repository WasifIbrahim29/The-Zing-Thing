package wasif.whatevervalue.com.instagramclone.flip.sample.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wasif.whatevervalue.com.instagramclone.Likes.BaseFlipAdapter;
import wasif.whatevervalue.com.instagramclone.R;
import wasif.whatevervalue.com.instagramclone.Utils.BottomNavigationViewHelper;
import wasif.whatevervalue.com.instagramclone.flip.sample.Utils;
import wasif.whatevervalue.com.instagramclone.flip.sample.model.Friend;
import wasif.whatevervalue.com.instagramclone.flipviewpager.utils.FlipSettings;
import wasif.whatevervalue.com.instagramclone.network.GetDataService;
import wasif.whatevervalue.com.instagramclone.network.RetrofitClientInstance;

import static wasif.whatevervalue.com.instagramclone.flip.sample.Utils.friends;

public class FriendsActivity extends Activity {

    private static final String TAG ="FriendsActivity" ;
    private static final int ACTIVITY_NUM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        final ListView friends = (ListView) findViewById(R.id.friends);

        Log.d(TAG, "onCreate: "+ R.drawable.hamzayy);
        Log.d(TAG, "onCreate: "+ R.color.saffron);
        Log.d(TAG, "onCreate: "+ R.drawable.kate);
        Log.d(TAG, "onCreate: "+ + R.color.green);
        Log.d(TAG, "onCreate: "+ R.drawable.paul);
        Log.d(TAG, "onCreate: "+ R.color.pink);
        Log.d(TAG, "onCreate: "+ R.drawable.daria);
        Log.d(TAG, "onCreate: "+ R.color.orange);
        Log.d(TAG, "onCreate: "+ R.drawable.kirill);
        Log.d(TAG, "onCreate: "+ R.color.saffron);
        Log.d(TAG, "onCreate: "+ R.drawable.julia);
        Log.d(TAG, "onCreate: "+ R.color.green);



        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Friend>> call = service.getAllFriends();
        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                List<Friend> frens= response.body();
                Log.d(TAG, "onResponse: "+ frens);

                FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();

                friends.setAdapter(new FriendsAdapter(FriendsActivity.this, frens,settings));
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                Log.d(TAG, "onFailure:  Something went wrong man");
                Toast.makeText(FriendsActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
       
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend f = (Friend) friends.getAdapter().getItem(position);

                Toast.makeText(FriendsActivity.this, f.getNickname(), Toast.LENGTH_SHORT).show();
            }
        });

        //setupBottomNavigationView();
    }

    private List<Friend> getFriends(List<Friend> frens){
        return frens;
    }

    class FriendsAdapter extends BaseFlipAdapter<Friend> {

        private final int PAGES = 3;
        private int[] IDS_INTEREST = {R.id.interest_1, R.id.interest_2, R.id.interest_3, R.id.interest_4, R.id.interest_5};

        public FriendsAdapter(Context context, List<Friend> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int position, View convertView, ViewGroup parent, Friend friend1, Friend friend2) {

            final FriendsHolder holder;

            if (convertView == null) {
                holder = new FriendsHolder();
                convertView = getLayoutInflater().inflate(R.layout.friends_merge_page, parent, false);
                holder.leftAvatar = (ImageView) convertView.findViewById(R.id.first);
                holder.rightAvatar = (ImageView) convertView.findViewById(R.id.second);
                holder.infoPage = getLayoutInflater().inflate(R.layout.friends_info, parent, false);
                holder.nickName = (TextView) holder.infoPage.findViewById(R.id.nickname);
                holder.details = (Button) holder.infoPage.findViewById(R.id.detailButton);

                for (int id : IDS_INTEREST)
                    holder.interests.add((TextView) holder.infoPage.findViewById(id));

                holder.details.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(), "Hurray! You clicked", Toast.LENGTH_LONG).show();
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (FriendsHolder) convertView.getTag();
            }



            switch (position) {
                // Merged page with 2 friends
                case 1:
                    holder.leftAvatar.setImageResource(friend1.getAvatar());
                    if (friend2 != null)
                        holder.rightAvatar.setImageResource(friend2.getAvatar());
                    break;
                default:
                    fillHolder(holder, position == 0 ? friend1 : friend2);
                    holder.infoPage.setTag(holder);
                    return holder.infoPage;
            }
            return convertView;
        }

        @Override
        public int getPagesCount() {
            return PAGES;
        }

        private void fillHolder(FriendsHolder holder, Friend friend) {
            if (friend == null)
                return;
            Iterator<TextView> iViews = holder.interests.iterator();
            Iterator<String> iInterests = friend.getInterests().iterator();

            while (iViews.hasNext() && iInterests.hasNext())
                iViews.next().setText(iInterests.next());

            holder.infoPage.setBackgroundColor(getResources().getColor(friend.getBackground()));
            holder.nickName.setText(friend.getNickname());
            holder.details.setText("Details");

        }

        class FriendsHolder {
            ImageView leftAvatar;
            ImageView rightAvatar;
            View infoPage;

            List<TextView> interests = new ArrayList<>();
            TextView nickName;
            Button details;
        }
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
