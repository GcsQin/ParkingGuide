package com.parking.parkingguide.fragment;

import android.app.Activity;
import android.view.View;

import com.parking.parkingguide.R;

/**
 * Created by 37266 on 2017/4/28.
 */

public class CaijingFragment extends BaseFragment {
    Activity mActivity;
//    public CaijingFragment(Activity activity) {
//        this.mActivity = activity;
////    }

    @Override
    public View initViews() {
        rootView=View.inflate(mActivity,R.layout.frag_layout_caijing,null);
        return rootView;
    }
}
