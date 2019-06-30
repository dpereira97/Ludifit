package com.example.user.ludifit.MainScreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.ludifit.R;

import Domain.User;

public class TopUserFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_user_fragment, container, false);
        Bundle args = getArguments();
        TextView name = view.findViewById(R.id.user_name);
        TextView level = view.findViewById(R.id.user_level);
        name.setText(args.getString("name"));
        level.setText("Level " + args.getInt("level"));

        ImageView img = view.findViewById(R.id.user_image);

        User u = MainActivity.getCurrentUser();

        Glide.with(view).asBitmap().load(u.getImage()).into(img);

        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
        int progress = (args.getInt("currXp")*100)/args.getInt("neededXp");
        pb.setProgress(progress);

        return view;
    }

    public void changeLevel(int newLevel, int currXp, int neededXp){
        TextView level = getView().findViewById(R.id.user_level);
        level.setText("Level " + newLevel);

        ProgressBar pb = (ProgressBar) getView().findViewById(R.id.progressBar);
        int progress = (currXp*100)/neededXp;
        pb.setProgress(progress);
    }
}
