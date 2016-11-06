package com.example.fan.terrynavigation;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * Created by Fan on 2016/10/26.
 */

public class GalleryFragment extends Fragment
{
    ImageView imageview;
    View myView;
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;
    ArrayList<File> list;
    protected int imageHeight = 0;
    protected int imageWidth = 0;
    protected String imageType = "NULL";
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }
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
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("TAG", "Max memory is " + maxMemory + "KB");
        list = new ArrayList<File>();
        getAllFiles(new File("/sdcard"));
        mPhotoWall = (GridView) getActivity().findViewById(R.id.grid_view);
        adapter = new PhotoWallAdapter(getActivity(), 0, list, mPhotoWall);
        mPhotoWall.setAdapter(adapter);
        /*
        imageList = imageReader(Environment.getExternalStorageDirectory());
        //GridView is a ViewGroup that displays items in a two-dimensional, scrollable grid.
        // The grid items are automatically inserted to the layout using a ListAdapter.
        gridview = (GridView) getActivity().findViewById(R.id.grid_view);
        //AdapterView有ListView、GridView、Spinner和ExpandableListView等,
        // Adapter和AdapterView又使用了观察者模式,
       gridview.setAdapter(new GridAdapter());*/

    }
    /**
     * 获得指定目录下图片文件
     */
    private void getAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllFiles(f);
                } else {
                    if (f.getName().indexOf(".png") > 0||
                    f.getName().indexOf(".jpg") > 0||
                    f.getName().indexOf(".jpeg") > 0)
                    this.list.add(f);
                }
            }
    }
    public void onDestroy() {
        super.onDestroy();
        adapter.cancelAllTasks();// 退出程序时结束所有的下载任务
    }

    public class PhotoWallAdapter extends ArrayAdapter<File> implements
        AbsListView.OnScrollListener
{

    //记录所有正在下载或等待下载的任务。
    private Set<BitmapWorkerTask> taskCollection;
    // 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会//将最少最近使用的图片移除掉。
    private LruCache<String, Bitmap> mMemoryCache;
    //GridView的实例
    private GridView mPhotoWall;
    //第一张可见图片的下标
    private int mFirstVisibleItem;
    // 一屏有多少张图片可见
    private int mVisibleItemCount;
    // 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
    private boolean isFirstEnter = true;
    ArrayList<File> list = null;
    public PhotoWallAdapter(Context context, int textViewResourceId,
                            ArrayList<File> objects, GridView photoWall) {
        super(context, textViewResourceId, objects);
        mPhotoWall = photoWall;
        list = objects;
        taskCollection = new HashSet<BitmapWorkerTask>();
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        mPhotoWall.setOnScrollListener(this);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        final File url = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.single_gallery, null);
        } else {
            view = convertView;
        }
        final ImageView photo = (ImageView) view.findViewById(R.id.galleryPhotoView);
        // 给ImageView设置一个Tag，保证异步加载图片时不会乱序
        photo.setTag(url.getAbsolutePath());
        setImageView(url.getAbsolutePath(), photo);
        return view;
    }

    /**
     * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
     * 就给ImageView设置一张默认图片。
     * @param imageUrl
     *            图片的URL地址，用于作为LruCache的键。
     * @param imageView
     *            用于显示图片的控件。
     */
    private void setImageView(String imageUrl, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            bitmap=getLoacalBitmap(imageUrl);
            imageView.setImageResource(R.drawable.cast_abc_scrubber_control_off_mtrl_alpha);
        }
    }

    /**
     * 将一张图片存储到LruCache中。
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param bitmap
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    @SuppressLint("NewApi")
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    @SuppressLint("NewApi")
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
        if (scrollState == SCROLL_STATE_IDLE) {
            loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
        } else {
            cancelAllTasks();
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        // 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
        // 因此在这里为首次进入程序开启下载任务。
        if (isFirstEnter && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnter = false;
        }
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
     *
     * @param firstVisibleItem
     *            第一个可见的ImageView的下标
     * @param visibleItemCount
     *            屏幕中总共可见的元素数
     */
    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem
                    + visibleItemCount; i++) {
                String imageUrl = list.get(i).getAbsolutePath();
                Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
                if (bitmap == null) {//如果缓存没有
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    taskCollection.add(task);
                    task.execute(imageUrl);//执行异步任务，并传入加载的图片url地址（这里是sd卡上的图片）
                } else {
                    ImageView imageView = (ImageView) mPhotoWall
                            .findViewWithTag(imageUrl);
                    if (imageView != null && bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消所有正在下载或等待下载的任务。
     */
    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(false);
            }
        }
    }

    /**
     * 异步下载图片的任务。
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
    {

        /**
         * 图片的URL地址
         */
        private String imageUrl;
        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            // 在后台开始下载图片
            Bitmap bitmap = getLoacalBitmap(params[0]);
            if (bitmap != null) {
                // 图片下载完成后缓存到LrcCache中
                addBitmapToMemoryCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
            ImageView imageView = (ImageView) mPhotoWall
                    .findViewWithTag(imageUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }

    }
    private  Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
    //BaseAdapter是一個超好用的類別 它可以讓你自己定義許多種View
    //例如 Spinner,ListView,GridView
    //region
    /*
    class GridAdapter extends BaseAdapter
    {
        //取得有多少列
        @Override
        public int getCount()
        {
            return imageList.size();
        }

        //取得某一列的內容
        @Override
        public Object getItem(int position)
        {
            return imageList.get(position);
        }

        //取得某一列的id
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        //修改某一列View的內容
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            /*显示出来然后因为拖动而被隐藏的Item才会触发回收。*/
            //convertView - The old view to reuse, if possible.
            //convertView的含义：是代表系统最近回收的View。
            /*
                第一次打开带ListView的控件时，因为并没有回收的View,调用getVIew时，
                参数convertView的值会为null,否则将不是null,而是最近回收的View的引用.
                那么合理利用convertView将是提升Adapter效率的关键，
                否则将会产生大量的new View开销。

            convertView = getActivity().getLayoutInflater().inflate(R.layout.single_gallery, parent, false);
            imageview = (ImageView) convertView.findViewById(R.id.galleryPhotoView);
            imageview.setImageURI(Uri.parse(getItem(position).toString()));
            return convertView;
        }
    }*/

    //endregion

    //imageReader
    //region
    ArrayList<File> imageReader(File root)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        ArrayList<File> array = new ArrayList<>();
        File[] files = root.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                array.addAll(imageReader(files[i]));
            }
            else
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


}
