package psn.oldmilk.swipecard;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

//import android.util.Log;
//import android.util.LogPrinter;

/**
 * Created by aaron on 4/12/2015.
 */
public class SwipeDeck extends FrameLayout {

    private static final String TAG = SwipeDeck.class.getSimpleName();
    private static int NUMBER_OF_CARDS;
    private float ROTATION_DEGREES;
    private float CARD_SPACING;
    private float INDICATOR_SPACING;
    private boolean RENDER_ABOVE;
    private boolean RENDER_BELOW;
    private float OPACITY_END;
    private int CARD_GRAVITY;
    private int DRAG_AXIS;
    private int OVERLAY_COLOR;

    private SwipeDeckRootLayout mRootView;
    private SwipeDeckLayout mSwipeDeckLayout;

    public static final int DRAG_AXIS_X = 0;
    public static final int DRAG_AXIS_Y = 1;
    public static final int DRAG_AXIS_XY = 2;


    private int paddingLeft;
    private boolean hardwareAccelerationEnabled = true;

    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    private SwipeEventCallback eventCallback;
    private CardPositionCallback cardPosCallback;

    /**
     * The adapter with all the data
     */
    private SwipeDeckAdapter mAdapter;
    DataSetObserver observer;
    int nextAdapterCard = 0;
    private boolean restoreInstanceState = false;

    private SwipeListener swipeListener;

    private boolean cardInteraction = false;
    private boolean isNeedRefreshCards = false;
//    private boolean isNeedRebuildCards = false;

//    private View mTopOuterView;
//    private View mBottomOuterView;
//    private View mRightOuterView;
//    private View mLeftOuterView;


    public SwipeDeck(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SwipeDeck,
                0, 0);
        try {
            NUMBER_OF_CARDS = a.getInt(R.styleable.SwipeDeck_max_visible, 3);
            ROTATION_DEGREES = a.getFloat(R.styleable.SwipeDeck_rotation_degrees, 15f);
            CARD_SPACING = a.getDimension(R.styleable.SwipeDeck_card_spacing, 15f);
            INDICATOR_SPACING  = a.getDimension(R.styleable.SwipeDeck_indicator_spacing, 50f);
            RENDER_ABOVE = a.getBoolean(R.styleable.SwipeDeck_render_above, true);
            RENDER_BELOW = a.getBoolean(R.styleable.SwipeDeck_render_below, false);
            CARD_GRAVITY = a.getInt(R.styleable.SwipeDeck_card_gravity, 0);
            DRAG_AXIS = a.getInt(R.styleable.SwipeDeck_drag_axis, 2);
            OPACITY_END = a.getFloat(R.styleable.SwipeDeck_opacity_end, 0.33f);
            OVERLAY_COLOR = a.getColor(R.styleable.SwipeDeck_overlay_color, 0);
        } finally {
            a.recycle();
        }

        paddingBottom = getPaddingBottom();
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();

        //set clipping of view parent to false so cards render outside their view boundary
        //make sure not to clip to padding
        setClipToPadding(false);
        setClipChildren(false);

        this.setWillNotDraw(false);

        //render the cards and card deck above or below everything
        if (RENDER_ABOVE) {
            ViewCompat.setTranslationZ(this, Float.MAX_VALUE);
        }
        if (RENDER_BELOW) {
            ViewCompat.setTranslationZ(this, Float.MIN_VALUE);
        }


    }

    /**
     * Set Hardware Acceleration Enabled.
     *
     * @param accel
     */
    public void setHardwareAccelerationEnabled(Boolean accel) {
        this.hardwareAccelerationEnabled = accel;
    }

    public void setAdapter(SwipeDeckAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(observer);
        }
        mAdapter = adapter;
        // if we're not restoring previous instance state
        if(!restoreInstanceState)nextAdapterCard = 0;

        observer = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                Log.i(TAG,"DataSetObserver - onChanged");

                if(!cardInteraction) {
                    refreshView();
                }else{
                    isNeedRefreshCards = true;
                }
            }

            @Override
            public void onInvalidated() {
//                Log.i(TAG, "onInvalidated");

                //reset state, remove views and request layout
                nextAdapterCard = 0;
                rebuildView();

//                if(!cardInteraction) {
//                    //reset state, remove views and request layout
//                    nextAdapterCard = 0;
//                    rebuildView();
//                }else{
//                    isNeedRebuildCards = true;
//                }


            }
        };

        adapter.registerDataSetObserver(observer);


