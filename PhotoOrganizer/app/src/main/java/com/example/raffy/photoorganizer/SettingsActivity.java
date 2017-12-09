package com.example.raffy.photoorganizer;

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
    Quality wifiImageQuality = new Quality();
    Quality mobileImageQuality = new Quality();

    // SharedPreferences
    SettingsHelper preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings);

        this.wifiRadioBtn.low = (RadioButton) findViewById(R.id.wifiRadioBtnLow);
        this.wifiRadioBtn.high = (RadioButton) findViewById(R.id.wifiRadioBtnHigh);
        this.wifiRadioBtn.full = (RadioButton) findViewById(R.id.wifiRadioBtnFull);
        this.wifiRadioBtn.radioBtns = (RadioGroup) findViewById(R.id.wifiRadioGroup);

        this.mobileRadioBtn.low = (RadioButton) findViewById(R.id.mobileRadioBtnLow);
        this.mobileRadioBtn.high = (RadioButton) findViewById(R.id.mobileRadioBtnHigh);
        this.mobileRadioBtn.full = (RadioButton) findViewById(R.id.mobileRadioBtnFull);
        this.mobileRadioBtn.radioBtns = (RadioGroup) findViewById(R.id.mobileRadioGroup);

        preferences = new SettingsHelper(this.getApplicationContext());

        setRadioButtons();
        setWifiImageQuality();
        setMobileImageQuality();

        this.wifiRadioBtn.radioBtns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                setWifiImageQuality();

                preferences.editWifiImageQuality(wifiImageQuality.getQuality());
            }
        });


        this.mobileRadioBtn.radioBtns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                setMobileImageQuality();

                preferences.editMobileImageQuality(mobileImageQuality.getQuality());
            }
        });
    }

    public void setRadioButtons() {
        String wQuality = this.preferences.getWifiImageQuality();
        String mQuality = this.preferences.getMobileImageQuality();

        if (wQuality.equals(SettingsHelper.ImageQuality.LOW)) {
            this.wifiRadioBtn.low.setChecked(true);
        }

        else if (wQuality.equals(SettingsHelper.ImageQuality.HIGH)) {
            this.wifiRadioBtn.high.setChecked(true);
        }

        else if (wQuality.equals(SettingsHelper.ImageQuality.FULL)) {
            this.wifiRadioBtn.full.setChecked(true);
        }

        else {
            this.wifiRadioBtn.high.setChecked(true);
        }

        if (mQuality.equals(SettingsHelper.ImageQuality.LOW)) {
            this.mobileRadioBtn.low.setChecked(true);
        }

        else if (mQuality.equals(SettingsHelper.ImageQuality.HIGH)) {
            this.mobileRadioBtn.high.setChecked(true);
        }

        else if (mQuality.equals(SettingsHelper.ImageQuality.FULL)) {
            this.mobileRadioBtn.full.setChecked(true);
        }

        else {
            this.mobileRadioBtn.high.setChecked(true);
        }

    }

    public void setWifiImageQuality() {
        if (this.wifiRadioBtn.getActiveRadioBtn().equals(this.wifiRadioBtn.low)) {
            this.wifiImageQuality.setImageQualityToLow();
        }
        else if (this.wifiRadioBtn.getActiveRadioBtn().equals(this.wifiRadioBtn.high)) {
            this.wifiImageQuality.setImageQualityToHigh();
        }
        else if (this.wifiRadioBtn.getActiveRadioBtn().equals(this.wifiRadioBtn.full)) {
            this.wifiImageQuality.setImageQualityToFull();
        }
        else {
            this.wifiImageQuality.setImageQualityToHigh();
        }
    }

    public void setMobileImageQuality() {
        if (this.mobileRadioBtn.getActiveRadioBtn().equals(this.mobileRadioBtn.low)) {
            this.mobileImageQuality.setImageQualityToLow();
        }
        else if (this.mobileRadioBtn.getActiveRadioBtn().equals(this.mobileRadioBtn.high)) {
            this.mobileImageQuality.setImageQualityToHigh();
        }
        else if (this.mobileRadioBtn.getActiveRadioBtn().equals(this.mobileRadioBtn.full)) {
            this.mobileImageQuality.setImageQualityToFull();
        }
        else {
            this.mobileImageQuality.setImageQualityToHigh();
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

class Quality {
    String quality;

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getQuality() {
        return this.quality;
    }

    public void setImageQualityToLow() {
        this.setQuality(SettingsHelper.ImageQuality.LOW.toString());
    }

    public void setImageQualityToHigh() {
        this.setQuality(SettingsHelper.ImageQuality.HIGH.toString());
    }

    public void setImageQualityToFull() {
        this.setQuality(SettingsHelper.ImageQuality.FULL.toString());
    }
}