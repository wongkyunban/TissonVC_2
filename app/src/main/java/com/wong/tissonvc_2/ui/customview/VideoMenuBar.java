package com.wong.tissonvc_2.ui.customview;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Video menu bar.
 * <p/>
 * VideoMenuBar
 * Video menu control bar
 */
public class VideoMenuBar implements View.OnClickListener, Runnable
{
    /**
     * MORE
     */
    public static final String MORE = "more";

    /**
     * mic
     */
    public static final String MIC = "mic";

    /**
     * SPEAKER
     */
    public static final String SPEAKER = "speaker";

    /**
     * AUDIO_VIDEO
     */
    public static final String AUDIO_VIDEO = "audio2video";

    /**
     * REDIAL_BOARD
     */
    public static final String REDIAL_BOARD = "redialBoard";



    /**
     * SWITCH_AUDIO_ROUTE
     */
    public static final String SWITCH_AUDIO_ROUTE = "switchAudioRoute";

    /**
     * 会场列表
     */
    public static final String CONFLIST = "conflist";

    public static final String DATASHARE = "datashare";

    /**
     * HANG_UP
     */
    public static final String HANG_UP = "hangup";

    private static final String ISGONE = "gone";

    /**
     * DURATION
     */
    private static final int DURATION = 100;

    private static final String TAG = VideoMenuBar.class.getSimpleName();

    private static final String THREAD_NAME = "hiddenThread";

    /**
     * startTime
     */
    private long startTime = 0;

    private MenuItemServer itemServer;

    private Map<String, MenuBarItem> items = new HashMap<String, MenuBarItem>(10);

    private List<MenuBarItem> itemsList = new ArrayList<MenuBarItem>(10);

    /**
     * linkViews
     */
    private List<View> linkViews = new ArrayList<View>(10);

    /**
     * rootView
     */
    private View rootView;

    /**
     * menu
     */
    private View menuBar;

    private boolean autoHidden;

    private boolean isNeedShowDialog = true;

