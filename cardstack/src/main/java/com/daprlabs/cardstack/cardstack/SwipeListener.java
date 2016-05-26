package com.daprlabs.cardstack.cardstack;

import android.animation.Animator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;

import com.daprlabs.cardstack.SwipeDeck;

/**
 * Created by aaron on 4/12/2015.
 */
public class SwipeListener implements View.OnTouchListener {

    private static final String TAG = com.daprlabs.cardstack.SwipeListener.class.getSimpleName();

    private float ROTATION_DEGREES = 15f;
    float OPACITY_END = 0.33f;
    private int DRAG_AXIS = 2;
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

    private View card;
    SwipeCallback callback;
    private boolean deactivated;
    private View rightView;
    private View leftView;
    private View topView;
    private View bottomView;

    public SwipeListener(View card, final SwipeCallback callback, float initialX, float initialY, float rotation, float opacityEnd, int dragMode) {
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
        this.paddingLeft = ((ViewGroup) card.getParent()).getPaddingLeft();
        this.paddingTop = ((ViewGroup) card.getParent()).getPaddingTop();

    }

    public SwipeListener(View card, final SwipeCallback callback, float initialX, float initialY, float rotation, float opacityEnd, int screenWidth,int screenHeight, int dragMode) {
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

                    //card.setRotation
                    float distobjectX = posX - initialX;
                    float rotation = ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                    card.setRotation(rotation);

                    //set alpha of left and right image
                    float alpha = (((posX - paddingLeft) / (parentWidth * OPACITY_END)));

                    if(alpha >= 0) {
                        alpha = (float)Utils.clamp(alpha, 0.0f, 1.0f);
                    }else{
                        alpha = (float)Utils.clamp(alpha, -1.0f, -0.0f);
                    }

                    //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                    callback.onDragProgress(alpha, 0.0f);

                    if (rightView != null && leftView != null){

//                        Log.i("alpha: ", Float.toString(alpha));
                        rightView.setAlpha(alpha);
                        leftView.setAlpha(-alpha);
                    }

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
                    if(alpha >= 0) {
                        alpha = (float)Utils.clamp(alpha, 0.0f, 1.0f);
                    }else{
                        alpha = (float)Utils.clamp(alpha, -1.0f, -0.0f);
                    }

                    //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                    //Log.i("alpha: ", Float.toString(alpha));
                    callback.onDragProgress(0.0f, alpha);
                    if (topView != null && bottomView != null){
                        //set alpha of left and right image

                        topView.setAlpha(-alpha);
                        bottomView.setAlpha(alpha);
                    }

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

                    if(alphaX >= 0) {
                        alphaX = (float)Utils.clamp(alphaX, 0.0f, 1.0f);
                    }else{
                        alphaX = (float)Utils.clamp(alphaX, -1.0f, -0.0f);
                    }

                    if(alphaY >= 0) {
                        alphaY = (float)Utils.clamp(alphaY, 0.0f, 1.0f);
                    }else{
                        alphaY = (float)Utils.clamp(alphaY, -1.0f, -0.0f);
                    }

                    callback.onDragProgress(alphaX, alphaY);

                    if (rightView != null && leftView != null){
                        //set alpha of left and right image

                        //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                        //Log.i("alpha: ", Float.toString(alpha));
                        rightView.setAlpha(alphaX);
                        leftView.setAlpha(-alphaX);
                    }

                    if (topView != null && bottomView != null){
                        //set alpha of left and right image

                        //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                        //Log.i("alpha: ", Float.toString(alpha));
                        topView.setAlpha(alphaY);
                        bottomView.setAlpha(-alphaY);
                    }
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
        return (card.getX() + (card.getWidth() / 2) < (parentWidth / 4.f));
    }

    private boolean cardBeyondRightBorder() {
//        Log.i(TAG, "cardBeyondRightBorder");
        //check if card middle is beyond the right quarter of the screen
        return (card.getX() + (card.getWidth() / 2) > ((parentWidth / 4.f) * 3));
    }

    private boolean cardBeyondTopBorder() {
//        Log.i(TAG, "cardBeyondTopBorder");
        //check if cards middle is beyond the left quarter of the screen
        return (card.getY() + (card.getHeight() / 2) < (parentHeight / 4.f));
    }

    private boolean cardBeyondBottomBorder() {
//        Log.i(TAG, "cardBeyondBottomBorder");
        //check if card middle is beyond the right quarter of the screen
        return (card.getY() + (card.getHeight() / 2) > ((parentHeight / 4.f) * 3));
    }

    private ViewPropertyAnimator resetCardPosition() {

//        Log.i(TAG, "resetCardPosition");

        if(rightView!=null)rightView.setAlpha(0);
        if(leftView!=null)leftView.setAlpha(0);
        if(topView!=null)topView.setAlpha(0);
        if(bottomView!=null)bottomView.setAlpha(0);

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

    public void setRightView(View view) {
        this.rightView = view;
    }

    public void setLeftView(View image) {
        this.leftView = image;
    }

    public void setBottomView(View view) {
        this.bottomView = view;
    }

    public void setTopView(View view) {
        this.topView = view;
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
