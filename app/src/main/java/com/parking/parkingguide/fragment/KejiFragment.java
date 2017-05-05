package com.parking.parkingguide.fragment;

import android.app.Activity;
import android.view.View;

import com.parking.parkingguide.R;

/**
 * Created by 37266 on 2017/4/28.
 */

public class KejiFragment extends BaseFragment {
    Activity mActivity;
//    public KejiFragment(Activity mActivity) {
//        this.mActivity = mActivity;
//    }

    @Override
    public View initViews() {
        rootView=View.inflate(mActivity,R.layout.frag_layout_keji,null);
        return rootView;
    }
}
