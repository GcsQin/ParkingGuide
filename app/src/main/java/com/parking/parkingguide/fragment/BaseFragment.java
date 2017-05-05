package com.parking.parkingguide.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ChumFuchiu on 2017/4/28.
 */

public abstract class BaseFragment extends Fragment {
    public Activity baseActivity;
    public View rootView;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity=getActivity();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initViews();
    }
    public abstract View initViews();
    public void initDatas(){
    }
}
