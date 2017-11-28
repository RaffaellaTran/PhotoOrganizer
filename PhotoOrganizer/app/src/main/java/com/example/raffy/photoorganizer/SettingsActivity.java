package com.example.raffy.photoorganizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;

/**
 * Created by Raffy on 15/11/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    // Switches
    ImageQualitySwitches wifiSwitches = new ImageQualitySwitches();
    ImageQualitySwitches mobileSwitches = new ImageQualitySwitches();

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
    }

    public void setImageQuality(ImageQualitySwitches iqs, ImageQuality iq) {
        if (iqs.low.isChecked()) {
            iq.setWidth(640);
            iq.setHeight(480);
            iq.setQuality(Quality.LOW.toString());
        }
        else if (iqs.high.isChecked()) {
            iq.setWidth(1280);
            iq.setHeight(960);
            iq.setQuality(Quality.HIGH.toString());
        }
        else if (iqs.full.isChecked()) {
            iq.setQuality(Quality.FULL.toString());
        }

        else {
            iq.setWidth(1280);
            iq.setHeight(960);
            iq.setQuality(Quality.HIGH.toString());
        }
    }

    /*
    public boolean OneSwitchIsOn() {
        if (this.wifiSwitches.atleastOneSwitchIsTrue() || this.mobileSwitches.atleastOneSwitchIsTrue()) {
            return true;
        }
        return false;
    }
    */




    // ADD function for only one switch can be "ON" @ a time
    // ADD default value
    // SAVE the value that was last turned "ON"
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

class ImageQuality {
    int width;
    int height;
    String quality;

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
}

/*
class Quality {
    String LOW = "low";
    String HIGH = "high";
    String FULL = "full";
}
*/