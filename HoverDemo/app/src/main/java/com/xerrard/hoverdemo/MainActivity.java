package com.xerrard.hoverdemo;

import android.app.ActionBar;
import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    private Button mTestbtn;
    private boolean isHovered;
    private FrameLayout mContainerView;
    private HoverLayout mHoverLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mTestbtn = (Button) findViewById(R.id.test_btn);
        mTestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHovered) {
                    isHovered = false;
                    Toast.makeText(getBaseContext(), "Hovered", Toast.LENGTH_SHORT).show();
                    //mHoverLayout.moveToHalf();
                    mHoverLayout.move(0,mHoverLayout.getHeight()/3,true);
                } else {
                    isHovered = true;
                    Toast.makeText(getBaseContext(), "Recovery", Toast.LENGTH_SHORT).show();
                    mHoverLayout.goHome(true);
                }
            }
        });
        initHoverLayout();
        attachDecorToFlyingLayout();
    }


    private void initHoverLayout() {
        // setup ContainerView
        mContainerView = new FrameLayout(this);
        mContainerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // setup HoverLayout
        mHoverLayout = new HoverLayout(this);
        mHoverLayout.addView(mContainerView);
        mHoverLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


    }

    private void attachDecorToFlyingLayout() {
        ViewGroup decor = (ViewGroup) getWindow().peekDecorView();
        Drawable bg= decor.getBackground();
        List<View> contents = new ArrayList<View>();
        for (int i = 0; i < decor.getChildCount(); ++i) {
            contents.add(decor.getChildAt(i));
        }
        decor.removeAllViews();

        FrameLayout backgroud = new FrameLayout(this);
        backgroud.setBackground(bg);
        mContainerView.addView(backgroud);
        for (View v : contents) {
            mContainerView.addView(v, v.getLayoutParams());
        }
        mHoverLayout.setBackground(WallpaperManager.getInstance(this).getDrawable());
        decor.addView(mHoverLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
}
