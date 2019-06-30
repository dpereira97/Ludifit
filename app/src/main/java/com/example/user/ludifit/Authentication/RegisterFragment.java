package com.example.user.ludifit.Authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.user.ludifit.R;

public class RegisterFragment extends Fragment {
    private String emailPassed;
    private EditText editTextFrag;

    ImageView imgProfile, imageViewMain;
    private btnClicked mCallback;

    public interface btnClicked {
        void signUp();
        void addPhoto();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);
        editTextFrag = view.findViewById(R.id.emailRegister);
        imgProfile = view.findViewById(R.id.profilePic);

        imageViewMain = view.findViewById(R.id.user_image);

        Bundle b = getArguments();
        if (b != null) {
            emailPassed = b.getString("email");
        }
        editTextFrag.setText(emailPassed);

        Button button = view.findViewById(R.id.add_pic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.addPhoto();
            }
        });

        Button register = view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.signUp();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (RegisterFragment.btnClicked) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement btnClicked");
        }
    }
}