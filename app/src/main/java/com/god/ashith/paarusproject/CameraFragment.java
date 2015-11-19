package com.god.ashith.paarusproject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Student on 5/20/2015.
 */
public class CameraFragment extends Fragment {
    public CameraFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View cameraView = inflater.inflate(R.layout.camera_fragment, container, false);
        return cameraView;

    }
}
