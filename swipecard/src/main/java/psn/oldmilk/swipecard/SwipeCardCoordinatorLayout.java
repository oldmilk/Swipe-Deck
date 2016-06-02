package psn.oldmilk.swipecard;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by aaron on 23/12/2015.
 */
public class SwipeCardCoordinatorLayout extends CoordinatorLayout {

    private View mTopOuterView;
    private View mBottomOuterView;
    private View mLeftOuterView;
    private View mRightOuterView;

    public void setTopOuterView(View view) {
        mTopOuterView = view;
    }

    public void setBottomOuterView(View view) {
        mBottomOuterView = view;
    }

    public void setLeftOuterView(View view) {
        mLeftOuterView = view;
    }

    public void setRightOuterView(View view) {
        mRightOuterView = view;
    }

    public View getTopOuterView() {
        return mTopOuterView;
    }

    public View getBottomOuterView() {
        return mBottomOuterView;
    }

    public View getLeftOuterView() {
        return mLeftOuterView;
    }

    public View getRightOuterView() {
        return mRightOuterView;
    }

    public SwipeCardCoordinatorLayout(Context context) {
        super(context);
        setClipChildren(false);
    }

    public SwipeCardCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
    }

    public SwipeCardCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
