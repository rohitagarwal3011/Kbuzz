package com.app.rohit.campk12_drawnshare;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.jar.*;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PaintAreaView paintAreaView;
    FrameLayout frame;
    CMYColor cmy;

    private Bitmap mBitmap;
    View mView;
    File mypath;
    LinearLayout undo_redo_btns;

    String tempDir;
    int brushsizevalue = 5;
    int erasersizevalue = 3;
    private ImageView mSizeBrush;
    private ImageView mColorSelect;
    private ImageView mEraser;
    private ImageView mSave;
    private ImageView mClean;
    private FrameLayout mFrame;
    private ImageView mUndo;
    private ImageView mRedo;
    private LinearLayout mRedoBtnsUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initView();
        frame = (FrameLayout) findViewById(R.id.frame);
        undo_redo_btns = (LinearLayout) findViewById(R.id.undo_redo_btns);

        cmy = new CMYColor();
        paintAreaView = new PaintAreaView(this);


        cmy = CMYColor.fromRGB(0xffff0000);
        paintAreaView.setPaintColor(cmy.getRGB());


        // paintAreaView.setId(42);
        frame.addView(paintAreaView);

//        Button save_btn = new Button(this);
//        save_btn.setText("Pick COlor");
//        save_btn.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        save_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                show_color_picker();
////                saveas(frame);
////                paintAreaView.clear();
//            }
//        });

//        Button increase_size = new Button(this);
//        increase_size.setText("Undo");
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0, 100, 0, 0);
//        increase_size.setLayoutParams(layoutParams);
//        increase_size.setGravity(Gravity.LEFT);
//        increase_size.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                paintAreaView.setBrushSize(paintAreaView.currentBrushSize()+1);
//                paintAreaView.onClickUndo();
//            }
//        });
//
//        Button decrease_size = new Button(this);
//        decrease_size.setText("Redo");
//
//        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams1.setMargins(200, 100, 0, 0);
//        decrease_size.setLayoutParams(layoutParams1);
//        decrease_size.setGravity(Gravity.RIGHT);
//        decrease_size.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paintAreaView.onClickRedo();
////                 paintAreaView.setBrushSize(paintAreaView.currentBrushSize()-1);
//            }
//        });


//        SeekBar brushsize = new SeekBar(this);
//        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams2.setMargins(0, 200, 0, 0);
//        brushsize.setLayoutParams(layoutParams2);
//        brushsize.setMax(10);
//        brushsize.setMin(1);


