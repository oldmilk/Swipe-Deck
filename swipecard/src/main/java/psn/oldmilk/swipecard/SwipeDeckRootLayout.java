package psn.oldmilk.swipecard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by aaron on 23/12/2015.
 */
public class SwipeDeckRootLayout extends RelativeLayout {
    public SwipeDeckRootLayout(Context context) {
        super(context);
        setClipChildren(false);
    }

    public SwipeDeckRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
    }

    public SwipeDeckRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeDeckRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setClipChildren(false);
    }

}
