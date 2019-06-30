package com.example.user.ludifit.Social;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.ludifit.R;

import java.util.ArrayList;
import java.util.HashMap;

import Domain.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "FriendsRecyclerViewAdapter";

    //vars
    private HashMap<String, User> userList;
    private Context mContext;
    private Social s;

    public FriendsRecyclerViewAdapter(Context mContext, HashMap<String, User> userList, Social s) {
        this.userList = userList;
        this.mContext = mContext;
        this.s = s;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_friends_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       ArrayList<User> users = new ArrayList<>(userList.values());

       if(users.get(position).getImage() != null)
        Glide.with(mContext)
                .asBitmap()
                .load(users.get(position).getImage())
                .into(holder.image);
       else
           Glide.with(mContext)
                   .asBitmap()
                   .load("http://icons.iconarchive.com/icons/custom-icon-design/flatastic-4/512/User-orange-icon.png")
                   .into(holder.image);


        holder.name.setText(users.get(position).getUsername());

        holder.image.setOnClickListener(new SocialListener(users.get(position).getId()) {
            @Override
            public void onClick(View v) {
                s.setU(getId());
                s.showFriendsPopup(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }
    }
}