    private boolean threadStart = false;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            dismiss();
        }
    };

    /**
     * constructor
     *
     * @param rootViewVar the root view var
     */
    public VideoMenuBar(View rootViewVar)
    {
        this.rootView = rootViewVar;
        menuBar = rootView.findViewById(R.id.menu_bar);
        initListItemView();
        menuBar.setVisibility(View.GONE);
    }

    /**
     * setItemServer
     *
     * @param itemServer the item server
     */
    public void setItemServer(MenuItemServer itemServer)
    {
        this.itemServer = itemServer;
    }

    /**
     * initListItemView
     *
     * @return Map<String MenuBarItem>
     */
    public Map<String, MenuBarItem> initListItemView()
    {
        MenuBarItem menuItem = null;

        View more = rootView.findViewById(R.id.more);
        more.setOnClickListener(this);
        menuItem = new MenuBarItem(more, rootView.findViewById(R.id.line_more));
        itemsList.add(menuItem);
        items.put(MORE, menuItem);

        View videoMic = rootView.findViewById(R.id.video_mic);
        videoMic.setOnClickListener(this);
        menuItem = new MenuBarItem(videoMic, rootView.findViewById(R.id.line_video_mic));
        itemsList.add(menuItem);
        items.put(MIC, menuItem);

        View videoSpeaker = rootView.findViewById(R.id.video_speaker);
        videoSpeaker.setOnClickListener(this);
        menuItem = new MenuBarItem(videoSpeaker, rootView.findViewById(
                R.id.line_video_speaker));
        itemsList.add(menuItem);
        items.put(SPEAKER, menuItem);

        View audioSwitchVideo = rootView.findViewById(R.id.audio_switch_video);
        audioSwitchVideo.setOnClickListener(this);
        menuItem = new MenuBarItem(audioSwitchVideo, rootView.findViewById(
                R.id.line_audio_switch_video));
        itemsList.add(menuItem);
        items.put(AUDIO_VIDEO, menuItem);

        View redialBoard = rootView.findViewById(R.id.redial_board);
        redialBoard.setOnClickListener(this);
        menuItem = new MenuBarItem(redialBoard, rootView.findViewById(
                R.id.line_redial_board));
        itemsList.add(menuItem);
        items.put(REDIAL_BOARD, menuItem);


        View confView = rootView.findViewById(R.id.vcConf);
        confView.setOnClickListener(this);
        menuItem = new MenuBarItem(confView, rootView.findViewById(R.id.vcConf));
        itemsList.add(menuItem);
        items.put(CONFLIST, menuItem);

        View shareView = rootView.findViewById(R.id.dataShare);
        shareView.setOnClickListener(this);
        menuItem = new MenuBarItem(shareView, rootView.findViewById(R.id.dataShare));
        itemsList.add(menuItem);
        items.put(DATASHARE, menuItem);




        View audioRoute = rootView.findViewById(R.id.switch_audio_route);
        audioRoute.setOnClickListener(this);
        menuItem = new MenuBarItem(audioRoute, null);
        itemsList.add(menuItem);
        items.put(SWITCH_AUDIO_ROUTE, menuItem);

        View videoHangup = rootView.findViewById(R.id.video_hangup);
        videoHangup.setOnClickListener(this);
        menuItem = new MenuBarItem(videoHangup, null);
        itemsList.add(menuItem);
        items.put(HANG_UP, menuItem);

        resetAllMenuItems();
        return items;
    }

    /**
     * getMenuItems
     *
     * @param item the item
     * @return View menu items
     */
    public View getMenuItems(String item)
    {
        TUPLogUtil.i(TAG, "getMenuItems()");
        if (null == items.get(item))
        {
            TUPLogUtil.i(TAG, "items.get(item) is null");
            return null;
        }
        return items.get(item).getItem();
    }

    /**
     * setMenuItemVisible
     *
     * @param item    the item
     * @param visible the visible
     */
    public void setMenuItemVisible(String item, int visible)
    {
        if (null == items.get(item))
        {
            return;
        }
        items.get(item).setItemVisible(visible);
    }

    /**
     * Is video audio gone.
     */
    public void isVideoAudioGONE()
    {
        if (null != items.get(AUDIO_VIDEO).getItemLine()
                && ISGONE.equals(items.get(AUDIO_VIDEO).getItemLine().getTag()))
        {
            items.get(AUDIO_VIDEO).setItemVisible(View.GONE);
        }
    }

    /**
     * setItemLineVisibility
     *
     * @param itemName   the item name
     * @param visibility the visibility
     */
    public void setItemLineVisibility(String itemName, int visibility)
    {
        if (null != items.get(itemName).getItemLine())
        {
            items.get(itemName).getItemLine().setVisibility(visibility);
        }
    }

    /**
     * getMenuItemsImg
     *
     * @param item the item
     * @return the menu items img
     */
    public ImageView getMenuItemsImg(String item)
    {
        if (null == items.get(item))
        {
            TUPLogUtil.i(TAG, "items.get(item) is null, item:" + item);
        }
        else
        {
            TUPLogUtil.i(TAG, "items.get(item) is not null, item:" + item);
        }
        return items.get(item).getItemImg();
    }

    /**
     * coverTime
     */
    public void coverTime()
    {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onClick(View viewVar)
    {
        startTime = System.currentTimeMillis();
        if (null == itemServer)
        {
            return;
        }
        int id = viewVar.getId();
        if (id == R.id.more)
        {
            if (View.VISIBLE == menuBar.getVisibility())
            {
                itemServer.showMoreOpre(viewVar);
            }
        }
        else if (id == R.id.video_mic)
        {
            itemServer.closeMIC(getMenuItemsImg(MIC));
        }
        else if (id == R.id.video_speaker)
        {
            itemServer.closeSpeaker(getMenuItemsImg(SPEAKER));
        }
        else if (id == R.id.switch_audio_route)
        {
            itemServer.switchAudioRoute(getMenuItemsImg(SWITCH_AUDIO_ROUTE));
        }
        else if (id == R.id.audio_switch_video)
        {
            itemServer.videoToAudio(viewVar);
        }
        else if (id == R.id.redial_board)
        {
            itemServer.audioRecall(viewVar);
        }
        else if (id == R.id.video_hangup)
        {
            endCall(viewVar);
        }
        else if (id == R.id.vcConf)
        {
            itemServer.showConfList();
        }else if (id == R.id.dataShare){
            itemServer.gotoShare();
        }
        else
        {
            endCall(viewVar);
        }
    }

    /**
     * resetAllMenuItems
     */
    public void resetAllMenuItems()
    {
        MenuBarItem item = null;
        int itemSize = itemsList.size();
        for (int i = 0; i < itemSize; i++)
        {
            item = itemsList.get(i);
            item.getItem().setVisibility(View.VISIBLE);
            if (null != item.getItemImg())
            {
                item.getItemImg().setEnabled(true);
                item.getItemImg().getDrawable().setAlpha(255);
                item.getItemImg().setVisibility(View.VISIBLE);
            }
            if (null != item.getItemLine() && ISGONE.equals(item.getItemLine().getTag()))
            {
                item.getItem().setVisibility(View.GONE);
            }
        }
    }

    /**
     * Sets need show.
     *
     * @param isNeedShow the is need show
     */
    public void setNeedShow(boolean isNeedShow)
    {
        this.isNeedShowDialog = isNeedShow;
    }

    /**
     * Gets menu bar.
     *
     * @return the menu bar
     */
    public View getMenuBar()
    {
        return menuBar;
    }

    @Override
    public void run()
    {
        long timeSpacing = System.currentTimeMillis() - startTime;
        try
        {
            while (timeSpacing < 5000)
            {
                sleepThread();
                timeSpacing = System.currentTimeMillis() - startTime;
            }
        }
        catch (Exception e)
        {
            TUPLogUtil.i(TAG, "AbsMenuBar thread error");
        }
        handler.sendEmptyMessage(0);
        threadStart = false;

    }

    /**
     * Dismiss.
     */
    public void dismiss()
    {
        if (!autoHidden)
        {
            return;
        }
        itemServer.dismissMorePopWindow();

        if (null != linkViews)
        {
            View view = null;
            int size = linkViews.size();
            for (int i = 0; i < size; i++)
            {
                view = linkViews.get(i);
                view.setAnimation(null);
                view.setVisibility(View.GONE);
            }
        }
        menuBar.setVisibility(View.GONE);
    }

    /**
     * showAndGone
     */
    public void showAndGone()
    {
        if (View.VISIBLE == menuBar.getVisibility())
        {
            startTime += 5000;
            menuBar.setAnimation(null);
            dismiss();
            return;
        }

        startTime = System.currentTimeMillis();
        animationIn(menuBar);
        menuBar.setVisibility(View.VISIBLE);
        TUPLogUtil.i(TAG, "menubar show()");
        View view = null;
        int linkSize = linkViews.size();
        for (int i = 0; i < linkSize; i++)
        {
            view = linkViews.get(i);
            if (null != view.getAnimation())
            {
                view.getAnimation().startNow();
                continue;
            }
            view.setVisibility(View.VISIBLE);
            animationIn(view);
        }

        startTime = System.currentTimeMillis();
        if (!threadStart)
        {
            new Thread(this, THREAD_NAME).start();
            threadStart = true;
        }
    }

    /**
     * setAutoHidden
     *
     * @param autoHidden the auto hidden
     */
    public void setAutoHidden(boolean autoHidden)
    {
        this.autoHidden = autoHidden;
    }

    /**
     * animationIn
     *
     * @param view the view
     */
    protected void animationIn(final View view)
    {
        AlphaAnimation anim = null;
        anim = new AlphaAnimation(0f, 1.0f);
        anim.setDuration(500);
        anim.setAnimationListener(new AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (linkViews.contains(view) || view == menuBar)
                {
                    view.setVisibility(View.VISIBLE);
                }
                view.postInvalidate();
            }
        });

        view.startAnimation(anim);
    }

    /**
     * clearData
     */
    public void clearData()
    {
        if (null != itemsList)
        {
            itemsList.clear();
        }
        itemsList = null;
        if (null != items)
        {
            items.clear();
        }
        items = null;
        if (null != linkViews)
        {
            linkViews.clear();
        }
        linkViews = null;
        handler = null;
    }

    private void sleepThread()
    {
        try
        {
            Thread.sleep(DURATION);
        }
        catch (InterruptedException e)
        {
            TUPLogUtil.e(TAG, "thread sleep error.");
        }
    }

    /**
     * endCall
     *
     * @param view
     */
    private void endCall(View view)
    {
        if (!isNeedShowDialog)
        {
            TUPLogUtil.i(TAG, "No need to show confirm dialog "
                    + "because the call is ended ,return here!");
            isNeedShowDialog = true;
            return;
        }

        // TO invoke
        itemServer.endVideoCall();
    }

    /**
     * MenuBarItem
     */
    private static final class MenuBarItem
    {
        private View item;
        private ImageView itemImg;
        private View lineView;

        private MenuBarItem(View itemVar, View lineViewVar)
        {
            if (null == itemVar)
            {
                return;
            }
            this.item = itemVar;
            if (itemVar instanceof ViewGroup)
            {
                itemImg = (ImageView) ((ViewGroup) item).getChildAt(0);
            }
            this.lineView = lineViewVar;
        }

        /**
         * setItemVisible
         *
         * @param visible the visible
         */
        public void setItemVisible(int visible)
        {
            if (null != item)
            {
                item.setVisibility(visible);
            }
            if (null != lineView)
            {
                lineView.setVisibility(visible);
            }
        }

        /**
         * Gets item.
         *
         * @return the item
         */
        public View getItem()
        {
            return item;
        }

        /**
         * Gets item img.
         *
         * @return the item img
         */
        public ImageView getItemImg()
        {
            return itemImg;
        }

        /**
         * Gets item line.
         *
         * @return the item line
         */
        public View getItemLine()
        {
            return lineView;
        }
    }

    /**
     * The interface Menu item server.
     */
    public interface MenuItemServer
    {

        /**
         * showMoreOpre
         *
         * @param view the view
         */
        void showMoreOpre(View view);

        /**
         * closeMIC
         *
         * @param view the view
         */
        void closeMIC(ImageView view);

        /**
         * closeSpeaker
         *
         * @param view the view
         */
        void closeSpeaker(ImageView view);

        /**
         * switchAudioRoute
         *
         * @param view the view
         */
        void switchAudioRoute(ImageView view);


        /**
         * switchCamera
         *
         * @param view the view
         */
        void switchCamera(View view);

        /**
         * videoToAudio
         *
         * @param view the view
         */
        void videoToAudio(View view);

        /**
         * audioRecall
         *
         * @param view the view
         */
        void audioRecall(View view);

        /**
         * endVideoCall
         */
        void endVideoCall();

        /**
         * Dismiss popup window.
         */
        void dismissPopupWindow();

        /**
         * dismissMorePopWindow
         */
        void dismissMorePopWindow();

        /**
         * 查看会场列表
         */
        void showConfList();

        void gotoShare();


    }


}
