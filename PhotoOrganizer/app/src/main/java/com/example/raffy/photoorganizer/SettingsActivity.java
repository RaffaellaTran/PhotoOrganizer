package com.example.raffy.photoorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by Raffy on 15/11/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    // RadioButtons
    ImageQualityRadioBtn wifiRadioBtn = new ImageQualityRadioBtn();
    ImageQualityRadioBtn mobileRadioBtn = new ImageQualityRadioBtn();

    // Image Quality
    ImageQuality wifiQuality = new ImageQuality();
    ImageQuality mobileQuality = new ImageQuality();

    // SharedPreferences
    public static final String PREFERENCES = "preferences";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.wifiRadioBtn.low = (RadioButton) findViewById(R.id.wifiRadioBtnLow);
        this.wifiRadioBtn.high = (RadioButton) findViewById(R.id.wifiRadioBtnHigh);
        this.wifiRadioBtn.full = (RadioButton) findViewById(R.id.wifiRadioBtnFull);
        this.wifiRadioBtn.radioBtns = (RadioGroup) findViewById(R.id.wifiRadioGroup);

        this.mobileRadioBtn.low = (RadioButton) findViewById(R.id.mobileRadioBtnLow);
        this.mobileRadioBtn.high = (RadioButton) findViewById(R.id.mobileRadioBtnHigh);
        this.mobileRadioBtn.full = (RadioButton) findViewById(R.id.mobileRadioBtnFull);
        this.mobileRadioBtn.radioBtns = (RadioGroup) findViewById(R.id.mobileRadioGroup);

        sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        setRadioButtons();

        setWifiImageQuality();
        setMobileImageQuality();

        this.wifiRadioBtn.radioBtns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                setWifiImageQuality();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("wifiQuality", wifiQuality.getQuality());
                editor.commit();
            }
        });


        this.mobileRadioBtn.radioBtns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                setMobileImageQuality();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("mobileQuality", mobileQuality.getQuality());
                editor.commit();
            }
        });
    }

    public void setRadioButtons() {
        String wQuality = this.sharedpreferences.getString("wifiQuality", "high");
        String mQuality = this.sharedpreferences.getString("mobileQuality", "high");


        if (wQuality.equals(this.wifiQuality.LOW)) {
            this.wifiRadioBtn.low.setChecked(true);
        }

        else if (wQuality.equals(this.wifiQuality.HIGH)) {
            this.wifiRadioBtn.high.setChecked(true);
        }

        else if (wQuality.equals(this.wifiQuality.FULL)) {
            this.wifiRadioBtn.full.setChecked(true);
        }

        else {
            this.wifiRadioBtn.high.setChecked(true);
        }

        if (mQuality.equals(this.mobileQuality.LOW)) {
            this.mobileRadioBtn.low.setChecked(true);
        }

        else if (mQuality.equals(this.mobileQuality.HIGH)) {
            this.mobileRadioBtn.high.setChecked(true);
        }

        else if (mQuality.equals(this.mobileQuality.FULL)) {
            this.mobileRadioBtn.full.setChecked(true);
        }

        else {
            this.mobileRadioBtn.high.setChecked(true);
        }

    }

    public void setWifiImageQuality() {
        if (this.wifiRadioBtn.getActiveRadioBtn().equals(this.wifiRadioBtn.low)) {
            this.wifiQuality.setImageQualityToLow();
        }
        else if (this.wifiRadioBtn.getActiveRadioBtn().equals(this.wifiRadioBtn.high)) {
            this.wifiQuality.setImageQualityToHigh();
        }
        else if (this.wifiRadioBtn.getActiveRadioBtn().equals(this.wifiRadioBtn.full)) {
            this.wifiQuality.setImageQualityToFull();
        }
        else {
            this.wifiQuality.setImageQualityToHigh();
        }
    }

    public void setMobileImageQuality() {
        if (this.mobileRadioBtn.getActiveRadioBtn().equals(this.mobileRadioBtn.low)) {
            this.mobileQuality.setImageQualityToLow();
        }
        else if (this.mobileRadioBtn.getActiveRadioBtn().equals(this.mobileRadioBtn.high)) {
            this.mobileQuality.setImageQualityToHigh();
        }
        else if (this.mobileRadioBtn.getActiveRadioBtn().equals(this.mobileRadioBtn.full)) {
            this.mobileQuality.setImageQualityToFull();
        }
        else {
            this.mobileQuality.setImageQualityToHigh();
        }
    }
}

class ImageQualityRadioBtn {

    RadioButton low;
    RadioButton high;
    RadioButton full;
    RadioGroup radioBtns;

    public RadioButton getActiveRadioBtn() {
        int id = this.radioBtns.getCheckedRadioButtonId();
        return getRadioBtnWithIndex(id);
    }

    public RadioButton getRadioBtnWithIndex(int id) {
        if (id == this.low.getId()) {
            return this.low;
        }
        else if (id == this.high.getId()) {
            return this.high;
        }
        else if (id == this.full.getId()) {
            return this.full;
        }
        else {
            Log.d("INDEXERROR", "getRadioBtnWithIndex: No such radiobutton with index: " + id);
            throw new IndexOutOfBoundsException("No such radiobutton with index: " + id);
        }
    }
}

class ImageQuality {
    int width;
    int height;
    String quality;
    String LOW = "low";
    String HIGH = "high";
    String FULL = "full";

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getQuality() {
        return quality;
    }

    public void setImageQualityToLow() {
        this.setWidth(640);
        this.setHeight(480);
        this.setQuality(this.LOW);
    }

    public void setImageQualityToHigh() {
        this.setWidth(1280);
        this.setHeight(960);
        this.setQuality(this.HIGH);
    }

    public void setImageQualityToFull() {
        this.setWidth(0);
        this.setHeight(0);
        this.setQuality(this.FULL);
    }
}