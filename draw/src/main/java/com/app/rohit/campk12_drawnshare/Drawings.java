package com.app.rohit.campk12_drawnshare;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Drawings extends AppCompatActivity {

    Image_adapter image_adapter;
    RecyclerView recyclerView;
    TextView introtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorStatusbar));
        }
        setContentView(R.layout.activity_drawings);
     recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        introtext = (TextView)findViewById(R.id.introtext);


        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Drawings.this,MainActivity.class);
                startActivity(intent);

            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    120);

        } else {

            File targetdir = new File(Environment.getExternalStorageDirectory().getPath(), "Kraft/");

            List<String> images = new ArrayList<>();

            File[] all_images = targetdir.listFiles();

            try {

                if (all_images.length > 0) {

                    recyclerView.setVisibility(View.VISIBLE);
                    introtext.setVisibility(View.GONE);
                    for (File file : all_images) {
                        images.add(file.getAbsolutePath());
                        Log.d("ImagePaths",file.getAbsolutePath());
                    }

                    image_adapter = new Image_adapter(Drawings.this, images);
                    GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(image_adapter);
                    image_adapter.notifyDataSetChanged();

                }
                else {
                    recyclerView.setVisibility(View.GONE);
                    introtext.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception e)
            {
                recyclerView.setVisibility(View.GONE);
                introtext.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }



        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==120)
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                File targetdir = new File(Environment.getExternalStorageDirectory().getPath(), "Kraft/");

                List<String> images = new ArrayList<>();

                File[] all_images = targetdir.listFiles();

                try {

                    if (all_images.length > 0) {

                        recyclerView.setVisibility(View.VISIBLE);
                        introtext.setVisibility(View.GONE);
                        for (File file : all_images) {
                            images.add(file.getAbsolutePath());
                            Log.d("ImagePaths",file.getAbsolutePath());
                        }

                        image_adapter = new Image_adapter(Drawings.this, images);
                        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(image_adapter);
                        image_adapter.notifyDataSetChanged();

                    }
                    else {
                        recyclerView.setVisibility(View.GONE);
                        introtext.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception e)
                {
                    recyclerView.setVisibility(View.GONE);
                    introtext.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        }

    }
}
