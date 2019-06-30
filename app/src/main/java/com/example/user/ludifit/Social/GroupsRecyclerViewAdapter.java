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

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsRecyclerViewAdapter extends RecyclerView.Adapter<GroupsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "FriendsRecyclerViewAdapter";

    //vars
    private ArrayList<String> mNames;
    private ArrayList<String> mImageUrls;
    private Context mContext;
    private Social s;

    public GroupsRecyclerViewAdapter(Context mContext, ArrayList<String> mNames, ArrayList<String> mImageUrls, Social s) {
        this.mNames = mNames;
        this.mImageUrls = mImageUrls;
        this.mContext = mContext;
        this.s = s;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_groups_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(mNames.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.showGroupsPopup(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
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
