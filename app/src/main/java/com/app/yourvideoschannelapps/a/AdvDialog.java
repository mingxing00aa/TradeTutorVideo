package com.app.yourvideoschannelapps.a;


import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.app.yourvideoschannelapps.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class AdvDialog extends Dialog {
    public AdvDialog(@NonNull Context context) {
        super(context,R.style.normal_dialog);
    }
private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_adv);

        WindowManager wm = (WindowManager)
                getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width =width-300; //设置宽度

       getWindow().setAttributes(lp);

imageView=findViewById(R.id.image);
       findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dismiss();
           }
       });
    }

    public void setImage(String imageUrl,String jumpUrl,int id,int uploadType){
        if(uploadType==0) {
            imageView.setVisibility(VISIBLE);
            RequestOptions options = new RequestOptions().transform(new RoundedCorners(20));
            Glide.with(getContext()).load(imageUrl).apply(options).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.addClick(id,0);
                    FireEvent.getInstance().AdEvent(Utils.click_main_dialog);
                    Uri uri = Uri.parse(jumpUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);
                    dismiss();
                }
            });
        }else {
            VideoView videoView=findViewById(R.id.videoView);
            videoView.setVisibility(VISIBLE);
            Uri uri=Uri.parse(imageUrl);
            videoView.setVideoURI(uri);

//            //创建MediaController对象
//            MediaController mediaController = new MediaController(getContext());
//            //VideoView与MediaController建立关联
//            videoView.setMediaController(mediaController);
            videoView.start();
        }

    }
}
