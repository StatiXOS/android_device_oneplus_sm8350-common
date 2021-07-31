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

#ifndef VENDOR_LINEAGE_TOUCH_V1_0_TOUCHSCREENGESTURE_H
#define VENDOR_LINEAGE_TOUCH_V1_0_TOUCHSCREENGESTURE_H

#include <hidl/MQDescriptor.h>
#include <hidl/Status.h>
#include <vendor/lineage/touch/1.0/ITouchscreenGesture.h>
#include <map>

namespace vendor {
namespace lineage {
namespace touch {
namespace V1_0 {
namespace implementation {

using ::android::hardware::Return;
using ::android::hardware::Void;
using ::android::sp;

#define KEY_GESTURE_W               246
#define KEY_GESTURE_M               247
#define KEY_GESTURE_S               248
#define KEY_GESTURE_CIRCLE          250
#define KEY_GESTURE_TWO_SWIPE       251
#define KEY_GESTURE_UP_ARROW        252
#define KEY_GESTURE_LEFT_ARROW      253
#define KEY_GESTURE_RIGHT_ARROW     254
#define KEY_GESTURE_DOWN_ARROW      255
#define KEY_GESTURE_SWIPE_LEFT      63
#define KEY_GESTURE_SWIPE_DOWN      64
#define KEY_GESTURE_SWIPE_RIGHT     65
#define KEY_GESTURE_SWIPE_UP        66
#define KEY_GESTURE_SINGLE_TAP      67

class TouchscreenGesture : public ITouchscreenGesture {
  public:
    // Methods from ::vendor::lineage::touch::V1_0::ITouchscreenGesture follow.
    Return<void> getSupportedGestures(getSupportedGestures_cb resultCb) override;
    Return<bool> setGestureEnabled(const ::vendor::lineage::touch::V1_0::Gesture& gesture,
                                   bool enabled) override;

  private:
    typedef struct {
        int32_t keycode;
        const char* name;
        const char* path;
    } GestureInfo;
    static const std::map<int32_t, GestureInfo> kGestureInfoMap;  // id -> info
};

}  // namespace implementation
}  // namespace V1_0
}  // namespace touch
}  // namespace lineage
}  // namespace vendor

#endif  // VENDOR_LINEAGE_TOUCH_V1_0_TOUCHSCREENGESTURE_H
