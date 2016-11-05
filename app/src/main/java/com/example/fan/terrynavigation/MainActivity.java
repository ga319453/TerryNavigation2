package com.example.fan.terrynavigation;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
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
import android.content.ContentValues;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileNotFoundException;

import static com.example.fan.terrynavigation.R.id.imageView;
import static com.example.fan.terrynavigation.R.id.nav_camera;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    // private DisplayMetrics mPhone;
    Button button;
    ImageView imageView;
    static final int CAM__REQUEST = 1;
    //region
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*A structure describing general information about a display,
        // such as its size, density, and font scaling.
        mPhone = new DisplayMetrics(); //讀取手機解析度
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);*/
        button = (Button) findViewById(R.id.nav_camera);
        imageView = (ImageView) findViewById(R.id.appbar_image_view);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //建立側選單觸發器， 其中會將 DrawerLayout(layDrawer) 及 R.drawable.ic_drawer (側選單的三條線圖示)
        // 等等參數指定給 ActionBarDrawerToggle 。

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    //endregion
    public Bitmap photo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String path = "sdcard/camera_app/cam_image.jpg";
        //imageView.setImageDrawable(Drawable.createFromPath(path));
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            //取得照片路徑uri
            Uri uri = data.getData();
        }
        Bundle extras = data.getExtras();
        photo = (Bitmap) extras.get("data");
        Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public File getFile() {
        File folder = new File("sdcard/camera_app");

        if (!folder.exists()) {
            folder.mkdir();
        }
        File image_file = new File(folder, "cam_image.jpg");
        return image_file;
    }

    //處理 App icon 的動作，這樣當你觸碰 App icon 時就可以開關 drawer 了。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentManager fragmentManager2 = getSupportFragmentManager();
        if (id == R.id.nav_helloworld) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HelloWorld()).commit();
        } else if (id == R.id.nav_camera) {
            Intent callCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            callCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE); //capture an image and return it;
            startActivityForResult(callCamera, ACTIVITY_START_CAMERA_APP);
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Camera()).commit();
        } else if (id == R.id.nav_gps) {
            fragmentManager2.beginTransaction().replace(R.id.content_frame, new GPS()).commit();
        } else if (id == R.id.nav_gallary) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Gallary()).commit();
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