//        removeAllViewsInLayout();
//        requestLayout();
//

        nextAdapterCard = 0;
        rebuildView();

    }

    private void refreshView() {

        Log.i(TAG,"refreshView");

        int startIndex = nextAdapterCard - getChildCount();
        nextAdapterCard = startIndex;

        rebuildView();
    }

    private void disableTouch(int position) {
        final SwipeCardView child = (SwipeCardView) getChildAt(position);

        if (child != null) {
            child.setOnTouchListener(null);

            if(swipeListener != null) {
                swipeListener.resetCardPosition();

                swipeListener = null;

            }

        }
    }

    private void rebuildView() {



        for (int i = 0; i < getChildCount(); ++i) {
            disableTouch(i);
        }

        removeAllLeftOuterView();
        removeAllRightOuterView();
        removeAllTopOuterView();
        removeAllBottomOuterView();

        removeAllViews();
        int childCount = getChildCount();
        for (int i = childCount; i < (NUMBER_OF_CARDS+1); ++i) {
            addNextCard();
        }
        for (int i = 0; i < getChildCount(); ++i) {
            transitItem(false, i, 0.0f, 0.0f);
        }
    }

    private List<View> mTopOuterViewList = new ArrayList<View>();
    private void removeAllTopOuterView() {
        for(View view :mTopOuterViewList) {

            if(view != null) {
                mRootView.removeView(view);
            }
        }
        mTopOuterViewList.clear();
    }

    private List<View> mBottomOuterViewList = new ArrayList<View>();
    private void removeAllBottomOuterView() {
        for(View view :mBottomOuterViewList) {

            if(view != null) {
                mRootView.removeView(view);
            }
        }
        mBottomOuterViewList.clear();
    }

    private List<View> mLeftOuterViewList = new ArrayList<View>();
    private void removeAllLeftOuterView() {
        for(View view :mLeftOuterViewList) {

            if(view != null) {
                mRootView.removeView(view);
            }
        }
        mLeftOuterViewList.clear();
    }

    private List<View> mRightOuterViewList = new ArrayList<View>();
    private void removeAllRightOuterView() {
        for(View view :mRightOuterViewList) {

            if(view != null) {
                mRootView.removeView(view);
            }
        }
        mRightOuterViewList.clear();
    }

    public void setSelection(int position){
        if(position < mAdapter.getCount()){
            this.nextAdapterCard = position;

            removeAllLeftOuterView();
            removeAllRightOuterView();
            removeAllTopOuterView();
            removeAllBottomOuterView();

            removeAllViews();
            requestLayout();
        }
    }

    public View getSelectedView() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

//        Log.i(TAG, "onLayout");
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null || mAdapter.getCount() == 0) {
            nextAdapterCard = 0;


            removeAllLeftOuterView();
            removeAllRightOuterView();
            removeAllTopOuterView();
            removeAllBottomOuterView();

            removeAllViewsInLayout();
            return;
        }

        //pull in views from the adapter at the position the top of the deck is set to
        //stop when you get to for cards or the end of the adapter
        int childCount = getChildCount();
        for (int i = childCount; i < (NUMBER_OF_CARDS+1); ++i) {
            addNextCard();
        }
        for (int i = 0; i < getChildCount(); ++i) {
            transitItem(false, i, 0.0f, 0.0f);
        }
        //position the new children we just added and set up the top card with a listener etc
    }

    private void removeTopCard() {

//        Log.i(TAG, "removeTopCard");

        int childOffset = getChildCount() - (NUMBER_OF_CARDS+1) + 1;
        int index = getChildCount() - childOffset;
        if(getChildCount() <=  index) {
            index = getChildCount()-1;
        }
        final SwipeCardView child = (SwipeCardView) getChildAt(index);

        if (child != null) {
            child.setOnTouchListener(null);
            swipeListener = null;

            //this will also check to see if cards are depleted
            removeViewWaitForAnimation(child);
        }
    }

    private void removeViewWaitForAnimation(View child) {
        new RemoveViewOnAnimCompleted().execute((SwipeCardView) child);
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);

