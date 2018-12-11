package wasif.whatevervalue.com.instagramclone.Likes;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import wasif.whatevervalue.com.instagramclone.Models.User;
import wasif.whatevervalue.com.instagramclone.R;
import wasif.whatevervalue.com.instagramclone.videocompressor.Config;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{

    private ArrayList<User> mUsers = new ArrayList<>();
    private UserListRecyclerClickListener mClickListener;

    public UserRecyclerAdapter(ArrayList<User> users, UserListRecyclerClickListener clickListener) {
        mUsers = users;
        mClickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view,mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ((ViewHolder)holder).username.setText(mUsers.get(position).getUsername());
        ((ViewHolder)holder).status.setText(mUsers.get(position).getStatus());
        String thumb=mUsers.get(position).getProfile_photo();
        Picasso.with(holder.profileImage.getContext()).load(thumb).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        TextView username, status;
        CircleImageView profileImage;
        UserListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, UserListRecyclerClickListener clickListener) {
            super(itemView);
            username = itemView.findViewById(R.id.user_single_name);
            status = itemView.findViewById(R.id.user_single_status);
            profileImage=itemView.findViewById(R.id.user_single_image);

            mClickListener = clickListener;

            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            mClickListener.onUserClicked(getAdapterPosition());
        }
    }
    public interface UserListRecyclerClickListener{
        void onUserClicked(int position);
    }


}
















