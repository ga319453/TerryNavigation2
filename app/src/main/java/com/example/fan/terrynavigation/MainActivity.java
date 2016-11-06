package com.example.fan.terrynavigation;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    // private DisplayMetrics mPhone;
    Button button;
    ImageView imageView;
    static final int CAM__REQUEST = 1;
    public Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        //沒意義
        //region
        //structure describing general information about a display,
        //such as its size, density, and font scaling.
        //mPhone = new DisplayMetrics(); //讀取手機解析度
        //getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        //endregion
        //宣告
        //region
        imageView = (ImageView) findViewById(R.id.appbar_image_view);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //endregion
        //Nav_Bar
        //region
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);  //DrawerLayout 則是 抽屜物件 提供滑動功能的元件 。
        //Actionbar是一個很常用的工具, 幾乎每個app都會使用它。
        //Actionbar早期也有很多第三方可以支援, 但是一樣過於複雜,
        // Android也經歷過幾次Actionbar改版, 最後開發出一個元件-Toolbar,
        //ActionBarDrawerToggle 是什麼呢? 字面上的直接翻譯為 : 作動列滑動觸發器( or 抽屜觸發器)。
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //建立側選單觸發器， 其中會將 DrawerLayout(layDrawer) 及 R.drawable.ic_drawer (側選單的三條線圖示)
        // 等等參數指定給 ActionBarDrawerToggle 。
        //要實作左側開合選單，我們必須使用到 DrawerLayout 這個佈局，這個佈局裡只可以包含兩個 Layout，
        // 第一個 Layout 代表主要的介面，第二個 Layout 則代表了左側開合選單的介面
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //endregion
    }


    //藉由requestCode以便確認返回數據的是哪個Activity，
    //resultCode則對應至setResult中傳送的resultCode，Intent對象則用來傳送返回的資料。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //String path = "sdcard/camera_app/cam_image.jpg";
        //imageView.setImageDrawable(Drawable.createFromPath(path));
        //在一個主界面(主Activity)上能連接往許多不同子功能模塊(子Activity)，
        //當子模塊的事情做完之後就回到主界面，或許還同時返回一些子模塊完成的數據交給主Activity處理。
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK)
        {
            //取得照片路徑uri
            Uri uri = data.getData();
        }

        Bundle extras = data.getExtras();
        photo = (Bitmap) extras.get("data");
        Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //關閉側邊選單(navBar)
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public File getFile()
    {
        File folder = new File("sdcard/camera_app");

        if (!folder.exists())
        {
            folder.mkdir();
        }
        File image_file = new File(folder, "cam_image.jpg");
        return image_file;
    }

    //處理 App icon 的動作，這樣當你觸碰 App icon 時就可以開關 drawer 了。
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        Fragment fragment = null;
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentManager fragmentManager2 = getSupportFragmentManager();
        if (id == R.id.nav_helloworld)
        {
            fragment = new HelloWorldFragment();
        } else if (id == R.id.nav_camera)
        {
            Intent callCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            callCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE); //capture an image and return it;
            startActivityForResult(callCamera, ACTIVITY_START_CAMERA_APP);
            fragment = new PhotoViewFragment();
        } else if (id == R.id.nav_gps)
        {
            fragmentManager2.beginTransaction().replace(R.id.content_frame, new MapViewFragment()).commit();
        } else if (id == R.id.nav_gallary)
        {
            fragment = new GalleryFragment();
        } else if (id == R.id.nav_send)
        {

        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // GravityCompat
        // Compatibility shim for accessing newer functionality from Gravity.
        return true;
    }


}
