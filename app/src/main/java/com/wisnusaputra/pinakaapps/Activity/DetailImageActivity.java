package com.wisnusaputra.pinakaapps.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wisnusaputra.pinakaapps.R;

public class DetailImageActivity extends AppCompatActivity  {

    VideoView video;
    ImageView image;
    String uri,type;

    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        mediaController = new MediaController(DetailImageActivity.this);
        video = findViewById(R.id.video);
        image = findViewById(R.id.image);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            uri = bundle.getString("uri");
            type =  bundle.getString("type");
        }
        else{
            Toast.makeText(this, "user missing", Toast.LENGTH_SHORT).show();
        }

        if (type.equals("iv")){
            image.setVisibility(View.VISIBLE);
            video.setVisibility(View.INVISIBLE);
            Glide.with(DetailImageActivity.this).load(uri)
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(image);
        }
        else{
            mediaController.setAnchorView(video);
            image.setVisibility(View.INVISIBLE);
            video.setVisibility(View.VISIBLE);
            video.setMediaController(mediaController);
//            holder.post_video.setVideoPath(post.getPostimage());
            video.setVideoURI(Uri.parse(uri));
            video.start();
        }

//        image.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                mScaleGestureDetector.onTouchEvent(motionEvent);
//                return true;
//            }
//        });

    }
//    private float mScaleFactor = 1.0f;
//    private ScaleGestureDetector.SimpleOnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.5f));
//            image.setScaleX(mScaleFactor);
//            image.setScaleY(mScaleFactor);
//            return true;
//        }
//    };
//    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(image.getContext(), onScaleGestureListener);

}