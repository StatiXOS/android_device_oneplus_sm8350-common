/**
 * Copyright (C) 2016 The CyanogenMod project
 *               2017,2019-2020 The LineageOS Project
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

package com.statix.touchhandler;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.internal.statix.hardware.TouchscreenGesture;

import vendor.lineage.touch.V1_0.Gesture;
import vendor.lineage.touch.V1_0.ITouchscreenGesture;

import java.lang.System;
import java.util.ArrayList;
import java.util.Set;

public class TouchscreenGestureSettings extends PreferenceFragment {

    private static final String KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture";
    private static final String KEY_TOUCHSCREEN_GESTURE_SETTINGS =
            "touchscreen_gesture_settings";
    private static final String KEY_TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK =
            "touchscreen_gesture_haptic_feedback";
    private static final String TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title";

    private TouchscreenGesture[] mTouchscreenGestures;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.touchscreen_gesture_settings, rootKey);

        ActionBar actionBar = getActivity().getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        initTouchscreenGestures();
    }

    private static TouchscreenGesture[] getTouchscreenGestures() {
        try {
            final ITouchscreenGesture touchService = ITouchscreenGesture.getService(true);
            return fromHIDLGestures(touchService.getSupportedGestures());
        } catch (RemoteException e) {
        }
        return null;
    }

    private static boolean setTouchscreenGestureEnabled(TouchscreenGesture gesture, boolean state) {
        try {
            final ITouchscreenGesture touchService = ITouchscreenGesture.getService(true);
            return touchService.setGestureEnabled(toHIDLGesture(gesture), state);
        } catch (RemoteException e) {
        }
        return false;
    }

    private void initTouchscreenGestures() {
        mTouchscreenGestures = getTouchscreenGestures();
        final int[] actions = getDefaultGestureActions(getContext(), mTouchscreenGestures);
        for (final TouchscreenGesture gesture : mTouchscreenGestures) {
            getPreferenceScreen().addPreference(new TouchscreenGesturePreference(
                    getContext(), gesture, actions[gesture.id]));
        }
    }

    private class TouchscreenGesturePreference extends ListPreference {
        private final Context mContext;
        private final TouchscreenGesture mGesture;

        public TouchscreenGesturePreference(final Context context,
                                            final TouchscreenGesture gesture,
                                            final int defaultAction) {
            super(context);
            mContext = context;
            mGesture = gesture;

            setKey(buildPreferenceKey(gesture));
            setEntries(R.array.touchscreen_gesture_action_entries);
            setEntryValues(R.array.touchscreen_gesture_action_values);
            setDefaultValue(String.valueOf(defaultAction));
            setIcon(getIconDrawableResourceForAction(defaultAction));

            setSummary("%s");
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
            setTitle(gesture.name);
        }

        @Override
        public boolean callChangeListener(final Object newValue) {
            final int action = Integer.parseInt(String.valueOf(newValue));
            if (!setTouchscreenGestureEnabled(mGesture, action > 0)) {
                return false;
            }
            return super.callChangeListener(newValue);
        }

        @Override
        protected boolean persistString(String value) {
            if (!super.persistString(value)) {
                return false;
            }
            final int action = Integer.parseInt(String.valueOf(value));
            setIcon(getIconDrawableResourceForAction(action));
            sendUpdateBroadcast(mContext, mTouchscreenGestures);
            return true;
        }

        private int getIconDrawableResourceForAction(final int action) {
            switch (action) {
                case TouchscreenGestureConstants.ACTION_CAMERA:
                    return R.drawable.ic_gesture_action_camera;
                case TouchscreenGestureConstants.ACTION_FLASHLIGHT:
                    return R.drawable.ic_gesture_action_flashlight;
                case TouchscreenGestureConstants.ACTION_BROWSER:
                    return R.drawable.ic_gesture_action_browser;
                case TouchscreenGestureConstants.ACTION_DIALER:
                    return R.drawable.ic_gesture_action_dialer;
                case TouchscreenGestureConstants.ACTION_EMAIL:
                    return R.drawable.ic_gesture_action_email;
                case TouchscreenGestureConstants.ACTION_MESSAGES:
                    return R.drawable.ic_gesture_action_messages;
                case TouchscreenGestureConstants.ACTION_PLAY_PAUSE_MUSIC:
                    return R.drawable.ic_gesture_action_play_pause;
                case TouchscreenGestureConstants.ACTION_PREVIOUS_TRACK:
                    return R.drawable.ic_gesture_action_previous_track;
                case TouchscreenGestureConstants.ACTION_NEXT_TRACK:
                    return R.drawable.ic_gesture_action_next_track;
                case TouchscreenGestureConstants.ACTION_VOLUME_DOWN:
                    return R.drawable.ic_gesture_action_volume_down;
                case TouchscreenGestureConstants.ACTION_VOLUME_UP:
                    return R.drawable.ic_gesture_action_volume_up;
                case TouchscreenGestureConstants.ACTION_AMBIENT_DISPLAY:
                    return R.drawable.ic_gesture_action_ambient_display;
                default:
                    // No gesture action
                    return R.drawable.ic_gesture_action_none;
            }
        }
    }

    public static void restoreTouchscreenGestureStates(final Context context) {
        final TouchscreenGesture[] gestures = getTouchscreenGestures();
        final int[] actionList = buildActionList(context, gestures);
        for (final TouchscreenGesture gesture : gestures) {
            setTouchscreenGestureEnabled(gesture, actionList[gesture.id] > 0);
        }
        sendUpdateBroadcast(context, gestures);
    }

    private static int[] getDefaultGestureActions(final Context context,
            final TouchscreenGesture[] gestures) {
        final int[] filledDefaultActions = new int[gestures.length];
        return filledDefaultActions;
    }

    private static int[] buildActionList(final Context context,
            final TouchscreenGesture[] gestures) {
        final int[] result = new int[gestures.length];
        final int[] defaultActions = getDefaultGestureActions(context, gestures);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (final TouchscreenGesture gesture : gestures) {
            final String key = buildPreferenceKey(gesture);
            final String defaultValue = String.valueOf(defaultActions[gesture.id]);
            result[gesture.id] = Integer.parseInt(prefs.getString(key, defaultValue));
        }
        return result;
    }

    private static String buildPreferenceKey(final TouchscreenGesture gesture) {
        return "touchscreen_gesture_" + gesture.id;
    }

    private static void sendUpdateBroadcast(final Context context,
            final TouchscreenGesture[] gestures) {
        final Intent intent = new Intent(TouchscreenGestureConstants.UPDATE_PREFS_ACTION);
        final int[] keycodes = new int[gestures.length];
        final int[] actions = buildActionList(context, gestures);
        for (final TouchscreenGesture gesture : gestures) {
            keycodes[gesture.id] = gesture.keycode;
        }
        intent.putExtra(TouchscreenGestureConstants.UPDATE_EXTRA_KEYCODE_MAPPING, keycodes);
        intent.putExtra(TouchscreenGestureConstants.UPDATE_EXTRA_ACTION_MAPPING, actions);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private static TouchscreenGesture[] fromHIDLGestures(
            ArrayList<Gesture> gestures) {
        int size = gestures.size();
        TouchscreenGesture[] r = new TouchscreenGesture[size];
        for (int i = 0; i < size; i++) {
            Gesture g = gestures.get(i);
            r[i] = new TouchscreenGesture(g.id, g.name, g.keycode);
        }
        return r;
    }

    private static Gesture toHIDLGesture(TouchscreenGesture gesture) {
        Gesture g = new Gesture();
        g.id = gesture.id;
        g.name = gesture.name;
        g.keycode = gesture.keycode;
        return g;
    }

}
