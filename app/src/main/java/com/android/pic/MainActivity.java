package com.android.pic;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.core.Scalar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.media.Image;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;

    private static ImageColourAnalysis ICA = new ImageColourAnalysis();

    private static final int SIDE_LEN = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // this method will be invoked when the user chooses to select a stock image
    public void onStockImageClicked(View v){
        Button mainButton1 = (Button) v;
//        startActivity(new Intent(getApplicationContext(), stock_list.class));
    }

    // this method will be invoked when the user chooses to select image from gallery
    public void onImageGalleryClicked(View v){
        // invoke the image gallery using an implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type. get all image types
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity and get something back from it
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            // if we are here, everything processed successfully
            if (requestCode == IMAGE_GALLERY_REQUEST){
                // if we are here, we are hearing back from the image gallery
                // the address of the image on the SD card
                Uri imageUri = data.getData();
//                // declare a stream to read the image data from the SD card
//                InputStream inputStream;
//                // we are getting an input stream, based on the URI of the image
//                try {
//                    inputStream = getContentResolver().openInputStream(imageUri);
//                    // get a bitmap from the stream
//                    Bitmap image = BitmapFactory.decodeStream(inputStream);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    // show a message to the user indicating that the image is unavailable
//                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG.show());
//                }
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap img = Bitmap.createBitmap(SIDE_LEN, SIDE_LEN, conf);
                try {
                    Bitmap bmp = getBitmapFromLocalPath(getRealPathFromURI(imageUri),1);
                    //Bitmap bmp = BitmapFactory.decodeStream(new BufferedInputStream(new URL(imageUri.toString()).openConnection().connect().getInputStream()));
                    int xOffset = bmp.getWidth() / 2 - (SIDE_LEN / 2);
                    int yOffset = bmp.getHeight() / 2 - (SIDE_LEN / 2);
                    for(int x = 0; x < SIDE_LEN; x++)
                    {
                        for(int y = 0; y < SIDE_LEN; y++)
                        {
                            img.setPixel(x,y, bmp.getPixel(x + xOffset, y + yOffset));
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                float R = 0, G = 0, B = 0;
                for(int x = 0; x < SIDE_LEN; x++)
                {
                    for(int y = 0; y < SIDE_LEN; y++)
                    {
                        R += ((img.getPixel(x, y) >> 16) & 0xff) / (double)(SIDE_LEN * SIDE_LEN);
                        G += ((img.getPixel(x, y) >>  8) & 0xff) / (double)(SIDE_LEN * SIDE_LEN);
                        B += ((img.getPixel(x, y) >>  0) & 0xff) / (double)(SIDE_LEN * SIDE_LEN);
                    }
                }

                Color avg = Color.valueOf(R, G, B);
                //Scalar colour = ICA.getRGB(imageUri,500);

                System.out.println("Colour is: "+avg.toString());
            }
        }
    }

    /**
     *
     * @param path
     * @param sampleSize 1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
     * @return
     */
    public static Bitmap getBitmapFromLocalPath(String path, int sampleSize)
    {
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            return BitmapFactory.decodeFile(path, options);
        }
        catch(Exception e)
        {
            //  Logger.e(e.toString());
        }

        return null;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
