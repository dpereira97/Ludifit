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
import android.widget.ImageButton;

import com.example.user.ludifit.R;

public class AddQuestButtonFragment extends Fragment {

    addQuestInterface mCallback;

    public interface addQuestInterface{
        void startAddQuest();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_quest_button_fragment, container, false);
        ImageButton button = (ImageButton) view.findViewById(R.id.add_quest_button);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCallback.startAddQuest();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (addQuestInterface) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement btnClicked");
        }
    }
}
