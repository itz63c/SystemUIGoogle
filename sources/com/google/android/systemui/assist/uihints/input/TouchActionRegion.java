package com.google.android.systemui.assist.uihints.input;

import android.graphics.Region;
import java.util.Optional;

public interface TouchActionRegion {
    Optional<Region> getTouchActionRegion();
}
