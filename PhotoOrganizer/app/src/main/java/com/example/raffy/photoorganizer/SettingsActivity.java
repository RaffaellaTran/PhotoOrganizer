package com.example.raffy.photoorganizer;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.ToggleButton;

/**
 * Created by Raffy on 15/11/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    /*
    // Switches
    ImageQualitySwitches wifiSwitches = new ImageQualitySwitches();
    ImageQualitySwitches mobileSwitches = new ImageQualitySwitches();

    // ToggleButtons
    ImageQualityToggleBtn wifiToggleBtn = new ImageQualityToggleBtn();
    ImageQualityToggleBtn mobileToggleBtn = new ImageQualityToggleBtn();
    */

    // RadioButtons
    ImageQualityRadioBtn wifiRadioBtn = new ImageQualityRadioBtn();
    ImageQualityRadioBtn mobileRadioBtn = new ImageQualityRadioBtn();

    // Image Quality
    ImageQuality wifiQuality = new ImageQuality();
    ImageQuality mobileQuality = new ImageQuality();

    // Qualities

    public enum Quality {
        LOW,
        HIGH,
        FULL
    }

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

        setWifiImageQuality();
        setMobileImageQuality();

        /*
        this.wifiSwitches.low = (Switch) findViewById(R.id.lowWifiSwitch);
        this.mobileSwitches.low = (Switch) findViewById(R.id.lowMobileSwitch);
        this.wifiSwitches.high = (Switch) findViewById(R.id.highWifiSwitch);
        this.mobileSwitches.high = (Switch) findViewById(R.id.highMobileSwitch);
        this.wifiSwitches.full = (Switch) findViewById(R.id.fullWifiSwitch);
        this.mobileSwitches.full = (Switch) findViewById(R.id.fullMobileSwitch);

        if (!this.wifiSwitches.atleastOneSwitchIsTrue()) {
            this.wifiSwitches.high.setChecked(true);
        }

        if (!this.mobileSwitches.atleastOneSwitchIsTrue()) {
            this.mobileSwitches.high.setChecked(true);
        }

        setImageQuality(this.wifiSwitches, this.wifiQuality);
        setImageQuality(this.mobileSwitches, this.mobileQuality);
        */
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

class ImageQualitySwitches {

    Switch low;
    Switch high;
    Switch full;

    boolean switchIsOn(Switch s) {
        return s.isChecked();
    }

    void setSwitchToTrueOthersToFalse(Switch s) {
        s.setChecked(true);

        if (!s.equals(this.low)) {
            this.low.setChecked(false);
        }

        if (!s.equals(this.high)) {
            this.high.setChecked(false);
        }

        if (!s.equals(this.full)) {
            this.full.setChecked(false);
        }
    }

    boolean atleastOneSwitchIsTrue() {
        if (this.low.isChecked() || this.high.isChecked() || this.full.isChecked()) {
            return true;
        }
        return false;
    }
}

class ImageQualityToggleBtn {

    ToggleButton low;
    ToggleButton high;
    ToggleButton full;

    boolean toggleButtonIsOn(ToggleButton t) {
        return t.isChecked();
    }

    void setToggleButtonToTrueOthersToFalse(Switch s) {
        s.setChecked(true);

        this.full.isClickable();

        if (!s.equals(this.low)) {
            this.low.setChecked(false);
        }

        if (!s.equals(this.high)) {
            this.high.setChecked(false);
        }

        if (!s.equals(this.full)) {
            this.full.setChecked(false);
        }
    }

    boolean atleastOneSwitchIsTrue() {
        if (this.low.isChecked() || this.high.isChecked() || this.full.isChecked()) {
            return true;
        }
        return false;
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
        this.setQuality(LOW);
    }

    public void setImageQualityToHigh() {
        this.setWidth(1280);
        this.setHeight(960);
        this.setQuality(HIGH);
    }

    public void setImageQualityToFull() {
        this.setQuality(FULL);
    }
}

/*
class Quality {
    String LOW = "low";
    String HIGH = "high";
    String FULL = "full";
}
*/