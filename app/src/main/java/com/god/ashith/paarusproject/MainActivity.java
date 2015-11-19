package com.god.ashith.paarusproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
    private static final int TAKE_PICTURE_REQUEST_B = 100;
    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private Button mSaveImageButton;
    private Button mCaptureImageButton;
    private byte[] mybytearray;
    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startImageCapture();
        }
    };

    private OnClickListener mSaveImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            File saveFile = openFileForImage();
            if (saveFile != null) {
                saveImageToFile(saveFile);
            } else {
                Toast.makeText(MainActivity.this, "Unable to open file for saving image.", Toast.LENGTH_LONG).show();
            }
            //asdasdasdasdad
            new PaaruSender().execute(mybytearray);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);
        mCaptureImageButton = (Button) findViewById(R.id.capture_image_button);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        mSaveImageButton = (Button) findViewById(R.id.save_image_button);
        mSaveImageButton.setOnClickListener(mSaveImageButtonClickListener);
        mSaveImageButton.setEnabled(false);
        CameraFragment camFrag = new CameraFragment();
        getFragmentManager().beginTransaction().add(R.id.rootContainer, camFrag).commit();
        //     callAsynchronousTask();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST_B) {
            if (resultCode == RESULT_OK) {
                // Recycle the previous bitmap.
                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }
                Bundle extras = data.getExtras();
                byte[] cameraData = extras.getByteArray(CameraActivity.EXTRA_CAMERA_DATA);
                mybytearray = new byte[cameraData.length];
                System.arraycopy(cameraData, 0, mybytearray, 0, cameraData.length);
                if (cameraData != null) {
                    mSaveImageButton.setEnabled(true);
                    mCameraBitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraData.length);
                    mCameraImageView.setImageBitmap(mCameraBitmap);


                }
            } else {
                mCameraBitmap = null;
                mSaveImageButton.setEnabled(false);
            }
        }
    }

    private void startImageCapture() {

//        startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
        CameraFragment fragment = new CameraFragment();
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer, fragment).commit();
    }

    private File openFileForImage() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "com.god.ashith.paarusproject");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        /*dateFormat.format(new Date()) + */".png");
            }
        }
        return null;
    }

    private void saveImageToFile(File file) {
        if (mCameraBitmap != null) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                if (!mCameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                    Toast.makeText(MainActivity.this, "Unable to save image to file.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Saved image to: " + file.getPath(), Toast.LENGTH_LONG).show();
                }
                outStream.close();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Unable to save image to file.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            mCaptureImageButton.callOnClick();
                            mSaveImageButton.callOnClick();
                            Log.d("timer", "timing");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
    }
}

class PaaruSender extends AsyncTask<byte[], Void, Void> {

    @Override
    protected Void doInBackground(byte[]... mybytearray) {
        Socket sock;
        try {
            sock = new Socket("192.168.43.180", 10000);
            Log.d("Ashi", "Connecting...");

            // sendfile


            OutputStream os = sock.getOutputStream();
            Log.d("Ashi", "Sending");
            os.write(mybytearray[0], 0, mybytearray[0].length);
            os.flush();

            sock.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}

