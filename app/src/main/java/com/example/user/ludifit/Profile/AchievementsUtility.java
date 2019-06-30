package com.example.user.ludifit.Profile;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Domain.Achievement;
import Domain.AchievementCategory;

public class AchievementsUtility {

    public static void achievementsDatabase (FirebaseDatabase fd) {

        DatabaseReference ref = fd.getReference().child("Achievements");

        // Achievements for steps

        Bitmap imgSteps = BitmapFactory.decodeFile("drawable/steps.png");

        Achievement a1 = new Achievement(1,AchievementCategory.Steps, "Walked 10 steps", imgSteps, 10);

        Achievement a2 = new Achievement(2,AchievementCategory.Steps, "Walked 100 steps", imgSteps, 100);

        Achievement a3 = new Achievement(3,AchievementCategory.Steps, "Walked 1000 steps", imgSteps, 1000);

        ref.child("steps").child(String.valueOf(a1.getId())).setValue(a1);
        ref.child("steps").child(String.valueOf(a2.getId())).setValue(a2);
        ref.child("steps").child(String.valueOf(a3.getId())).setValue(a3);

        // ---------

        // Achievements for walking

        Bitmap imgWalk = BitmapFactory.decodeFile("drawable/walk.png");

        Achievement a4 = new Achievement(4, AchievementCategory.Walking, "Walked 10 meters", imgWalk, 10);

        Achievement a5 = new Achievement(5, AchievementCategory.Walking, "Walked 100 meters", imgWalk, 100);

        Achievement a6 = new Achievement(6, AchievementCategory.Walking, "Walked 1 kilometer", imgWalk, 1);

        ref.child("walking").child(String.valueOf(a4.getId())).setValue(a4);
        ref.child("walking").child(String.valueOf(a5.getId())).setValue(a5);
        ref.child("walking").child(String.valueOf(a6.getId())).setValue(a6);

        // ---------


        // Achievements for running

        Bitmap imgRunning = BitmapFactory.decodeFile("drawable/running.png");

        Achievement a7 = new Achievement(7, AchievementCategory.Running, "Ran 10 meters", imgRunning, 10);

        Achievement a8 = new Achievement(8, AchievementCategory.Running, "Ran 100 meters", imgRunning, 100);

        Achievement a9 = new Achievement(9, AchievementCategory.Running, "Ran 1 kilometer", imgRunning, 1);

        ref.child("running").child(String.valueOf(a7.getId())).setValue(a7);
        ref.child("running").child(String.valueOf(a8.getId())).setValue(a8);
        ref.child("running").child(String.valueOf(a9.getId())).setValue(a9);

    }
}