//        frame.addView(save_btn);
//        frame.addView(increase_size);
//        frame.addView(decrease_size);
//        frame.addView(brushsize);


    }


    public void hide() {
        undo_redo_btns.setVisibility(View.GONE);
    }

    public void show() {
        undo_redo_btns.setVisibility(View.VISIBLE);
    }

    public void show_color_picker() {
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("Choose color")
                .initialColor(cmy.getRGB())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(20)
                .lightnessSliderOnly()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        cmy = CMYColor.fromRGB(selectedColor);
                       // paintAreaView.change_background_color(cmy.getRGB());
                         paintAreaView.setPaintColor(cmy.getRGB());
                        paintAreaView.setBrushSize(brushsizevalue);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent();
        intent.putExtra("URI","notavailabe");
        setResult(1,intent);
        finish();
    }

    public void saveas(View v) {
        mBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        v.draw(canvas);


        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            Toast.makeText(MainActivity.this,"Saved!!",Toast.LENGTH_SHORT).show();

            Intent intent=new Intent();
            intent.putExtra("URI",filename);
            setResult(2,intent);
            finish();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Kraft/");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

//    public void save(View v) {
//        Log.v("log_tag", "Width: " + v.getWidth());
//        Log.v("log_tag", "Height: " + v.getHeight());
////        if(mBitmap != null)
////        {
//        mBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
//        ;
////        }
//        Canvas canvas = new Canvas(mBitmap);
//        try {
//            tempDir = Environment.getExternalStorageDirectory() + "/" + "Drawing" + "/";
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir("Drawing", Context.MODE_PRIVATE);
//
//            prepareDirectory();
//            String uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
//            String current = uniqueId + ".png";
//            mypath = new File(directory, current);
//            FileOutputStream mFileOutStream = new FileOutputStream(mypath);
//
//            v.draw(canvas);
//            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
//            mFileOutStream.flush();
//            mFileOutStream.close();
//            String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Drawing", null);
//            Log.v("log_tag", "url: " + url);
//            //In case you want to delete the file
//            //boolean deleted = mypath.delete();
//            //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
//            //If you want to convert the image to string use base64 converter
//
//        } catch (Exception e) {
//            Log.v("log_tag", e.toString());
//        }
//    }
//
//
//    private String getTodaysDate() {
//
//        final Calendar c = Calendar.getInstance();
//        int todaysDate = (c.get(Calendar.YEAR) * 10000) +
//                ((c.get(Calendar.MONTH) + 1) * 100) +
//                (c.get(Calendar.DAY_OF_MONTH));
//        Log.w("DATE:", String.valueOf(todaysDate));
//        return (String.valueOf(todaysDate));
//
//    }
//
//    private String getCurrentTime() {
//
//        final Calendar c = Calendar.getInstance();
//        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
//                (c.get(Calendar.MINUTE) * 100) +
//                (c.get(Calendar.SECOND));
//        Log.w("TIME:", String.valueOf(currentTime));
//        return (String.valueOf(currentTime));
//
//    }
//
//
//    private boolean prepareDirectory() {
//        try {
//            if (makedirs()) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Could not initiate File System.. Is Sdcard mounted properly?", Toast.LENGTH_LONG).show();
//            return false;
//        }
//    }
//
//    private boolean makedirs() {
//        File tempdir = new File(tempDir);
//        if (!tempdir.exists())
//            tempdir.mkdirs();
//
//        if (tempdir.isDirectory()) {
//            File[] files = tempdir.listFiles();
//            for (File file : files) {
//                if (!file.delete()) {
//                    Log.d("", "Failed to delete " + file);
//                }
//            }
//        }
//        return (tempdir.isDirectory());
//    }


    public void show_dialog(final int i ) // i=1 when brush size is changed and i = 2 when rubber size needs to be changed
    {

        final Dialog dialog  = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_seek_bar);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        SeekBar brush_size = (SeekBar)dialog.findViewById(R.id.seekbar);
        TextView seekbar_text = (TextView)dialog.findViewById(R.id.seekbar_text);
        Button cancel_button = (Button) dialog.findViewById(R.id.cancel);
        Button ok_button = (Button) dialog.findViewById(R.id.ok);

        if(i==1)
        {
            seekbar_text.setText("Choose Brush Size");

            brush_size.setProgress(brushsizevalue);
        }

        if(i==2)
        {
            seekbar_text.setText("Choose Eraser Size");
            brush_size.setProgress(erasersizevalue);
        }

        brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(i==1)
                brushsizevalue = progress;
                else
                    erasersizevalue=progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==1) {
                    paintAreaView.setPaintColor(cmy.getRGB());
                    paintAreaView.setBrushSize(brushsizevalue);
                }
                else {
                    paintAreaView.setPaintColor(CMYColor.fromRGB(Color.WHITE).getRGB());
                    paintAreaView.setBrushSize(erasersizevalue);
                }
                dialog.dismiss();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void initView() {
        mSizeBrush = (ImageView) findViewById(R.id.brush_size);
        mSizeBrush.setOnClickListener(this);
        mColorSelect = (ImageView) findViewById(R.id.select_color);
        mColorSelect.setOnClickListener(this);
        mEraser = (ImageView) findViewById(R.id.eraser);
        mEraser.setOnClickListener(this);
        mSave = (ImageView) findViewById(R.id.save);
        mSave.setOnClickListener(this);
        mClean = (ImageView) findViewById(R.id.clean);
        mClean.setOnClickListener(this);
        mFrame = (FrameLayout) findViewById(R.id.frame);
        mUndo = (ImageView) findViewById(R.id.undo);
        mUndo.setOnClickListener(this);
        mRedo = (ImageView) findViewById(R.id.redo);
        mRedo.setOnClickListener(this);
        mRedoBtnsUndo = (LinearLayout) findViewById(R.id.undo_redo_btns);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.brush_size) {//TODO

            show_dialog(1);

        } else if (i == R.id.select_color) {//TODO
            show_color_picker();

        } else if (i == R.id.eraser) {//TODO
            show_dialog(2);

        } else if (i == R.id.save) {//TODO

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Camera permission has not been granted.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        120);

            } else {

                saveas(frame);

            }


        } else if (i == R.id.clean) {//TODO
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Won't be able to recover this!")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            paintAreaView.clear();
                            sDialog.cancel();
                        }
                    })
                    .show();


        } else if (i == R.id.undo) {//TODO
            paintAreaView.onClickUndo();

        } else if (i == R.id.redo) {//TODO
            paintAreaView.onClickRedo();

        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==120)
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveas(frame);
            }
        }

    }
}
