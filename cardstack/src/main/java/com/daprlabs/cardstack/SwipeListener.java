package com.daprlabs.cardstack;

import android.animation.Animator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by aaron on 4/12/2015.
 */
public class SwipeListener implements View.OnTouchListener {

    private static final String TAG = com.daprlabs.cardstack.SwipeListener.class.getSimpleName();

    private float ROTATION_DEGREES = 15f;
    float OPACITY_END = 0.33f;
    private int DRAG_AXIS = 2;
    private float INDICATOR_SPACING = 50f;
    private float initialX;
    private float initialY;

    private int mActivePointerId;
    private float initialXPress;
    private float initialYPress;
    private ViewGroup parent;
    private float parentWidth;
    private float parentHeight;
    private int paddingLeft;
    private int paddingTop;

    private SwipeCardView card;
    SwipeCallback callback;
    private boolean deactivated;



    public SwipeListener(SwipeCardView card, final SwipeCallback callback, float initialX, float initialY, float rotation, float opacityEnd, int dragMode, float indicatorSpacing) {
        this.card = card;
        this.initialX = initialX;
        this.initialY = initialY;
        this.callback = callback;
        this.parent = (ViewGroup) card.getParent();
        this.parentWidth = parent.getWidth();
        this.parentHeight = parent.getHeight();

        this.ROTATION_DEGREES = rotation;
        this.OPACITY_END = opacityEnd;
        this.DRAG_AXIS = dragMode;

        this.INDICATOR_SPACING = indicatorSpacing;

        this.paddingLeft = ((ViewGroup) card.getParent()).getPaddingLeft();
        this.paddingTop = ((ViewGroup) card.getParent()).getPaddingTop();

    }

    public SwipeListener(SwipeCardView card, final SwipeCallback callback, float initialX, float initialY, float rotation, float opacityEnd, int screenWidth, int screenHeight, int dragMode, float indicatorSpacing) {
        this.card = card;
        this.initialX = initialX;
        this.initialY = initialY;
        this.callback = callback;
        this.parent = (ViewGroup) card.getParent();
        this.parentWidth = screenWidth;
        this.parentHeight = screenHeight;
        this.ROTATION_DEGREES = rotation;
        this.OPACITY_END = opacityEnd;
        this.DRAG_AXIS = dragMode;
        this.INDICATOR_SPACING = indicatorSpacing;
        this.paddingLeft = ((ViewGroup) card.getParent()).getPaddingLeft();
        this.paddingTop = ((ViewGroup) card.getParent()).getPaddingTop();

    }


    private boolean click = true;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (deactivated) return false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                click = true;
                //gesture has begun
                float x;
                float y;
                //cancel any current animations
                v.clearAnimation();

                mActivePointerId = event.getPointerId(0);

                x = event.getX();
                y = event.getY();

                if(event.findPointerIndex(mActivePointerId) == 0) {
                    callback.cardActionDown();
                }

                if(DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_X) {
                    initialXPress = x;
                }else if (DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_Y) {
                    initialYPress = y;
                }else if (DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_XY){
                    initialXPress = x;
                    initialYPress = y;
                }

                View leftView = card.getLeftOuterView();
                if(leftView != null) {
                    leftView.setVisibility(View.VISIBLE);
                    leftView.setAlpha(0);

                    float endOfViewX = card.getX() + card.getWidth();
                    leftView.setX( endOfViewX + (INDICATOR_SPACING));

//                    Log.i(TAG,"leftView.getWidth():"+leftView.getWidth());

                }

                View rightView = card.getRightOuterView();
                if(rightView != null) {
                    rightView.setVisibility(View.VISIBLE);
                    rightView.setAlpha(0);
                }

                View topView = card.getTopOuterView();
                if(topView != null) {
                    topView.setVisibility(View.VISIBLE);
                    topView.setAlpha(0);
                }

                View bottomView = card.getBottomOuterView();
                if(bottomView != null) {
                    bottomView.setVisibility(View.VISIBLE);
                    bottomView.setAlpha(0);
                }

                break;

            case MotionEvent.ACTION_MOVE:
                //gesture is in progress

                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                //Log.i("pointer index: " , Integer.toString(pointerIndex));
                if(pointerIndex < 0 || pointerIndex > 0 ){
                    break;
                }

