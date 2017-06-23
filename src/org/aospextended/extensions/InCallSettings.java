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
import com.android.settings.R;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.SettingsPreferenceFragment;

import org.aospextended.extensions.preference.CustomSeekBarPreference;

public class InCallSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
    private static final String TAG = "InCallSettings";

    private static final String VIBRATE_ON_CONNECT = "vibrate_on_connect";
    private static final String VIBRATE_ON_DISCONNECT = "vibrate_on_disconnect";
    private static final String VIBRATE_ON_CONNECT_TIME = "vibrate_on_connect_time";
    private static final String VIBRATE_ON_DISCONNECT_TIME = "vibrate_on_disconnect_time";

    private static final String PROXIMITY_AUTO_SPEAKER  = "proximity_auto_speaker";
    private static final String PROXIMITY_AUTO_SPEAKER_DELAY  = "proximity_auto_speaker_delay";
    private static final String PROXIMITY_AUTO_SPEAKER_INCALL_ONLY  = "proximity_auto_speaker_incall_only";

    public static final String BUTTON_SMART_MUTE_KEY = "button_smart_mute";

    private SwitchPreference mVibrateOnConnect;
    private SwitchPreference mVibrateOnDisconnect;

    private CustomSeekBarPreference mVibrateOnConnectTime;
    private CustomSeekBarPreference mVibrateOnDisconnectTime;
    private SwitchPreference mHomeRingButton;

    private SwitchPreference mProxSpeaker;
    private ListPreference mProxSpeakerDelay;
    private SwitchPreference mProxSpeakerIncallOnly;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.incall);

	mVibrateOnConnect = (SwitchPreference) findPreference(VIBRATE_ON_CONNECT);
        mVibrateOnConnect.setOnPreferenceChangeListener(this);
        int vibconnect = Settings.System.getInt(getContentResolver(),
                VIBRATE_ON_CONNECT, 0);
        mVibrateOnConnect.setChecked(vibconnect != 0);

        mVibrateOnConnectTime = (CustomSeekBarPreference) findPreference(VIBRATE_ON_CONNECT_TIME);
        int VibrateOnConnectTime = Settings.System.getInt(getContentResolver(),
                                Settings.System.VIBRATE_ON_CONNECT_TIME, 200);
        mVibrateOnConnectTime.setValue(VibrateOnConnectTime / 1);
        mVibrateOnConnectTime.setOnPreferenceChangeListener(this);

        mVibrateOnDisconnect = (SwitchPreference) findPreference(VIBRATE_ON_DISCONNECT);
        mVibrateOnDisconnect.setOnPreferenceChangeListener(this);
        int vibdisconnect = Settings.System.getInt(getContentResolver(),
                VIBRATE_ON_DISCONNECT, 0);
        mVibrateOnDisconnect.setChecked(vibdisconnect != 0);

	mVibrateOnDisconnectTime = (CustomSeekBarPreference) findPreference(VIBRATE_ON_DISCONNECT_TIME);
        int VibrateOnDisconnectTime = Settings.System.getInt(getContentResolver(),
                                Settings.System.VIBRATE_ON_DISCONNECT_TIME, 100);
        mVibrateOnDisconnectTime.setValue(VibrateOnDisconnectTime / 1);
        mVibrateOnDisconnectTime.setOnPreferenceChangeListener(this);

        mProxSpeaker = (SwitchPreference) findPreference(PROXIMITY_AUTO_SPEAKER);
        mProxSpeaker.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PROXIMITY_AUTO_SPEAKER, 0) == 1);
        mProxSpeaker.setOnPreferenceChangeListener(this);

        mProxSpeakerDelay = (ListPreference) findPreference(PROXIMITY_AUTO_SPEAKER_DELAY);
        int proxDelay = Settings.System.getInt(getContentResolver(),
                Settings.System.PROXIMITY_AUTO_SPEAKER_DELAY, 100);
        mProxSpeakerDelay.setValue(String.valueOf(proxDelay));
        mProxSpeakerDelay.setOnPreferenceChangeListener(this);
        updateProximityDelaySummary(proxDelay);

        mProxSpeakerIncallOnly = (SwitchPreference) findPreference(PROXIMITY_AUTO_SPEAKER_INCALL_ONLY);
        mProxSpeakerIncallOnly.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PROXIMITY_AUTO_SPEAKER_INCALL_ONLY, 0) == 1);
        mProxSpeakerIncallOnly.setOnPreferenceChangeListener(this);

    }

    private void updateProximityDelaySummary(int value) {
        String summary = getResources().getString(R.string.proximity_auto_speaker_delay_summary, value);
        mProxSpeakerDelay.setSummary(summary);
    }
    public boolean onPreferenceChange(Preference preference, Object newValue) {
    	  if (preference == mVibrateOnConnect) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), VIBRATE_ON_CONNECT,
                    value ? 1 : 0);
            return true;
    	} else if (preference == mVibrateOnDisconnect) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), VIBRATE_ON_DISCONNECT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVibrateOnConnectTime) {
            int valueConnect = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VIBRATE_ON_CONNECT_TIME, valueConnect * 1);
            return true;
        } else if (preference == mVibrateOnDisconnectTime) {
            int valueDisconnect = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VIBRATE_ON_DISCONNECT_TIME, valueDisconnect * 1);
            return true;
        } else if (preference == mProxSpeaker) {
            Settings.System.putInt(getContentResolver(), Settings.System.PROXIMITY_AUTO_SPEAKER,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        } else if (preference == mProxSpeakerDelay) {
            int proxDelay = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.PROXIMITY_AUTO_SPEAKER_DELAY, proxDelay);
            updateProximityDelaySummary(proxDelay);
            return true;
        } else if (preference == mProxSpeakerIncallOnly) {
            Settings.System.putInt(getContentResolver(), Settings.System.PROXIMITY_AUTO_SPEAKER_INCALL_ONLY,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        }
    
        return false;
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
