/*
 * Copyright (C) 2019 The LineageOS Project
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

#define LOG_TAG "TouchscreenGestureService"

#include "TouchscreenGesture.h"
#include <android-base/logging.h>
#include <fstream>

namespace vendor {
namespace lineage {
namespace touch {
namespace V1_0 {
namespace implementation {

/*
#define KEY_F5                  63
#define KEY_F6                  64
#define KEY_F7                  65
#define KEY_F8                  66
#define KEY_F9                  67
#define KEY_GESTURE_W               246
#define KEY_GESTURE_M               247
#define KEY_GESTURE_S               248
#define KEY_DOUBLE_TAP              KEY_WAKEUP
#define KEY_GESTURE_CIRCLE          250
#define KEY_GESTURE_TWO_SWIPE       251
#define KEY_GESTURE_UP_ARROW        252
#define KEY_GESTURE_LEFT_ARROW      253
#define KEY_GESTURE_RIGHT_ARROW     254
#define KEY_GESTURE_DOWN_ARROW      255
#define KEY_GESTURE_SWIPE_LEFT      KEY_F5
#define KEY_GESTURE_SWIPE_DOWN      KEY_F6
#define KEY_GESTURE_SWIPE_RIGHT     KEY_F7
#define KEY_GESTURE_SWIPE_UP        KEY_F8
#define KEY_GESTURE_SINGLE_TAP      KEY_F9
*/
const std::map<int32_t, TouchscreenGesture::GestureInfo> TouchscreenGesture::kGestureInfoMap = {
    {0, {KEY_GESTURE_TWO_SWIPE, "Two fingers down swipe", "/proc/touchpanel/double_swipe_enable"}},
    {1, {KEY_GESTURE_DOWN_ARROW, "Down arrow", "/proc/touchpanel/down_arrow_enable"}},
    {2, {KEY_GESTURE_LEFT_ARROW, "Left arrow", "/proc/touchpanel/left_arrow_enable"}},
    {3, {KEY_GESTURE_RIGHT_ARROW, "Right arrow", "/proc/touchpanel/right_arrow_enable"}},
    {4, {KEY_GESTURE_M, "Letter M", "/proc/touchpanel/letter_m_enable"}},
    {5, {KEY_GESTURE_CIRCLE, "Letter O", "/proc/touchpanel/letter_o_enable"}},
    {6, {KEY_GESTURE_S, "Letter S", "/proc/touchpanel/letter_s_enable"}},
    {7, {KEY_GESTURE_W, "Letter W", "/proc/touchpanel/letter_w_enable"}},
    {8, {KEY_GESTURE_SINGLE_TAP, "Single Tap", "/proc/touchpanel/single_tap_enable"}},
};

Return<void> TouchscreenGesture::getSupportedGestures(getSupportedGestures_cb resultCb) {
    std::vector<Gesture> gestures;

    for (const auto& entry : kGestureInfoMap) {
        gestures.push_back({entry.first, entry.second.name, entry.second.keycode});
    }
    resultCb(gestures);

    return Void();
}

Return<bool> TouchscreenGesture::setGestureEnabled(
    const ::vendor::lineage::touch::V1_0::Gesture& gesture, bool enabled) {
    const auto entry = kGestureInfoMap.find(gesture.id);
    if (entry == kGestureInfoMap.end()) {
        return false;
    }

    std::ofstream file(entry->second.path);
    file << (enabled ? "1" : "0");
    LOG(DEBUG) << "Wrote file " << entry->second.path << " fail " << file.fail();
    return !file.fail();
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace touch
}  // namespace lineage
}  // namespace vendor
