package com.tianxia.lib.baseworld.widget;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;

import android.util.AttributeSet;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tianxia.lib.baseworld.R;

import java.util.List;

/**
 * 拖拽ListView
 */
public class DragListView extends ListView {

    private int mScaledTouchSlop;//判断滑动的一个距离,scroll的时候会用到

    private ImageView mDragImageView;//被拖拽项的影像，其实就是一个ImageView
    private int mDragSrcPosition;//手指拖动项原始在列表中的位置
    private int mDragPosition;//手指拖动的时候，当前拖动项在列表中的位置

    private int mDragPoint;//在当前数据项中的位置
    private int mDragOffset;//当前视图和屏幕的距离(这里只使用了y方向上)

    private WindowManager mWindowManager;//windows窗口控制类
    private WindowManager.LayoutParams mWindowParams;//用于控制拖拽项的显示的参数

    private int mUpScrollBounce;//拖动的时候，开始向上滚动的边界
    private int mDownScrollBounce;//拖动的时候，开始向下滚动的边界

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //捕获down事件
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)ev.getX();
            int y = (int)ev.getY();

            //选中的数据项位置，使用ListView自带的pointToPosition(x, y)方法
            mDragSrcPosition = mDragPosition = pointToPosition(x, y);
            //如果是无效位置(超出边界，分割线等位置)，返回
            if (mDragPosition == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }

            //获取选中项View
            //getChildAt(int position)显示display在界面的position位置的View
            //getFirstVisiblePosition()返回第一个display在界面的view在adapter的位置position，可能是0，也可能是4
            ViewGroup itemView = (ViewGroup) getChildAt(mDragPosition-getFirstVisiblePosition());

            //mDragPoint点击位置在点击View内的相对位置
            //mDragOffset屏幕位置和当前ListView位置的偏移量，这里只用到y坐标上的值
            //这两个参数用于后面拖动的开始位置和移动位置的计算
            mDragPoint = y - itemView.getTop();
            mDragOffset = (int) (ev.getRawY() - y);

            //获取右边的拖动图标，这个对后面分组拖拽有妙用
            View dragger = itemView.findViewById(R.id.drag_list_item_image);
            //如果在右边位置（拖拽图片左边的20px的右边区域）
            if (dragger != null && x > dragger.getLeft()-20) {
                //准备拖动
                //初始化拖动时滚动变量
                //scaledTouchSlop定义了拖动的偏差位(一般+-10)
                //mUpScrollBounce当在屏幕的上部(上面1/3区域)或者更上的区域，执行拖动的边界，mDownScrollBounce同理定义
                mUpScrollBounce = Math.min(y - mScaledTouchSlop, getHeight()/3);
                mDownScrollBounce = Math.max(y + mScaledTouchSlop, getHeight()*2/3);

                //设置Drawingcache为true，获得选中项的影像bm，就是后面我们拖动的哪个头像
                itemView.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());

                //准备拖动影像(把影像加入到当前窗口，并没有拖动，拖动操作我们放在onTouchEvent()的move中执行)
                startDrag(bm, y);
            }
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
     * 触摸事件
     * */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //如果dragmageView为空，说明拦截事件中已经判定仅仅是点击，不是拖动，返回
        //如果点击的是无效位置，返回，需要重新判断
        if (mDragImageView != null && mDragPosition != INVALID_POSITION) {
            int action = ev.getAction();
            switch(action){
                case MotionEvent.ACTION_UP:
                    int upY = (int)ev.getY();
                    //释放拖动影像
                    stopDrag();
                    //放下后，判断位置，实现相应的位置删除和插入
                    onDrop(upY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int)ev.getY();
                    //拖动影像
                    onDrag(moveY);
                    break;
                default:break;
            }
            return true;
        }
        //这个返回值能够实现selected的选中效果，如果返回true则无选中效果
        return super.onTouchEvent(ev);
    }

    /**
     *  * 准备拖动，初始化拖动项的图像
     *   * @param bm
     *    * @param y
     *     */
    public void startDrag(Bitmap bm ,int y){
        //释放影像，在准备影像的时候，防止影像没释放，每次都执行一下
        stopDrag();

        mWindowParams = new WindowManager.LayoutParams();
        //从上到下计算y方向上的相对位置，
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPoint + mDragOffset;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //下面这些参数能够帮助准确定位到选中项点击位置，照抄即可
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        //把影像ImagView添加到当前视图中
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        mWindowManager = (WindowManager)getContext().getSystemService("window");
        mWindowManager.addView(imageView, mWindowParams);
        //把影像ImageView引用到变量drawImageView，用于后续操作(拖动，释放等等)
        mDragImageView = imageView;
    }

    /**
     *  * 停止拖动，去除拖动项的头像
     *   */
    public void stopDrag(){
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     *  * 拖动执行，在Move方法中执行
     *   * @param y
     *    */
    public void onDrag(int y){
        if (mDragImageView != null) {
            mWindowParams.alpha = 0.8f;
            mWindowParams.y = y - mDragPoint + mDragOffset;
            mWindowManager.updateViewLayout(mDragImageView, mWindowParams);
        }
        //为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            mDragPosition = tempPosition;
        }

        //滚动
        int scrollHeight = 0;
        if (y < mUpScrollBounce) {
            scrollHeight = 8;//定义向上滚动8个像素，如果可以向上滚动的话
        } else if (y > mDownScrollBounce){
            scrollHeight = -8;//定义向下滚动8个像素，，如果可以向上滚动的话
        }

        if (scrollHeight != 0) {
            //真正滚动的方法setSelectionFromTop()
            setSelectionFromTop(mDragPosition, getChildAt(mDragPosition-getFirstVisiblePosition()).getTop()+scrollHeight);
        }
    }

    /*
     * 拖动放下的时候
     * @param y
     * */
    public void onDrop(int y){
        //获取放下位置在数据集合中position
        //定义临时位置变量为了避免滑动到分割线的时候，返回-1的问题，如果为-1，则不修改mDragPosition的值，急需执行，达到跳过无效位置的效果
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            mDragPosition = tempPosition;
        }

        //超出边界处理
        if (y < getChildAt(0).getTop()) {
            //超出上边界，设为最小值位置0
            mDragPosition = 0;
        } else if (y > getChildAt(getChildCount()-1).getBottom()) {
            //超出下边界，设为最大值位置，注意哦，如果大于可视界面中最大的View的底部则是越下界，所以判断中用getChildCount()方法
            //但是最后一项在数据集合中的position是getAdapter().getCount()-1，这点要区分清除
            mDragPosition = getAdapter().getCount() - 1;
        }

        //数据更新
        if (mDragPosition  >= 0 && mDragPosition < getAdapter().getCount()) {
            if (mDropListener != null) {
                mDropListener.onDrop(mDragSrcPosition, mDragPosition);
            }
        }
    }

    //set the drop listener
    public interface OnDropListener {
        void onDrop(int srcPosition, int destPosition);
    }

    private OnDropListener mDropListener = null;
    public void setDropListener (OnDropListener listener) {
        mDropListener = listener;
    }
}
