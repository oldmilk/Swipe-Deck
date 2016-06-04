package psn.oldmilk.swipecard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by CarlChia on 26/5/16.
 */
public abstract class SwipeDeckAdapter extends BaseAdapter {

    public abstract View getOutLeftView(int position);
    public abstract View getOutRightView(int position);
    public abstract View getOutTopView(int position);
    public abstract View getOutBottomView(int position);

    public abstract SwipeActions getActions(int position);


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public SwipeCardView getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