//        Log.i(TAG, "removeView");
    }


    private void addNextCard() {
//        Log.i(TAG, "addNextCard");
//        Log.i(TAG, "addNextCard - nextAdapterCard:"+nextAdapterCard+", mAdapter.getCount():"+mAdapter.getCount());

        if (nextAdapterCard < mAdapter.getCount()) {

            // TODO: Make view recycling work
            // TODO: Instead of removing the view from here and adding it again when it's swiped
            // ... don't remove and add to this instance: don't call removeView & addView in sequence.


            SwipeCardView newBottomChild = mAdapter.getView(nextAdapterCard, null/*lastRemovedView*/, this);
            newBottomChild.setSwipeActions(mAdapter.getActions(nextAdapterCard));

            if(mSwipeDeckLayout == null) {
                mSwipeDeckLayout = ((SwipeDeckLayout)getParent());
            }

            if(mRootView == null) {
                mRootView = ((SwipeDeckRootLayout)mSwipeDeckLayout.getParent());
            }

            ImageView overlayView = new ImageView(getContext());
            overlayView.setBackgroundColor(OVERLAY_COLOR);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            overlayView.setAlpha(0.0f);
            newBottomChild.addView(overlayView, lp);
            newBottomChild.setOverlayView(overlayView);

            View leftView = mAdapter.getOutLeftView(nextAdapterCard);
            if(leftView != null) {
                RelativeLayout.LayoutParams rlpLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlpLeft.addRule(RelativeLayout.CENTER_VERTICAL);
                rlpLeft.addRule(RelativeLayout.ALIGN_LEFT, mSwipeDeckLayout.getId());
                mRootView.addView(leftView, rlpLeft);
                leftView.setVisibility(GONE);
                newBottomChild.setLeftOuterView(leftView);

                mLeftOuterViewList.add(leftView);
            }

            View rightView = mAdapter.getOutRightView(nextAdapterCard);
            if(rightView != null) {
                RelativeLayout.LayoutParams rlpRight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlpRight.addRule(RelativeLayout.CENTER_VERTICAL);
                rlpRight.addRule(RelativeLayout.ALIGN_RIGHT, mSwipeDeckLayout.getId());
                mRootView.addView(rightView, rlpRight);
                rightView.setVisibility(GONE);
                newBottomChild.setRightOuterView(rightView);

                mRightOuterViewList.add(rightView);
            }

            View topView = mAdapter.getOutTopView(nextAdapterCard);
            if(topView != null) {
                RelativeLayout.LayoutParams rlpTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlpTop.addRule(RelativeLayout.CENTER_VERTICAL);
                rlpTop.addRule(RelativeLayout.ALIGN_TOP, mSwipeDeckLayout.getId());
                mRootView.addView(topView, rlpTop);
                topView.setVisibility(GONE);
                newBottomChild.setTopOuterView(topView);

                mTopOuterViewList.add(topView);
            }

            View bottomView = mAdapter.getOutBottomView(nextAdapterCard);
            if(bottomView != null) {
                RelativeLayout.LayoutParams rlpBottom = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlpBottom.addRule(RelativeLayout.CENTER_VERTICAL);
                rlpBottom.addRule(RelativeLayout.ALIGN_BOTTOM, mSwipeDeckLayout.getId());
                mRootView.addView(bottomView, rlpBottom);
                bottomView.setVisibility(GONE);
                newBottomChild.setBottomOuterView(bottomView);

                mBottomOuterViewList.add(bottomView);
            }

            if (hardwareAccelerationEnabled) {
                //set backed by an off-screen buffer
                newBottomChild.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }



            //set the initial Y value so card appears from under the deck
            //newBottomChild.setY(paddingTop);
            addAndMeasureChild(newBottomChild);
            nextAdapterCard++;
        }
        setupTopCard();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setZTranslations() {
        //this is only needed to add shadows to cardviews on > lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int count = getChildCount();
            for (int i = 0; i < count; ++i) {
                getChildAt(i).setTranslationZ(i * 10);
            }
        }
    }

    /**
     * Adds a view as a child view and takes care of measuring it
     *
     * @param child The view to add
     */
    private void addAndMeasureChild(SwipeCardView child) {

//        Log.i(TAG, "addAndMeasureChild");

        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        //ensure new card is under the deck at the beginning
        child.setY(paddingTop);

        //every time we add and measure a child refresh the children on screen and order them
        ArrayList<SwipeCardView> children = new ArrayList<SwipeCardView>();
        children.add(child);
        for (int i = 0; i < getChildCount(); ++i) {
            children.add((SwipeCardView)getChildAt(i));
        }

        removeAllViews();

        for (SwipeCardView c : children) {
            addViewInLayout(c, -1, params, true);
            int itemWidth = getWidth() - (paddingLeft + paddingRight);
            int itemHeight = getHeight() - (paddingTop + paddingBottom);
            c.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.EXACTLY | itemHeight); //MeasureSpec.UNSPECIFIED

            //ensure that if there's a left and right image set their alpha to 0 initially
            //alpha animation is handled in the swipe listener
            for(View view : c.getTopInnerViews()){
                if(view != null){
                    view.setAlpha(0);
                }
            }

            for(View view : c.getBottomInnerViews()){
                if(view != null){
                    view.setAlpha(0);
                }
            }

            for(View view : c.getLeftInnerViews()){
                if(view != null){
                    view.setAlpha(0);
                }
            }

            for(View view : c.getRightInnerViews()){
                if(view != null){
                    view.setAlpha(0);
                }
            }

            View leftOuterView = c.getLeftOuterView();
            if(leftOuterView != null) {
                leftOuterView.setAlpha(0);
            }

            View rightOuterView = c.getRightOuterView();
            if(rightOuterView != null) {
                rightOuterView.setAlpha(0);
            }

            View topOuterView = c.getTopOuterView();
            if(topOuterView != null) {
                topOuterView.setAlpha(0);
            }

            View bottomOuterView = c.getBottomOuterView();
            if(bottomOuterView != null) {
                bottomOuterView.setAlpha(0);
            }
        }
        setZTranslations();


    }

    /**
     * Positions the children at the "correct" positions
     */

    private void transitItem(boolean isDragging, int index, float transitValue, float progress) {

        SwipeCardView child = (SwipeCardView) getChildAt(index);

        if(isDragging) {
            //skip top
            if(index == (getChildCount()-1)) {

                ImageView overlayImage = child.getOverlayView();
                overlayImage.setAlpha(0.0f);
                return;
            }

            //second top
            if(index == (getChildCount()-2)) {

                ImageView overlayImage = child.getOverlayView();
                overlayImage.setAlpha(progress - 1.0f);
            }else{
                ImageView overlayImage = child.getOverlayView();
                overlayImage.setAlpha(0.0f);
            }

        }else{

            ImageView overlayImage = child.getOverlayView();
            overlayImage.setAlpha(0.0f);
        }

        float multiply = (getChildCount()-1-index) * 0.05f;
        float fromScale = 1.00f - multiply;
        float toScale = (index == (getChildCount()-1))? fromScale : fromScale + 0.05f;

        float mappedScale = (float) Utils.mapValueFromRangeToRange(Math.abs(transitValue) , 0.0f, 1.0f, fromScale, toScale);
        child.setScaleX(mappedScale);
        child.setScaleY(mappedScale);

        int childCount = getChildCount();
        float offset = (int) (((childCount - 1) * CARD_SPACING) - (index * CARD_SPACING));
        float fromOffset = offset ;
        float toOffset = (index == (getChildCount()-1))? fromOffset : fromOffset - CARD_SPACING;
        float mappedValueY = (float) Utils.mapValueFromRangeToRange(Math.abs(transitValue) , 0.0f, 1.0f, fromOffset, toOffset);

        child.setY(paddingTop + mappedValueY);

        //hide the most bottom one but appear slowly when dragging
        if(index == 0 && (getChildCount() > NUMBER_OF_CARDS)) {
            child.setAlpha(transitValue);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        Log.i(TAG, "onMeasure");

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = widthSize;
        } else {
            //Be whatever you want
            width = widthSize;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = heightSize;
        } else {
            //Be whatever you want
            height = heightSize;
        }
        setMeasuredDimension(width, height);
    }


    private void setupTopCard() {

        //TODO: maybe find a better solution this is kind of hacky
        //if there's an extra card on screen that means the top card is still being animated
        //in that case setup the next card along
//        Log.i(TAG,"setupTopCard-getChildCount():"+getChildCount());
//        Log.i(TAG,"setupTopCard-(NUMBER_OF_CARDS+1):"+(NUMBER_OF_CARDS+1));

        int childOffset = getChildCount() - (NUMBER_OF_CARDS+1) + 1;
        int index = getChildCount() - childOffset;
        if(getChildCount() <=  index) {
            index = getChildCount()-1;
        }
        final SwipeCardView child = (SwipeCardView) getChildAt(index);

//        Log.i(TAG,"setupTopCard-childOffset:"+childOffset+", index:"+(index));

        //this calculation is to get the correct position in the adapter of the current top card
        //the card position on setup top card is currently always the bottom card in the view
        //at any given time.

        if (child != null) {
            //make sure we have a card

            View mLeftOuterView = child.getLeftOuterView();
            if(mLeftOuterView != null) {
                mLeftOuterView.setVisibility(VISIBLE);
                mLeftOuterView.setAlpha(0);
            }

            View mRightOuterView = child.getRightOuterView();
            if(mRightOuterView != null) {
                mRightOuterView.setVisibility(VISIBLE);
                mRightOuterView.setAlpha(0);
            }

            View mTopOuterView = child.getTopOuterView();
            if(mTopOuterView != null) {
                mTopOuterView.setVisibility(VISIBLE);
                mTopOuterView.setAlpha(0);
            }

            View mBottomOuterView = child.getBottomOuterView();
            if(mBottomOuterView != null) {
                mBottomOuterView.setVisibility(VISIBLE);
                mBottomOuterView.setAlpha(0);
            }

            int initialX = paddingLeft;
            int initialY = paddingTop;

            swipeListener = new SwipeListener(child, new SwipeListener.SwipeCallback() {
                @Override
                public void cardSwipedLeft() {
                    int positionInAdapter = nextAdapterCard - getChildCount();
                    if (eventCallback != null) eventCallback.cardSwipedLeft(positionInAdapter);

                    removeTopCard();
                    child.getSwipeActions().onSwipeLeft();
                }

                @Override
                public void cardSwipedRight() {
                    int positionInAdapter = nextAdapterCard - getChildCount();
                    if (eventCallback != null) eventCallback.cardSwipedRight(positionInAdapter);
                    removeTopCard();
                    child.getSwipeActions().onSwipeRight();
                }

                @Override
                public void cardSwipedUp() {
                    int positionInAdapter = nextAdapterCard - getChildCount();
                    if (eventCallback != null) eventCallback.cardSwipedUp(positionInAdapter);

                    removeTopCard();
                    child.getSwipeActions().onSwipeUp();
                }

                @Override
                public void cardSwipedDown() {
                    int positionInAdapter = nextAdapterCard - getChildCount();
                    if (eventCallback != null) eventCallback.cardSwipedDown(positionInAdapter);

                    removeTopCard();
                    child.getSwipeActions().onSwipeDown();
                }

                @Override
                public void cardOffScreen() {

                }

                @Override
                public void cardActionDown() {
                    if(eventCallback!=null) eventCallback.cardActionDown();
                    cardInteraction = true;
                }

                @Override
                public void cardActionUp() {

                    if(eventCallback!=null) eventCallback.cardActionUp();
                    cardInteraction = false;

                    if(isNeedRefreshCards) {
                        isNeedRefreshCards = false;

                        refreshView();
                    }
//
//                    if(isNeedRebuildCards) {
//                        isNeedRebuildCards = false;
//
//                        nextAdapterCard = 0;
//                        rebuildView();
//                    }
                }

                @Override
                public void cardResetPosition() {
                    for (int i = 0; i < getChildCount(); ++i) {
                        transitItem(false, i, 0.0f, 0.0f);
                    }
                }

                @Override
                public void onDragProgress(float xProgress, float yProgress) {

                    float translateX = 0.0f;
                    if(xProgress >= 0) {
                        translateX = (float) Utils.clamp(xProgress, 0.0f, 1.0f);
                    }else{
                        translateX = (float) Utils.clamp(xProgress, -1.0f, -0.0f);
                    }

                    float translateY = 0.0f;
                    if(yProgress >= 0) {
                        translateY = (float) Utils.clamp(yProgress, 0.0f, 1.0f);
                    }else{
                        translateY = (float) Utils.clamp(yProgress, -1.0f, -0.0f);
                    }

                    if(DRAG_AXIS == SwipeDeck.DRAG_AXIS_X) {

                        for (int i = 0; i < getChildCount(); ++i) {
                            transitItem(true, i, Math.abs(translateX), Math.abs(xProgress));
                        }

                    }else if (DRAG_AXIS == SwipeDeck.DRAG_AXIS_Y) {

                        for (int i = 0; i < getChildCount(); ++i) {
                            transitItem(true, i, Math.abs(translateY), Math.abs(yProgress));
                        }

                    }else if (DRAG_AXIS == SwipeDeck.DRAG_AXIS_XY){
                        for (int i = 0; i < getChildCount(); ++i) {
                            transitItem(true, i, Math.abs(translateX+translateY)*0.5f, Math.abs(xProgress+yProgress)*0.5f);
                        }
                    }
                    if(eventCallback!=null) eventCallback.onDragProgress(xProgress, yProgress);
                }

            }, initialX, initialY, ROTATION_DEGREES, OPACITY_END, DRAG_AXIS, INDICATOR_SPACING);

            child.setOnTouchListener(swipeListener);

//            RelativeLayout.LayoutParams rlpLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            rlpLeft.addRule(RelativeLayout.CENTER_IN_PARENT);
//            LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//
//
//            View leftView = inflater.inflate(child.getLeftOuterViewResId(), null);
//
////            ViewGroup.LayoutParams lp = child.getLeftOuterView().getLayoutParams();
//
//
//            mRootView.addView(leftView, rlpLeft);

//            if(getParent().getParent() instanceof SwipeDeckRootLayout) {
//                SwipeDeckRootLayout swipeDeckRootLayout = (SwipeDeckRootLayout)getParent().getParent();
//
//                RelativeLayout.LayoutParams rlpLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                rlpLeft.addRule(RelativeLayout.ALIGN_RIGHT, getId());
//                rlpLeft.addRule(RelativeLayout.CENTER_IN_PARENT);
//
////                swipeDeckRootLayout.addView(child.getLeftOuterView(), rlpLeft);
////                child.getLeftOuterView().getParent()
////                child.getLeftOuterView().setLayoutParams(rlpLeft);
//            }


//            }
//            View leftView = child.getLeftOuterView();
//            leftView.getParent();
//            if(leftView != null) {
//                leftView.setVisibility(GONE);
//                child.addView(leftView);
//            }
//
//            View rightView = child.getRightOuterView();
//            if(rightView != null) {
//                rightView.setVisibility(GONE);
//                child.addView(rightView);
//            }
//
//            View topView = child.getTopOuterView();
//            if(topView != null) {
//                topView.setVisibility(GONE);
//                child.addView(topView);
//            }
//
//            View bottomView = child.getBottomOuterView();
//            if(bottomView != null) {
//                bottomView.setVisibility(GONE);
//                child.addView(bottomView);
//            }

        }
    }

    public void setEventCallback(SwipeEventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }


    public void swipeTopCardLeft(int duration) {

        try {
            int childCount = getChildCount();
            if (childCount > 0 && getChildCount() < ((NUMBER_OF_CARDS+1) + 1)) {
                swipeListener.animateOffScreenLeft(duration);

                int positionInAdapter = nextAdapterCard - getChildCount();
                if (eventCallback != null) eventCallback.cardSwipedLeft(positionInAdapter);

                removeTopCard();
//            addNextCard();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void swipeTopCardRight(int duration) {

        try {
            int childCount = getChildCount();
            if (childCount > 0 && getChildCount() < ((NUMBER_OF_CARDS+1) + 1)) {
                swipeListener.animateOffScreenRight(duration);

                int positionInAdapter = nextAdapterCard - getChildCount();
                if (eventCallback != null) eventCallback.cardSwipedRight(positionInAdapter);

                removeTopCard();
//            addNextCard();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void swipeTopCardTop(int duration) {

        try {
            int childCount = getChildCount();
            if (childCount > 0 && getChildCount() < ((NUMBER_OF_CARDS+1) + 1)) {
                swipeListener.animateOffScreenTop(duration);

                int positionInAdapter = nextAdapterCard - getChildCount();
                if (eventCallback != null) eventCallback.cardSwipedUp(positionInAdapter);

                removeTopCard();
//            addNextCard();
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public void swipeTopCardBottom(int duration) {

        try {
            int childCount = getChildCount();
            if (childCount > 0 && getChildCount() < ((NUMBER_OF_CARDS+1) + 1)) {
                swipeListener.animateOffScreenBottom(duration);

                int positionInAdapter = nextAdapterCard - getChildCount();
                if (eventCallback != null) eventCallback.cardSwipedDown(positionInAdapter);

                removeTopCard();
//            addNextCard();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void setPositionCallback(CardPositionCallback callback) {
        cardPosCallback = callback;
    }

    public interface SwipeEventCallback {
        //returning the object position in the adapter
        void cardSwipedLeft(int position);

        void cardSwipedRight(int position);

        void cardSwipedUp(int position);

        void cardSwipedDown(int position);

        void cardsDepleted();

        void cardActionDown();

        void cardActionUp();

        void cardResetPosition();

        void onDragProgress(float xProgress, float yProgress);
    }

    public interface CardPositionCallback {
        void xPos(Float x);
        void yPos(Float y);
    }

    private int AnimationTime = 200;
    private class RemoveViewOnAnimCompleted extends AsyncTask<SwipeCardView, Void, SwipeCardView> {

        @Override
        protected SwipeCardView doInBackground(SwipeCardView... params) {

            android.os.SystemClock.sleep(AnimationTime);
            return params[0];
        }

        @Override
        protected void onPostExecute(SwipeCardView view) {
            super.onPostExecute(view);

            final View leftView = view.getLeftOuterView();
            if(leftView != null) {
                mRootView.removeView(leftView);
                mLeftOuterViewList.remove(leftView);
            }

            final View rightView = view.getRightOuterView();
            if(rightView != null) {
                mRootView.removeView(rightView);
                mRightOuterViewList.remove(rightView);
            }

             final View topView = view.getTopOuterView();
            if(topView != null) {
                mRootView.removeView(topView);
                mTopOuterViewList.remove(topView);
            }

            final View bottomView = view.getLeftOuterView();
            if(bottomView != null) {
                mRootView.removeView(bottomView);
                mBottomOuterViewList.remove(bottomView);
            }

            removeView(view);

//            final View leftView = view.getLeftOuterView();
//            if(leftView != null) {
//                leftView.animate().setDuration(200).scaleX(1.2f).scaleY(1.2f).alpha(0.0f).setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mRootView.removeView(leftView);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
//            }
//
//            final View rightView = view.getRightOuterView();
//            if(rightView != null) {
//                rightView.animate().setDuration(200).scaleX(1.2f).scaleY(1.2f).alpha(0.0f).setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mRootView.removeView(rightView);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
//            }
//
//            final View topView = view.getTopOuterView();
//            if(topView != null) {
//                topView.animate().setDuration(200).scaleX(1.2f).scaleY(1.2f).alpha(0.0f).setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mRootView.removeView(topView);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
//            }
//
//            final View bottomView = view.getLeftOuterView();
//            if(bottomView != null) {
//                bottomView.animate().setDuration(200).scaleX(1.2f).scaleY(1.2f).alpha(0.0f).setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mRootView.removeView(bottomView);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
//            }


            addNextCard();

            //if there are no more children left after top card removal let the callback know
            if (getChildCount() <= 0 && eventCallback != null) {
                eventCallback.cardsDepleted();
            }
        }
    }
}


