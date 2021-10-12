package com.mohammadkiani.androidroomdbdemo.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkiani.androidroomdbdemo.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Swipe Helper class
 * You need to call the constructor of the class and override the abstract method "instantiateSwipeButton"
 * in this method you pass the recyclerView viewHolder and the list of buttons you want to have
 */
public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as*/

    private final int buttonWidth;
    private RecyclerView recyclerView;
    private List<SwipeUnderlayButton> buttonList;
    private GestureDetector gestureDetector;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<SwipeUnderlayButton>> buttonBuffer;
    private Queue<Integer> removeQueue;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (SwipeUnderlayButton button : buttonList) {
                if (button.onclick(e.getX(), e.getY()))
                    break;
            }
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (swipePosition < 0) return false;
            Point point = new Point((int) event.getRawX(), (int) event.getRawY());

            RecyclerView.ViewHolder swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition);
            View swipedItem = swipeViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y)
                    gestureDetector.onTouchEvent(event);
                else {
                    removeQueue.add(swipePosition);
                    swipePosition = -1;
                    recoverSwipedItem();
                }
            }
            return false;
        }
    };

    private synchronized void recoverSwipedItem() {
        while (!removeQueue.isEmpty()) {
            int position = removeQueue.poll();
            if (position > -1)
                recyclerView.getAdapter().notifyItemChanged(position);
        }

    }

    /*public SwipeHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }*/

    /**
     * The class SwipeUnderlayButton is for creating underlay buttons
     */
    protected class SwipeUnderlayButton {
        private Context context;
        private String text;
        private int imageResId, textSize, color, position, cornerSize;
        private RectF clickRegion;
        private SwipeUnderlayButtonClickListener listener;
        private Resources resources;

        /**
         * SwipeUnderlayButton is used to create underlay buttons in swipe action
         * @param context the context of the app
         * @param text the text to be shown on the button in case the drawable not provided
         * @param imageResId the vector asset you have in drawable folder, 0 in case no drawable provided
         * @param textSize the size (int) of the button text
         * @param cornerSize the size (int) of the corner of the button round rect, 0 in case you want sharp corners
         * @param color background color of the button
         * @param listener handler of action
         */
        public SwipeUnderlayButton(Context context, String text, int imageResId, int textSize, int cornerSize, int color, SwipeUnderlayButtonClickListener listener) {
            this.context = context;
            this.text = text;
            this.imageResId = imageResId;
            this.textSize = textSize;
            this.cornerSize = cornerSize;
            this.color = color;
            this.listener = listener;
            this.resources = context.getResources();
        }

        public boolean onclick(float x, float y) {
            if (clickRegion != null && clickRegion.contains(x, y)) {
                listener.onClick(position);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas c, RectF rectF, int position) {
            Paint p = new Paint();
            // background
            p.setColor(color);
//            c.drawRect(rectF, p);
            c.drawRoundRect(rectF, cornerSize, cornerSize, p);
            // text
            p.setColor(Color.WHITE);
            p.setTextSize(textSize);

            Rect r = new Rect();
            float cHeight = rectF.height();
            float cWidth = rectF.width();
            p.setTextAlign(Paint.Align.LEFT);
            p.getTextBounds(text, 0, text.length(), r);
            float x = 0, y = 0;
            if (imageResId == 0) {
                // if it is showing only text
                x = cWidth/2f - r.width()/2f - r.left;
                y = cHeight/2f + r.height()/2f - r.bottom;
                c.drawText(text, rectF.left + x, rectF.top + y, p);
            } else {
                Drawable d = ContextCompat.getDrawable(context, imageResId);
                Bitmap bitmap = drawableToBitmap(d);
                c.drawBitmap(bitmap, (rectF.left+rectF.right)/2 - textSize,(rectF.top+rectF.bottom)/2 - textSize,p);
            }
            clickRegion = rectF;
            this.position = position;
        }

        private Bitmap drawableToBitmap(Drawable d) {
            if (d instanceof BitmapDrawable)
                return ((BitmapDrawable) d).getBitmap();
            Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(),
                    d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0,0,canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
            return bitmap;
        }

    }

    /**
     *
     * @param context the context should be passed to be used in swipeHelper class
     * @param buttonWidth the width of buttons
     * @param recyclerView the instance of recyclerView in the app
     */
    public SwipeHelper(Context context, int buttonWidth, RecyclerView recyclerView) {
        super(0, ItemTouchHelper.LEFT);
        this.buttonWidth = buttonWidth;
        this.recyclerView = recyclerView;
        this.buttonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.buttonBuffer = new HashMap<>();

        removeQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer integer) {
                if (contains(integer))
                    return false;
                else
                    return super.add(integer);
            }
        };
        attachSwipe();
    }

    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Override methods
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (swipePosition != position)
            removeQueue.add(swipePosition);
        swipePosition = position;
        if (buttonBuffer.containsKey(swipePosition))
            buttonList = buttonBuffer.get(swipePosition);
        else
            buttonList.clear();
        buttonBuffer.clear();
        swipeThreshold = 0.5f * buttonList.size() * buttonWidth;
        recoverSwipedItem();
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof RecyclerViewAdapter.ViewHolder)
            if (((RecyclerViewAdapter.ViewHolder) viewHolder).isDepartmentView())
                return 0;
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f*defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f*defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int position = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if (position < 0) {
            swipePosition = position;
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                List<SwipeUnderlayButton> buffer = new ArrayList<>();
                if (!buttonBuffer.containsKey(position)) {
                    instantiateSwipeButton(viewHolder, buffer);
                    buttonBuffer.put(position, buffer);
                } else {
                    buffer = buttonBuffer.get(position);
                }
                translationX = dX*buffer.size()*200 / itemView.getWidth();
                drawButton(c, itemView, buffer, position, translationX);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);

    }

    private void drawButton(Canvas c, View itemView, List<SwipeUnderlayButton> buffer, int position, float translationX) {
        float right = itemView.getRight();
        float dButtonWidth = -1*translationX / buffer.size();
        for (SwipeUnderlayButton button : buffer) {
            float left = right - dButtonWidth;
            button.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), position);
            right = left;
        }
    }

    /**
     * An abstract method to be overridden in order to create underlay buttons
     * @param viewHolder recyclerView ViewHolder
     * @param buffer list of buttons
     */
    protected abstract void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeUnderlayButton> buffer);
}
