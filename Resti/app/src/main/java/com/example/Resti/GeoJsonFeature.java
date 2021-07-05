package com.example.Resti;

import com.google.android.gms.maps.model.LatLngBounds;


/**
 * A GeoJsonFeature has a geometry, bounding box, id and set of properties. Styles are also stored
 * in this class.
 */
public class GeoJsonFeature  {

    public GeoJsonFeature(String mId, LatLngBounds mBoundingBox) {
        this.mId = mId;
        this.mBoundingBox = mBoundingBox;
    }

    private final String mId;

    private final LatLngBounds mBoundingBox;


}
