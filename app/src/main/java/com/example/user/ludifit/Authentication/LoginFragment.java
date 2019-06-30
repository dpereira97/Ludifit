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
import android.widget.TextView;

import com.example.user.ludifit.R;

public class LoginFragment extends Fragment {

    private EditText editTextFrag;
    private Button btnFrag;
    private TextView linkRegister;
    btnClicked mCallback;



    public interface btnClicked {
        void login();
        void linkToRegister();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        btnFrag = view.findViewById(R.id.btnLogin);
        editTextFrag = view.findViewById(R.id.email);
        linkRegister = view.findViewById(R.id.textView2);

        linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.linkToRegister();
            }
        });

        btnFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.login();
            }
        });
        return view;
    }

    public String getEmail() {
        return editTextFrag.getText().toString();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (btnClicked) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement btnClicked");
        }
    }
}