package ru.fargus.testapp.ui.map.constants;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.PatternItem;

import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 02.02.18.
 */

public class MapConfig {

    public static final String MAP_ARRIVAL_PARAM = "arrival";
    public static final String MAP_DEPARTURE_PARAM = "departure";
    public static final long MAP_MARKER_MOVEMENT_ANIMATION_DURATION = 7000;

    public static final int MAP_PATTERN_GAP_LENGTH_PX = 12;
    public static final int MAP_POLYLINE_STROKE_WIDTH_PX = 10;

    public static final Dot MAP_DOT = new Dot();
    public static final Gap MAP_GAP = new Gap(MAP_PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(MAP_DOT, MAP_GAP);
}
