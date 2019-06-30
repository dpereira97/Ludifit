package com.example.user.ludifit.MainScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.ludifit.R;

import Domain.Quest;
import Domain.QuestType;
import Domain.UnitType;

public class UserQuestFragment extends Fragment {

    public interface QuestComplete{
        public void completeQuest(int xp, int id);
    }

    private QuestComplete mCallback;
    private int xp;

    private double curr;
    private double needed;
    private int id;

    private QuestType type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_quest_fragment, container, false);
        Bundle args = getArguments();
        TextView name = (TextView) view.findViewById(R.id.quest_name);
        TextView obj = (TextView) view.findViewById(R.id.quest_progress);
        xp = args.getInt("xpGiven");
        curr = args.getDouble("done");
        needed = args.getDouble("obj");
        id = args.getInt("id");

        String objective;

        type = QuestType.valueOf(args.getString("name"));

        if(curr >= needed){
            FrameLayout fl = (FrameLayout) view.findViewById(R.id.inner_quest_box);
            fl.setBackground(getResources().getDrawable(R.drawable.layout_bg_done));
            if(type.equals(QuestType.Steps)) {
                objective = Math.round(needed) + "/" +  Math.round(needed);
            } else {
                objective = needed + "/" + needed;
            }
            fl.setClickable(true);
            fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.completeQuest(xp,id);
                    close();
                }
            });
        }else{
            objective = curr + "/" + needed;
        }

        ProgressBar pb = (ProgressBar) view.findViewById(R.id.quest_exp);
        int progress = (int)((curr >= needed)? 100:(curr*100)/needed);
        pb.setProgress(progress);

        String text = getQuestText(args.getString("name"), args.getString("unit"), needed);
        name.setText(text);
        obj.setText(objective);

        return view;
    }

    private void close(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private String getQuestText(String type, String unitType, double goal) {
        QuestType t = QuestType.valueOf(type);
        String unit = "";
        if (!unitType.equals("nothing")) {
            UnitType ut = UnitType.valueOf(unitType);
            unit = (ut == UnitType.Meters) ? " meters" : " kilometers";
        }
        switch (t) {
            case Steps:
                return "Walk " + (int)goal + " steps";
            case Walking:
                return "Walk " + (int)goal + unit;
            case Running:
                return "Run " + (int)goal + unit;
            default:
                return "";
        }
    }

    public void addToCurrent(double newCurr){
        curr += newCurr;
        TextView obj = (TextView) getView().findViewById(R.id.quest_progress);
        if(curr >= needed){
            FrameLayout fl = (FrameLayout) getView().findViewById(R.id.inner_quest_box);
            fl.setBackground(getResources().getDrawable(R.drawable.layout_bg_done));
            obj.setText(needed + "/" + needed);
            fl.setClickable(true);
            fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.completeQuest(xp,id);
                    close();
                }
            });
        }else{
            if(type.equals(QuestType.Steps)) {
                obj.setText(Math.round(curr) + "/" + Math.round(needed));
            }
            else {
                obj.setText(Math.round(curr * 100.0)/ 100.0 + "/" + Math.round(needed));
            }
        }

        ProgressBar pb = (ProgressBar) getView().findViewById(R.id.quest_exp);
        int progress = (int)((curr >= needed)? 100:(curr*100)/needed);
        pb.setProgress(progress);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (QuestComplete) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement QuestComplete");
        }
    }
}