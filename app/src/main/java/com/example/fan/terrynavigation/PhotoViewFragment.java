package com.example.fan.terrynavigation;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Fan on 2016/10/26.
 */

public class PhotoViewFragment extends Fragment
{
    ImageView result_photo;
    View myView;

    @Nullable   //可以用来标识特定的参数或者返回值可以为null
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.camera_photo_view, container, false); //将一个用xml定义的布局文件查找出来

        return myView;
    }

    @Override
    public void onResume()
    {
        result_photo = (ImageView) getActivity().findViewById(R.id.photoView);
        result_photo.setImageBitmap(((MainActivity) getActivity()).photo);
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }
}
