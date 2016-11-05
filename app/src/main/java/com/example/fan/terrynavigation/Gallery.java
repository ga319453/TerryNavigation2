package com.example.fan.terrynavigation;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Fan on 2016/10/26.
 */

public class Gallery extends Fragment
{
    View myView;
    GridView gridview;
    ArrayList<File> list;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    //BaseAdapter是一個超好用的類別 它可以讓你自己定義許多種View
    //例如 Spinner,ListView,GridView
    class GridAdapter extends BaseAdapter
    {
        //取得有多少列
        @Override
        public int getCount()
        {
            return list.size();
        }

        //取得某一列的內容
        @Override
        public Object getItem(int position)
        {
            return list.get(position);
        }

        //取得某一列的id
        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        //修改某一列View的內容
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            convertView = getActivity().getLayoutInflater().inflate(R.layout.single_gallery, parent, false);
            ImageView image = (ImageView) convertView.findViewById(R.id.galleryPhotoView);
            image.setImageURI(Uri.parse(getItem(position).toString()));
            return convertView;
        }
    }

    //region
    ArrayList<File> imageReader(File root)
    {
        ArrayList<File> array = new ArrayList<>();
        File[] files = root.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                array.addAll(imageReader(files[i]));
            } else
            {
                if (files[i].getName().endsWith(".jpg"))
                {
                    array.add(files[i]);
                }
            }
        }
        return array;
    }
    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.gallery, container, false);
        return myView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        list = imageReader(Environment.getExternalStorageDirectory());
        //GridView is a ViewGroup that displays items in a two-dimensional, scrollable grid.
        // The grid items are automatically inserted to the layout using a ListAdapter.
        gridview = (GridView) getActivity().findViewById(R.id.gridView);
        //AdapterView有ListView、GridView、Spinner和ExpandableListView等,
        // Adapter和AdapterView又使用了观察者模式,
        gridview.setAdapter(new GridAdapter());
    }
}
