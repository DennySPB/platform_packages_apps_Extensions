/*
 * Copyright (C) 2013 The ChameleonOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aospextended.extensions;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Gravity;
import android.graphics.Color;
import com.android.settings.R;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.SettingsPreferenceFragment;

import org.aospextended.extensions.preference.CustomSeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class VolumePanel extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
    private static final String TAG = "VolumePanel";

    private static final String VOLUME_DIALOG_TIMEOUT  = "volume_dialog_timeout";
    private static final String TRANSPARENT_VOLUME_DIALOG  = "transparent_volume_dialog";
    private static final String PREF_VOLUME_DIALOG_STROKE = "volume_dialog_stroke";
    private static final String PREF_VOLUME_DIALOG_STROKE_COLOR = "volume_dialog_stroke_color";
    private static final String PREF_VOLUME_DIALOG_STROKE_THICKNESS = "volume_dialog_stroke_thickness";
    private static final String PREF_VOLUME_DIALOG_CORNER_RADIUS = "volume_dialog_corner_radius";
    private static final String PREF_VOLUME_DIALOG_STROKE_DASH_WIDTH = "volume_dialog_dash_width";
    private static final String PREF_VOLUME_DIALOG_STROKE_DASH_GAP = "volume_dialog_dash_gap";


    private CustomSeekBarPreference mVolumeDialogTimeout;
    private CustomSeekBarPreference mVolumeDialogAlpha;
    private ListPreference mVolumeDialogStroke;
    private ColorPickerPreference mVolumeDialogStrokeColor;
    private CustomSeekBarPreference mVolumeDialogStrokeThickness;
    private CustomSeekBarPreference mVolumeDialogCornerRadius;
    private CustomSeekBarPreference mVolumeDialogDashWidth;
    private CustomSeekBarPreference mVolumeDialogDashGap;

    static final int DEFAULT_VOLUME_DIALOG_STROKE_COLOR = 0xFF80CBC4;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.volume_panel);

        mVolumeDialogTimeout = (CustomSeekBarPreference) findPreference(VOLUME_DIALOG_TIMEOUT);
        int VolumeTimeout = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_TIMEOUT, 3000);
        mVolumeDialogTimeout.setValue(VolumeTimeout / 1);
        mVolumeDialogTimeout.setOnPreferenceChangeListener(this);

        mVolumeDialogAlpha = (CustomSeekBarPreference) findPreference(TRANSPARENT_VOLUME_DIALOG);
        int VolumeAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.TRANSPARENT_VOLUME_DIALOG, 255);
        mVolumeDialogAlpha.setValue(VolumeAlpha / 1);
        mVolumeDialogAlpha.setOnPreferenceChangeListener(this);

        // Volume dialog stroke
        mVolumeDialogStroke =
                (ListPreference) findPreference(PREF_VOLUME_DIALOG_STROKE);
        int volumeDialogStroke = Settings.System.getIntForUser(getContentResolver(),
                        Settings.System.VOLUME_DIALOG_STROKE, 0,
                        UserHandle.USER_CURRENT);
        mVolumeDialogStroke.setValue(String.valueOf(volumeDialogStroke));
        mVolumeDialogStroke.setSummary(mVolumeDialogStroke.getEntry());
        mVolumeDialogStroke.setOnPreferenceChangeListener(this);

        // Volume dialog stroke color
        mVolumeDialogStrokeColor =
                (ColorPickerPreference) findPreference(PREF_VOLUME_DIALOG_STROKE_COLOR);
        mVolumeDialogStrokeColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_STROKE_COLOR, DEFAULT_VOLUME_DIALOG_STROKE_COLOR);
        String hexColor = String.format("#%08x", (0xFF80CBC4 & intColor));
        mVolumeDialogStrokeColor.setSummary(hexColor);
        mVolumeDialogStrokeColor.setNewPreviewColor(intColor);

        // Volume dialog stroke thickness
        mVolumeDialogStrokeThickness =
                (CustomSeekBarPreference) findPreference(PREF_VOLUME_DIALOG_STROKE_THICKNESS);
        int volumeDialogStrokeThickness = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_STROKE_THICKNESS, 4);
        mVolumeDialogStrokeThickness.setValue(volumeDialogStrokeThickness / 1);
        mVolumeDialogStrokeThickness.setOnPreferenceChangeListener(this);

        // Volume dialog corner radius
        mVolumeDialogCornerRadius =
                (CustomSeekBarPreference) findPreference(PREF_VOLUME_DIALOG_CORNER_RADIUS);
        int volumeDialogCornerRadius = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_CORNER_RADIUS, 10);
        mVolumeDialogCornerRadius.setValue(volumeDialogCornerRadius / 1);
        mVolumeDialogCornerRadius.setOnPreferenceChangeListener(this);
            // Volume dialog dash width
        mVolumeDialogDashWidth =
                (CustomSeekBarPreference) findPreference(PREF_VOLUME_DIALOG_STROKE_DASH_WIDTH);
        int volumeDialogDashWidth = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_STROKE_DASH_WIDTH, 0);
        if (volumeDialogDashWidth != 0) {
            mVolumeDialogDashWidth.setValue(volumeDialogDashWidth / 1);
        } else {
            mVolumeDialogDashWidth.setValue(0);
        }
        mVolumeDialogDashWidth.setOnPreferenceChangeListener(this);

        // Volume dialog dash gap
        mVolumeDialogDashGap =
                (CustomSeekBarPreference) findPreference(PREF_VOLUME_DIALOG_STROKE_DASH_GAP);
        int volumeDialogDashGap = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_DIALOG_STROKE_DASH_GAP, 10);
        mVolumeDialogDashGap.setValue(volumeDialogDashGap / 1);
        mVolumeDialogDashGap.setOnPreferenceChangeListener(this);

    }

//    private void updateProximityDelaySummary(int value) {
//        String summary = getResources().getString(R.string.proximity_auto_speaker_delay_summary, value);
//        mProxSpeakerDelay.setSummary(summary);
//    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
	if (preference == mVolumeDialogTimeout) {
	    int valueVolumeTimeout = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VOLUME_DIALOG_TIMEOUT, valueVolumeTimeout * 1);
            return true;
        } else if (preference == mVolumeDialogStroke) {
            int volumeDialogStroke = Integer.parseInt((String) newValue);
            int index = mVolumeDialogStroke.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.
                    VOLUME_DIALOG_STROKE, volumeDialogStroke, UserHandle.USER_CURRENT);
            mVolumeDialogStroke.setSummary(mVolumeDialogStroke.getEntries()[index]);
            VolumeDialogSettingsDisabler(volumeDialogStroke);
    	    return true;
        } else if (preference == mVolumeDialogStrokeColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_STROKE_COLOR, intHex);
            return true;
        } else if (preference == mVolumeDialogStrokeThickness) {
            int val = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_STROKE_THICKNESS, val * 1);
            return true;
        } else if (preference == mVolumeDialogCornerRadius) {
            int val = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_CORNER_RADIUS, val * 1);
            return true;
        } else if (preference == mVolumeDialogDashWidth) {
            int val = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_STROKE_DASH_WIDTH, val * 1);
            return true;
        } else if (preference == mVolumeDialogDashGap) {
            int val = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_DIALOG_STROKE_DASH_GAP, val * 1);
            return true;
	} else if (preference == mVolumeDialogAlpha) {
	    int valueVolumeAlpha = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRANSPARENT_VOLUME_DIALOG, valueVolumeAlpha * 1);
            return true;
	}
    
        return false;
    }

    private void VolumeDialogSettingsDisabler(int volumeDialogStroke) {
        if (volumeDialogStroke == 0) {
			mVolumeDialogStrokeColor.setEnabled(false);
            mVolumeDialogStrokeThickness.setEnabled(false);
            mVolumeDialogDashWidth.setEnabled(false);
            mVolumeDialogDashGap.setEnabled(false);
        } else if (volumeDialogStroke == 1) {
			mVolumeDialogStrokeColor.setEnabled(false);
            mVolumeDialogStrokeThickness.setEnabled(true);
            mVolumeDialogDashWidth.setEnabled(true);
            mVolumeDialogDashGap.setEnabled(true);
        } else {
			mVolumeDialogStrokeColor.setEnabled(true);
            mVolumeDialogStrokeThickness.setEnabled(true);
            mVolumeDialogDashWidth.setEnabled(true);
            mVolumeDialogDashGap.setEnabled(true);
	}
    }


    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.EXTENSIONS;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
