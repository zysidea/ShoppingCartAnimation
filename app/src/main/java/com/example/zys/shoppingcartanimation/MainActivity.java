package com.example.zys.shoppingcartanimation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private int[] mShoppingBagCoordinate = new int[2];

    private GoodsDetailView mGoodsDetailView;

    private RelativeLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout=(RelativeLayout)findViewById(R.id.activity_main);
        mGoodsDetailView = (GoodsDetailView) View.inflate(this, R.layout.goods_detail_view, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        mGoodsDetailView.setLayoutParams(lp);


        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                View shoppingBag =findViewById(R.id.image);
                if(shoppingBag!=null) {
                    shoppingBag.getLocationOnScreen(mShoppingBagCoordinate);
                    mShoppingBagCoordinate[0] += shoppingBag.getWidth() / 2;
                    mShoppingBagCoordinate[1] += shoppingBag.getHeight() / 2;
                    mGoodsDetailView.setShopBagCoordinate(mShoppingBagCoordinate);
                }
            }
        });


        Button button = (Button)mGoodsDetailView.findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoodsDetailView.getParent()!=null) {
                    mGoodsDetailView.removeWithAnimation(true);
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoodsDetailView.addWithAnimation(mCoordinatorLayout);
            }
        });
    }
}
