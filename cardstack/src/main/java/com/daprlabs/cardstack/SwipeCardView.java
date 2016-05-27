package com.daprlabs.cardstack;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaron on 23/12/2015.
 */
public class SwipeCardView extends RelativeLayout {

    private SwipeActions mSwipeActions;
    public void setSwipeActions(SwipeActions actions) {
        mSwipeActions = actions;
    }
    public SwipeActions getSwipeActions() {
        return mSwipeActions;
    }
    //inner view
    private List<View> leftViewList = new ArrayList<View>();
    private List<View> rightViewList = new ArrayList<View>();
    private List<View> topViewList = new ArrayList<View>();
    private List<View> bottomViewList = new ArrayList<View>();

    public void addLeftView(View view) {
        this.leftViewList.add(view);
    }
    public void clearLeftView() {
        this.leftViewList.clear();
    }
    public List<View> getLeftInnerViews() {
        return this.leftViewList;
    }

    public void addRightView(View view) {
        this.rightViewList.add(view);
    }
    public void clearRightView() {
        this.rightViewList.clear();
    }
    public List<View> getRightInnerViews() {
        return this.rightViewList;
    }

    public void addTopView(View view) {
        this.topViewList.add(view);
    }
    public void clearTopView() {
        this.topViewList.clear();
    }
    public List<View> getTopInnerViews() {
        return this.topViewList;
    }

    public void addBottomView(View view) {
        this.bottomViewList.add(view);
    }
    public void clearBottomView() {
        this.bottomViewList.clear();
    }
    public List<View> getBottomInnerViews() {
        return this.bottomViewList;
    }

//    private int mLeftOuterViewId = 0;
//    public void setLeftOuterViewResId(int resId) {
//        mLeftOuterViewId = resId;
//    }
//    public int getLeftOuterViewResId() {
//        return mLeftOuterViewId;
//    }

    //outter view
    private View mTopOuterView;
    private View mBottomOuterView;
    private View mLeftOuterView;
    private View mRightOuterView;

    public void setTopOuterView(View view) {
        mTopOuterView = view;
        if(mTopOuterView != null) {
//            mTopOuterView.setVisibility(GONE);
        }
    }

    public void setBottomOuterView(View view) {
        mBottomOuterView = view;
        if(mBottomOuterView != null) {
//            mBottomOuterView.setVisibility(GONE);
        }
    }

    public void setLeftOuterView(View view) {
        mLeftOuterView = view;
        if(mLeftOuterView != null) {
//            mTopOuterView.setVisibility(GONE);
        }

    }

    public void setRightOuterView(View view) {
        mRightOuterView = view;
        if(mRightOuterView != null) {
//            mRightOuterView.setVisibility(GONE);
        }

    }

    public View getTopOuterView() {
        if(mTopOuterView != null) {
//            mTopOuterView.setVisibility(VISIBLE);
        }
        return mTopOuterView;

    }

    public View getBottomOuterView() {
        if(mBottomOuterView != null) {
//            mBottomOuterView.setVisibility(VISIBLE);
        }
        return mBottomOuterView;
    }

    public View getLeftOuterView() {
        if(mLeftOuterView != null) {
//            mLeftOuterView.setVisibility(VISIBLE);
        }
        return mLeftOuterView;
    }

    public View getRightOuterView() {
        if(mRightOuterView != null) {
//            mRightOuterView.setVisibility(VISIBLE);
        }
        return mRightOuterView;
    }

    public SwipeCardView(Context context) {
        super(context);
        setClipChildren(false);
    }

    public SwipeCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
    }

    public SwipeCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setClipChildren(false);
    }

//    //this is so that on versions of android pre lollipop it will render the cardstack above
//    //everything else within the layout
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        int childCount = getChildCount();
//        ViewGroup.LayoutParams params = getLayoutParams();
//
//        ArrayList<View> children = new ArrayList<>();
//        View swipeDeck = null;
//        for(int i=0; i< childCount; ++i){
//            View child = getChildAt(i);
//            if(child instanceof SwipeDeck){
//                swipeDeck = getChildAt(i);
//            }else{
//                children.add(child);
//            }
//        }
//        removeAllViews();
//        removeAllViewsInLayout();
//        for(View v : children){
//            addViewInLayout(v, -1, v.getLayoutParams(), true);
//        }
//        if(swipeDeck != null){
//            addViewInLayout(swipeDeck, -1, swipeDeck.getLayoutParams(), true);
//        }
//        invalidate();
//        requestLayout();
//    }

}
