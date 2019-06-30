package com.example.user.ludifit.Social;

import android.view.View;

public class SocialListener implements View.OnClickListener {

    private String id;

    public SocialListener(String id) {
        this.id = id;
    }

    public String getId() {return this.id;}

    @Override
    public void onClick(View v) {
        //override this shizzle
    }


}
