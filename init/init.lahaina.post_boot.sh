#
# Copyright (C) 2023 StatiXOS
# SPDX-License-Identifier: Apache-2.0
#

# Custom tuning for Lahaina SoC

# Runtime fs tuning
write /sys/block/sda/queue/read_ahead_kb 128
write /sys/block/sda/queue/nr_requests 128
write /sys/block/sda/queue/iostats 1
write /sys/block/dm-0/queue/read_ahead_kb 128
write /sys/block/dm-1/queue/read_ahead_kb 128
write /sys/block/dm-2/queue/read_ahead_kb 128
write /sys/block/dm-3/queue/read_ahead_kb 128
write /sys/block/dm-4/queue/read_ahead_kb 128
write /sys/block/dm-5/queue/read_ahead_kb 128
write /sys/block/dm-6/queue/read_ahead_kb 128
write /sys/block/dm-7/queue/read_ahead_kb 128
write /sys/block/dm-8/queue/read_ahead_kb 128
write /sys/block/dm-9/queue/read_ahead_kb 128

# cpuset
echo 0-1 > /dev/cpuset/background/cpus
echo 0-3 > /dev/cpuset/system-background/cpus
