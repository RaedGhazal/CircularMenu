package com.raedghazal.circularmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CircularMenu circularMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circularMenu = findViewById(R.id.c_menu);
        //circularMenu.setItemCount(6);
        circularMenuCustomization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        circularMenu.invalidate();
    }

    public void circularMenuCustomization() {
        ArrayList<Pair<Integer, String>> iconsTexts = new ArrayList<>();
        iconsTexts.add(new Pair<>(R.drawable.ic_phone, "Zong Load\nBundles"));
        iconsTexts.add(new Pair<>(R.drawable.ic_bill, "Bill\nPayment"));
        iconsTexts.add(new Pair<>(R.drawable.ic_more, "More"));
        iconsTexts.add(new Pair<>(R.drawable.ic_money_transfer, "Money\nTransfer"));
        iconsTexts.add(new Pair<>(R.drawable.ic_cash_out, "Cash\nOut"));
        iconsTexts.add(new Pair<>(R.drawable.ic_cash_in, "Cash In"));
        iconsTexts.add(new Pair<>(R.drawable.ic_more, "More"));
        iconsTexts.add(new Pair<>(R.drawable.ic_money_transfer, "Money\nTransfer"));
        iconsTexts.add(new Pair<>(R.drawable.ic_cash_out, "Cash\nOut"));
        iconsTexts.add(new Pair<>(R.drawable.ic_cash_in, "Cash In"));
        circularMenu.setIconsTexts(iconsTexts);
        circularMenu.setTextSize(15);
        circularMenu.setOuterRadius(180);
        circularMenu.setItemCount(6);
        circularMenu.setInnerRadius(70);
        circularMenu.setCenterTextSize(17);
        circularMenu.setStrokeWidth(10);
        circularMenu.setCenterTextColor(Color.BLACK);
        circularMenu.setInnerCircleColor(Color.WHITE);
        circularMenu.setStrokeColor(Color.WHITE);
        circularMenu.setSelectedItemColor(Color.parseColor("#8BC63E"));
        circularMenu.setDefaultItemsColor(Color.parseColor("#E1288F"));
        circularMenu.setCenterText("Current Balance\nRs. 2000");
    }

}
