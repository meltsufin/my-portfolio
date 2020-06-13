// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;
import java.util.stream.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    if (request.getAttendees().isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    } else if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    } else {
      List<TimeRange> openTimes = new ArrayList<>();

      boolean[] blockedBitmap = new boolean[TimeRange.WHOLE_DAY.duration()];

      List<TimeRange> blockedTimes = events.stream()
        .filter(event -> !Collections.disjoint(event.getAttendees(), request.getAttendees()))
        .map(event -> event.getWhen())
        .sorted(TimeRange.ORDER_BY_START)
        .collect(Collectors.toList());

      // fill blockedBitmap
      for (int i = 0; i < blockedBitmap.length; i++) {
        for (TimeRange blockedTime : blockedTimes){
          if (blockedTime.contains(i)) {
            blockedBitmap[i] = true;
          }
        }
      }

      // scan blockedBitmap for available times
      int start = 0;
      int duration = 0;
      for (int i = 0; i <= blockedBitmap.length; i++) {
        if (i < blockedBitmap.length && !blockedBitmap[i]) {
          duration++;
        } else {
          if (duration > 0 && duration >= request.getDuration()) {
            openTimes.add(TimeRange.fromStartDuration(start, duration));
          }
          start = i + 1;
          duration = 0;
        }
      }

      return openTimes;
    }
  
  }
}
