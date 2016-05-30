package com.daprlabs.swipedeck;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daprlabs.cardstack.SwipeActions;
import com.daprlabs.cardstack.SwipeCardView;
import com.daprlabs.cardstack.SwipeDeck;
import com.daprlabs.cardstack.SwipeDeckAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SwipeDeckActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SwipeDeck cardStack;
    private Context context = this;

    private SwipeDeckAdapter adapter;
    private ArrayList<String> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_deck);
        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        cardStack.setHardwareAccelerationEnabled(true);

        testData = new ArrayList<>();
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");

        adapter = new MySwipeDeckAdapter(testData, this, cardStack);
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {

            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
            }

            @Override
            public void cardSwipedUp(int position) {

            }

            @Override
            public void cardSwipedDown(int position) {

            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
            }

            @Override
            public void cardActionDown() {
                Log.i(TAG, "cardActionDown");
            }

            @Override
            public void cardActionUp() {
                Log.i(TAG, "cardActionUp");
            }

            @Override
            public void cardResetPosition() {

            }

            @Override
            public void onDragProgress(float xProgress, float yProgress) {

            }

        });
//        cardStack.clearLeftViewResourceIdList();
//        cardStack.addLeftViewResourceId(R.id.left_image);
//
//        cardStack.clearRightViewResourceIdList();
//        cardStack.addRightViewResourceId(R.id.right_image);

//        Button btn = (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardStack.swipeTopCardLeft(180);
//
//            }
//        });
//        Button btn2 = (Button) findViewById(R.id.button2);
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardStack.swipeTopCardRight(180);
//            }
//        });
//
        Button btn3 = (Button) findViewById(R.id.button_add);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testData.add("a sample string.");
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_swipe_deck, menu);
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

    public class MySwipeDeckAdapter extends SwipeDeckAdapter {

        private List<String> data;
        private Context context;
        private SwipeDeck mSwipeDeck;

        public MySwipeDeckAdapter(List<String> data, Context context, SwipeDeck swipeDeck) {
            this.data = data;
            this.context = context;
            this.mSwipeDeck = swipeDeck;
        }

        @Override
        public View getOutLeftView(int position) {

            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.left_view, null);

            TextView textView = (TextView)v.findViewById(R.id.leftview_text);
            textView.setText(String.valueOf(position));

            return v;
        }

        @Override
        public View getOutRightView(int position) {

            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.right_view, null);
            return v;
        }

        @Override
        public View getOutTopView(int position) {
            return null;
        }

        @Override
        public View getOutBottomView(int position) {
            return null;
        }

        @Override
        public SwipeActions getActions(int position) {

            SwipeActions actions = new SwipeActions() {

                @Override
                public void onSwipeUp() {
                    Log.e("SwipeActions","onSwipteUp");
                }

                @Override
                public void onSwipeDown() {
                    Log.e("SwipeActions","onSwipteDown");
                }

                @Override
                public void onSwipeLeft() {
                    Log.e("SwipeActions","onSwipteLeft");
                }

                @Override
                public void onSwipeRight() {
                    Log.e("SwipeActions","onSwipteRight");
                }
            };
            return actions;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public SwipeCardView getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            SwipeCardView v = (SwipeCardView)inflater.inflate(R.layout.test_card3, parent, false);
//            SwipeCardView v = convertView;
//            if (v == null) {
//                // normally use a viewholder
//
//            }
            //((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));
            ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
            Picasso.with(context).load(R.drawable.food).fit().centerCrop().into(imageView);
            TextView textView = (TextView) v.findViewById(R.id.sample_text);
            String item = (String)getItem(position);
            textView.setText(item);

            RelativeLayout leftHoverCardView = (RelativeLayout) v.findViewById(R.id.left_hover);
            ImageView leftArrowImageView = (ImageView) v.findViewById(R.id.left_arrow);
            v.addLeftView(leftHoverCardView);
            v.addLeftView(leftArrowImageView);

            RelativeLayout rightHoverCardView = (RelativeLayout) v.findViewById(R.id.right_hover);
            ImageView rightArrowImageView = (ImageView) v.findViewById(R.id.right_arrow);
            v.addRightView(rightHoverCardView);
            v.addRightView(rightArrowImageView);

//            mSwipeDeck.clearLeftViewResourceIdList();
//            mSwipeDeck.clearRightViewResourceIdList();
//            mSwipeDeck.clearTopViewResourceIdList();
//            mSwipeDeck.clearBottomViewResourceIdList();
//
//            mSwipeDeck.addLeftViewResourceId(R.id.left_hover);
////            mSwipeDeck.addLeftViewResourceId(R.id.left_background);
//            mSwipeDeck.addLeftViewResourceId(R.id.left_indicator);
//
//            mSwipeDeck.addRightViewResourceId(R.id.right_hover);
////            mSwipeDeck.addRightViewResourceId(R.id.right_background);
//            mSwipeDeck.addRightViewResourceId(R.id.right_indicator);

//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
//                    Log.i("Hwardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
//                    Intent i = new Intent(v.getContext(), BlankActivity.class);
//                    v.getContext().startActivity(i);
//                }
//            });
            return v;
        }
    }
}
