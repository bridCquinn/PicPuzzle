package com.example.picpuzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;

import static java.sql.DriverManager.println;


public class chunkImage extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

//        // Getting drawable image via drawable-hdpi folder and covert into bitmap.
//        Bitmap originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//        // column/row number
//        int edge = originalImage.getWidth()/5;
//        // matrix to hold the images created
//        Rect[][] chunks = new Rect[5][5];
//        // spacing (white/black outline between squares)
//        int spacing = originalImage.getWidth()/25;
//        // iterating through the chunk matrix
//
//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; j++) {
//                chunks[i][j] = new Rect(i*edge, j*edge, edge, edge);
//                Canvas myCanvas = new Canvas();
//                myCanvas.drawBitmap(originalImage, chunks[i][j], chunks[i][j], null);
//            }
//        }
//
        ImageView imageView= (ImageView) findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.sunset);

        Button upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                 checks if the image matches the amount of color
//
//                 uploads and replaces a tile with the image
//                 needs to take path of gallery photo
//                imageView.setImageResource(R.drawable.new_image);
//
            }

        });


        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), endgame.class);
                startActivity(myIntent);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }
}

