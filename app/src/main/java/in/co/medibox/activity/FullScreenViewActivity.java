package in.co.medibox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import in.co.medibox.R;
import in.co.medibox.adapter.FullScreenImageAdapter;

public class FullScreenViewActivity extends Activity {
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<String> paths;
    private int Img_Position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        paths = new ArrayList<String>();

        Bundle d = getIntent().getExtras();
        if (d != null) {
            paths = d.getStringArrayList("Pic_path");
            Img_Position = d.getInt("Position");
        }

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
                paths);
        viewPager.setAdapter(adapter);
        // displaying selected image first
        viewPager.setCurrentItem(Img_Position);
    }
}
