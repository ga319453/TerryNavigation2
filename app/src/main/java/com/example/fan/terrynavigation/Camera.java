package com.example.fan.terrynavigation;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Fan on 2016/10/26.
 */

public class Camera extends Fragment{
    ImageView result_photo;
    View myView;
    @Nullable   //可以用来标识特定的参数或者返回值可以为null
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.camera,container,false); //将一个用xml定义的布局文件查找出来

        return myView;
    }

    @Override
    public void onResume() {
        result_photo = (ImageView)getActivity().findViewById(R.id.imageView2);
        result_photo.setImageBitmap(((MainActivity)getActivity()).photo);
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