                if(DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_X) {
                    final float xMove = event.getX(pointerIndex);
                    final float dx = xMove - initialXPress;

//                    Log.i("dx: ", Float.toString(dx));
                    if((int)initialXPress == 0){
                        //makes sure the pointer is valid
                        break;
                    }

                    float posX = card.getX() + dx;

                    //in this circumstance consider the motion a click
                    if (Math.abs(dx) > 2.5f) click = false;

                    card.setX(posX);

                    View movignLeftView = card.getLeftOuterView();
                    if(movignLeftView != null) {
                        float endOfViewX = card.getX() + card.getWidth();
                        movignLeftView.setX( endOfViewX + (INDICATOR_SPACING));
                    }

                    //card.setRotation
                    float distobjectX = posX - initialX;
                    float rotation = ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                    card.setRotation(rotation);

                    //set alpha of left and right image
                    float alpha = (((posX - paddingLeft) / (parentWidth * OPACITY_END)));



                    //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                    callback.onDragProgress(alpha, 0.0f);

                    for(View view : card.getLeftInnerViews()){
                        view.setAlpha(-alpha);
                    }

                    for(View view : card.getRightInnerViews()){
                        view.setAlpha(alpha);
                    }

                    View leftOuterView = card.getLeftOuterView();
                    if(leftOuterView != null) {
                        leftOuterView.setAlpha(-alpha);
                    }

                    View rightOuterView = card.getRightOuterView();
                    if(rightOuterView != null) {
                        rightOuterView.setAlpha(alpha);
                    }

//                    View leftView = card.getLeftOuterView();
//                    View rightView = card.getRightOuterView();
//                    View topView = card.getTopOuterView();
//                    View bottomView = card.getBottomOuterView();


//                    if (rightView != null && leftView != null){
//                        rightView.setAlpha(alpha);
//                        leftView.setAlpha(-alpha);
//                    }

                }else if (DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_Y) {
                    final float yMove = event.getY(pointerIndex);
                    final float dy = yMove - initialYPress;

                    if((int) initialYPress == 0){
                        //makes sure the pointer is valid
                        break;
                    }

                    float posY = card.getY() + dy;

                    //in this circumstance consider the motion a click
                    if (Math.abs(dy) > 2.5f) click = false;

                    card.setY(posY);

                    //card.setRotation
                    float distobjectY = posY - initialY;
                    float rotation = ROTATION_DEGREES * 2.f * distobjectY / parentHeight;
                    card.setRotation(rotation);

                    float alpha = (((posY - paddingTop) / (parentHeight * OPACITY_END)));


                    //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                    //Log.i("alpha: ", Float.toString(alpha));
                    callback.onDragProgress(0.0f, alpha);

                    for(View view : card.getTopInnerViews()){
                        view.setAlpha(-alpha);
                    }

                    for(View view : card.getBottomInnerViews()){
                        view.setAlpha(alpha);
                    }

                    View topOuterView = card.getTopOuterView();
                    if(topOuterView != null) {
                        topOuterView.setAlpha(-alpha);
                    }

                    View bottomOuterView = card.getBottomOuterView();
                    if(bottomOuterView != null) {
                        bottomOuterView.setAlpha(alpha);
                    }

//                    if (topView != null && bottomView != null){
//                        topView.setAlpha(-alpha);
//                        bottomView.setAlpha(alpha);
//                    }

                }else if (DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_XY){
                    //calculate distance moved

                    final float xMove = event.getX(pointerIndex);
                    final float dx = xMove - initialXPress;

                    final float yMove = event.getY(pointerIndex);
                    final float dy = yMove - initialYPress;

                    //throw away the move in this case as it seems to be wrong
                    //TODO: figure out why this is the case
                    if((int)initialXPress == 0 && (int) initialYPress == 0){
                        //makes sure the pointer is valid
                        break;
                    }
                    //calc rotation here
                    float posX = card.getX() + dx;
                    float posY = card.getY() + dy;

                    //in this circumstance consider the motion a click
                    if (Math.abs(dx + dy) > 5) click = false;

                    card.setX(posX);
                    card.setY(posY);

                    //card.setRotation
                    float distobjectX = posX - initialX;
                    float rotation = ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                    card.setRotation(rotation);

                    float alphaX = (((posX - paddingLeft) / (parentWidth * OPACITY_END)));
                    float alphaY = (((posY - paddingTop) / (parentHeight * OPACITY_END)));

                    callback.onDragProgress(alphaX, alphaY);

                    for(View view : card.getLeftInnerViews()){
                        view.setAlpha(-alphaX);
                    }

                    for(View view : card.getRightInnerViews()){
                        view.setAlpha(alphaX);
                    }

                    for(View view : card.getTopInnerViews()){
                        view.setAlpha(-alphaY);
                    }

                    for(View view : card.getBottomInnerViews()){
                        view.setAlpha(alphaY);
                    }

                    View leftOuterView = card.getLeftOuterView();
                    if(leftOuterView != null) {
                        leftOuterView.setAlpha(-alphaX);
                    }

                    View rightOuterView = card.getRightOuterView();
                    if(rightOuterView != null) {
                        rightOuterView.setAlpha(alphaX);
                    }

                    View topOuterView = card.getTopOuterView();
                    if(topOuterView != null) {
                        topOuterView.setAlpha(-alphaY);
                    }

                    View bottomOuterView = card.getBottomOuterView();
                    if(bottomOuterView != null) {
                        bottomOuterView.setAlpha(alphaY);
                    }
//                    if (rightView != null && leftView != null){
//                        //set alpha of left and right image
//
//                        //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
//                        //Log.i("alpha: ", Float.toString(alpha));
//                        leftView.setAlpha(-alphaX);
//                        rightView.setAlpha(alphaX);
//
//                    }
//
//                    if (topView != null && bottomView != null){
//                        //set alpha of left and right image
//
//                        //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
//                        //Log.i("alpha: ", Float.toString(alpha));
//                        bottomView.setAlpha(alphaY);
//                        topView.setAlpha(-alphaY);
//                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                //gesture has finished
                //check to see if card has moved beyond the left or right bounds or reset
                //card position
                checkCardForEvent();

                if(event.findPointerIndex(mActivePointerId) == 0) {
                    callback.cardActionUp();
                }
                //check if this is a click event and then perform a click
                //this is a workaround, android doesn't play well with multiple listeners

                if (click) v.performClick();
                //if(click) return false;

                break;

            default:
                return false;
        }
        return true;
    }

    public void checkCardForEvent() {

        if(DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_X) {

            if (cardBeyondLeftBorder()) {
                animateOffScreenLeft(160)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                callback.cardSwipedLeft();
                this.deactivated = true;
            } else if (cardBeyondRightBorder()) {
                animateOffScreenRight(160)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                callback.cardSwipedRight();
                this.deactivated = true;
            } else {
                callback.cardResetPosition();
                resetCardPosition();
            }

        }else if (DRAG_AXIS == com.daprlabs.cardstack.SwipeDeck.DRAG_AXIS_Y) {

            if (cardBeyondTopBorder()) {
                animateOffScreenTop(160)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                callback.cardSwipedUp();
                this.deactivated = true;
            } else if (cardBeyondBottomBorder()) {
                animateOffScreenBottom(160)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                callback.cardSwipedDown();
                this.deactivated = true;
            } else {
                callback.cardResetPosition();
                resetCardPosition();
            }

        }else if (DRAG_AXIS == SwipeDeck.DRAG_AXIS_XY){
            if (cardBeyondLeftBorder()) {
                animateOffScreenLeft(160)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                callback.cardSwipedLeft();
                this.deactivated = true;
            } else if (cardBeyondRightBorder()) {
                animateOffScreenRight(160)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                callback.cardSwipedRight();
                this.deactivated = true;
            } else if (cardBeyondTopBorder()) {
                animateOffScreenTop(160)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                callback.cardSwipedUp();
                this.deactivated = true;
            } else if (cardBeyondBottomBorder()) {
                animateOffScreenBottom(160).setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                callback.cardSwipedDown();
                this.deactivated = true;
            } else {
                callback.cardResetPosition();
                resetCardPosition();
            }
        }




//        if (cardBeyondLeftBorder()) {
//            animateOffScreenLeft(160)
//                    .setListener(new Animator.AnimatorListener() {
//
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//
//                            callback.cardOffScreen();
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//                        }
//                    });
//            callback.cardSwipedLeft();
//            this.deactivated = true;
//        } else if (cardBeyondRightBorder()) {
//            animateOffScreenRight(160)
//                    .setListener(new Animator.AnimatorListener() {
//
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            callback.cardOffScreen();
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//
//                        }
//                    });
//            callback.cardSwipedRight();
//            this.deactivated = true;
//        } else {
//            resetCardPosition();
//        }
    }

    private boolean cardBeyondLeftBorder() {
//        Log.i(TAG, "cardBeyondLeftBorder");
        //check if cards middle is beyond the left quarter of the screen
        return (card.getX() + (card.getWidth() / 2) < (parentWidth / 5.f));
    }

    private boolean cardBeyondRightBorder() {
//        Log.i(TAG, "cardBeyondRightBorder");
        //check if card middle is beyond the right quarter of the screen
        return (card.getY() + (card.getHeight() / 2) > ((parentWidth / 5.f) * 4));
    }

    private boolean cardBeyondTopBorder() {
//        Log.i(TAG, "cardBeyondTopBorder");
        //check if cards middle is beyond the left quarter of the screen
        return (card.getY() + (card.getHeight() / 2) < (parentHeight / 5.f));
    }

    private boolean cardBeyondBottomBorder() {
//        Log.i(TAG, "cardBeyondBottomBorder");
        //check if card middle is beyond the right quarter of the screen
        return (card.getY() + (card.getHeight() / 2) > ((parentHeight / 5.f) * 4));
    }

    private ViewPropertyAnimator resetCardPosition() {

//        Log.i(TAG, "resetCardPosition");

//        if(rightView!=null)rightView.setAlpha(0);
//        if(leftView!=null)leftView.setAlpha(0);
//        if(topView!=null)topView.setAlpha(0);
//        if(bottomView!=null)bottomView.setAlpha(0);

        for(View view : card.getLeftInnerViews()){
            if(view != null){
                view.setAlpha(0);
            }
        }

        for(View view : card.getRightInnerViews()){
            if(view != null){
                view.setAlpha(0);
            }
        }

        for(View view : card.getTopInnerViews()){
            if(view != null){
                view.setAlpha(0);
            }
        }

        for(View view : card.getBottomInnerViews()){
            if(view != null){
                view.setAlpha(0);
            }
        }

        View leftOuterView = card.getLeftOuterView();
        if(leftOuterView != null) {
            leftOuterView.setAlpha(0);
        }

        View rightOuterView = card.getRightOuterView();
        if(rightOuterView != null) {
            rightOuterView.setAlpha(0);
        }

        View topOuterView = card.getTopOuterView();
        if(topOuterView != null) {
            topOuterView.setAlpha(0);
        }

        View bottomOuterView = card.getBottomOuterView();
        if(bottomOuterView != null) {
            bottomOuterView.setAlpha(0);
        }

        return card.animate()
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .x(initialX)
                .y(initialY)
                .rotation(0);
    }

    public ViewPropertyAnimator animateOffScreenLeft(int duration) {

//        Log.i(TAG, "animateOffScreenLeft-duration:"+duration);


        return card.animate()
                .setDuration(duration)
                .x(-(parentWidth))
                .y(0)
                .rotation(-30);
    }


    public ViewPropertyAnimator animateOffScreenRight(int duration) {

//        Log.i(TAG, "animateOffScreenRight-duration:"+duration);

        return card.animate()
                .setDuration(duration)
                .x(parentWidth * 2)
                .y(0)
                .rotation(30);
    }

    public ViewPropertyAnimator animateOffScreenTop(int duration) {

//        Log.i(TAG, "animateOffScreenTop-parentHeight:"+parentWidth);
//        Log.i(TAG, "animateOffScreenTop-parentHeight:"+parentHeight);

        return card.animate()
                .setDuration(duration)
                .translationYBy(-(parentHeight))
                .rotation(-30);
    }


    public ViewPropertyAnimator animateOffScreenBottom(int duration) {
//        Log.i(TAG, "animateOffScreenBottom-parentHeight:"+parentWidth);
//        Log.i(TAG, "animateOffScreenBottom-parentHeight:"+parentHeight);

        return card.animate()
                .setDuration(duration)
                .translationYBy(parentHeight * 2)
                .rotation(30);
    }



    public interface SwipeCallback {
        void cardSwipedLeft();
        void cardSwipedRight();
        void cardSwipedUp();
        void cardSwipedDown();
        void cardOffScreen();
        void cardActionDown();
        void cardActionUp();
        void cardResetPosition();
        void onDragProgress(float xProgress, float yProgress);
    }
}
