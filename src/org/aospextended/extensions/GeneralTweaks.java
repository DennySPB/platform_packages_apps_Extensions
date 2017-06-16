/*
 * Copyright (C) 2016 AospExtended ROM Project
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

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

import org.aospextended.extensions.preference.CustomSeekBarPreference;

public class GeneralTweaks extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";
    public static final String VIBRATE_ON_CONNECT = "vibrate_on_connect";
    public static final String VIBRATE_ON_DISCONNECT = "vibrate_on_disconnect";
    public static final String VIBRATE_ON_CONNECT_TIME = "vibrate_on_connect_time";
    public static final String VIBRATE_ON_DISCONNECT_TIME = "vibrate_on_disconnect_time";
    private static final String SCREENSHOT_TYPE = "screenshot_type";
    private static final String SCREENSHOT_DELAY = "screenshot_delay";
    private static final String WIRED_RINGTONE_FOCUS_MODE = "wired_ringtone_focus_mode";
    private static final String HEADSET_CONNECT_PLAYER = "headset_connect_player";

    private SwitchPreference mVibrateOnConnect;
    private SwitchPreference mVibrateOnDisconnect;
    private CustomSeekBarPreference mScreenshotDelay;
    private CustomSeekBarPreference mVibrateOnConnectTime;
    private CustomSeekBarPreference mVibrateOnDisconnectTime;
    private ListPreference mMsob;
    private ListPreference mScreenshotType;
    private ListPreference mWiredHeadsetRingtoneFocus;
    private ListPreference mLaunchPlayerHeadsetConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.general_tweaks);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

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

        mMsob = (ListPreference) findPreference(PREF_MEDIA_SCANNER_ON_BOOT);
        mMsob.setValue(String.valueOf(Settings.System.getInt(resolver,
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0)));
        mMsob.setSummary(mMsob.getEntry());
        mMsob.setOnPreferenceChangeListener(this);

        mScreenshotType = (ListPreference) findPreference(SCREENSHOT_TYPE);
        int mScreenshotTypeValue = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_TYPE, 0);
        mScreenshotType.setValue(String.valueOf(mScreenshotTypeValue));
        mScreenshotType.setSummary(mScreenshotType.getEntry());
        mScreenshotType.setOnPreferenceChangeListener(this);

        mScreenshotDelay = (CustomSeekBarPreference) findPreference(SCREENSHOT_DELAY);
        int screenshotDelay = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_DELAY, 1000);
        mScreenshotDelay.setValue(screenshotDelay / 1);
        mScreenshotDelay.setOnPreferenceChangeListener(this);

        mWiredHeadsetRingtoneFocus = (ListPreference) findPreference(WIRED_RINGTONE_FOCUS_MODE);
        int mWiredHeadsetRingtoneFocusValue = Settings.Global.getInt(resolver,
                Settings.Global.WIRED_RINGTONE_FOCUS_MODE, 1);
        mWiredHeadsetRingtoneFocus.setValue(Integer.toString(mWiredHeadsetRingtoneFocusValue));
        mWiredHeadsetRingtoneFocus.setSummary(mWiredHeadsetRingtoneFocus.getEntry());
        mWiredHeadsetRingtoneFocus.setOnPreferenceChangeListener(this);

        mLaunchPlayerHeadsetConnection = (ListPreference) findPreference(HEADSET_CONNECT_PLAYER);
        int mLaunchPlayerHeadsetConnectionValue = Settings.System.getIntForUser(resolver,
                Settings.System.HEADSET_CONNECT_PLAYER, 0, UserHandle.USER_CURRENT);
        mLaunchPlayerHeadsetConnection.setValue(Integer.toString(mLaunchPlayerHeadsetConnectionValue));
        mLaunchPlayerHeadsetConnection.setSummary(mLaunchPlayerHeadsetConnection.getEntry());
        mLaunchPlayerHeadsetConnection.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.EXTENSIONS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMsob) {
            Settings.System.putInt(resolver,
                    Settings.System.MEDIA_SCANNER_ON_BOOT,
                    Integer.valueOf(String.valueOf(newValue)));
            mMsob.setValue(String.valueOf(newValue));
            mMsob.setSummary(mMsob.getEntry());
            return true;
    	} else if (preference == mVibrateOnConnect) {
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
	}else if  (preference == mScreenshotType) {
            int mScreenshotTypeValue = Integer.parseInt(((String) newValue).toString());
            mScreenshotType.setSummary(
                    mScreenshotType.getEntries()[mScreenshotTypeValue]);
            Settings.System.putInt(resolver,
                    Settings.System.SCREENSHOT_TYPE, mScreenshotTypeValue);
            mScreenshotType.setValue(String.valueOf(mScreenshotTypeValue));
            return true;
        } else if (preference == mScreenshotDelay) {
            int screenshotDelay = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.SCREENSHOT_DELAY, screenshotDelay * 1);
            return true;
        } else if (preference == mWiredHeadsetRingtoneFocus) {
            int mWiredHeadsetRingtoneFocusValue = Integer.valueOf((String) newValue);
            int index = mWiredHeadsetRingtoneFocus.findIndexOfValue((String) newValue);
            mWiredHeadsetRingtoneFocus.setSummary(
                    mWiredHeadsetRingtoneFocus.getEntries()[index]);
            Settings.Global.putInt(resolver, Settings.Global.WIRED_RINGTONE_FOCUS_MODE,
                    mWiredHeadsetRingtoneFocusValue);
            return true;
        } else if (preference == mLaunchPlayerHeadsetConnection) {
            int mLaunchPlayerHeadsetConnectionValue = Integer.valueOf((String) newValue);
            int index = mLaunchPlayerHeadsetConnection.findIndexOfValue((String) newValue);
            mLaunchPlayerHeadsetConnection.setSummary(
                    mLaunchPlayerHeadsetConnection.getEntries()[index]);
            Settings.System.putIntForUser(resolver, Settings.System.HEADSET_CONNECT_PLAYER,
                    mLaunchPlayerHeadsetConnectionValue, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }
}
