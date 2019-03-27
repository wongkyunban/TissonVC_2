package com.wong.tissonvc_2.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.wong.tissonvc_2.R;
import com.wong.tissonvc_2.service.call.CallService;
import com.wong.tissonvc_2.service.common.CallConstants;
import com.wong.tissonvc_2.service.utils.TUPLogUtil;

/**
 * The type Recall pop window.
 * <p/>
 * RecallPopWindow
 * Recall number display view
 */
public class RecallPopWindow extends PopupWindow
{
    private static final String TAG = RecallPopWindow.class.getSimpleName();

    private View topWindow;

    private LayoutInflater inflater;

    private EditText compRecallNum;

    private Button[] buttons;

    /**
     * 0-9[0-9] *--10 #--11
     */
    private int[] numValueInt = {KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9,
            KeyEvent.KEYCODE_STAR, KeyEvent.KEYCODE_POUND};

    private View atchorView;

    private RelativeLayout viewGroup;

    /**
     * Instantiates a new Recall pop window.
     */
    public RecallPopWindow()
    {
        super();
    }

    /**
     * Instantiates a new Recall pop window.
     *
     * @param context    the context
     * @param atchorView the atchor view
     */
    public RecallPopWindow(Context context, View atchorView)
    {
        super(context);
        this.atchorView = atchorView;
        viewGroup = new RelativeLayout(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setBackgroundDrawable(null);
        initReCallPopWindow();
        measureView(topWindow);
        setHeight(topWindow.getMeasuredHeight());
        setWidth(topWindow.getMeasuredWidth());
    }

    /**
     * initReCallPopWindow
     */
    public void initReCallPopWindow()
    {
        TUPLogUtil.i(TAG, "before inflate ");
        topWindow = inflater.inflate(R.layout.recall_pop_window, viewGroup, false);
        TUPLogUtil.i(TAG, "end inflate ");
        compRecallNum = (EditText) topWindow.findViewById(R.id.recallNum);

        compRecallNum.setFocusable(false);
        compRecallNum.setClickable(false);

        compRecallNum.setCursorVisible(false);
        topWindow.setOnTouchListener(new OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                RecallPopWindow.this.dismiss();
                atchorView.setSelected(false);
                compRecallNum.setText("");
                return false;
            }
        });
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(topWindow);

        buttons = new Button[CallConstants.Num.TWELVE];
        buttons[CallConstants.Num.ZERO] = (Button) topWindow.findViewById(R.id.zero);
        buttons[CallConstants.Num.ONE] = (Button) topWindow.findViewById(R.id.one);
        buttons[CallConstants.Num.TWO] = (Button) topWindow.findViewById(R.id.two);
        buttons[CallConstants.Num.THREE] = (Button) topWindow.findViewById(R.id.three);
        buttons[CallConstants.Num.FOUR] = (Button) topWindow.findViewById(R.id.four);
        buttons[CallConstants.Num.FIVE] = (Button) topWindow.findViewById(R.id.five);
        buttons[CallConstants.Num.SIX] = (Button) topWindow.findViewById(R.id.six);
        buttons[CallConstants.Num.SEVEN] = (Button) topWindow.findViewById(R.id.seven);
        buttons[CallConstants.Num.EIGHT] = (Button) topWindow.findViewById(R.id.eight);
        buttons[CallConstants.Num.NINE] = (Button) topWindow.findViewById(R.id.nine);
        buttons[CallConstants.Num.TEN] = (Button) topWindow.findViewById(R.id.star);
        buttons[CallConstants.Num.ELEVEN] = (Button) topWindow.findViewById(R.id.jing);
        int lens = buttons.length;
        for (int i = 0; i < lens; i++)
        {
            setButtonListener(buttons[i], i);
        }
    }

    /**
     * setButtonListener
     */
    private void setButtonListener(final Button button, final int i)
    {
        button.setOnTouchListener(new OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if (compRecallNum != null)
                    {
                        compRecallNum.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                                numValueInt[i]));
                    }
                    CallService.getInstance().redial(i + "");

                }
                return false;
            }
        });
    }

    private void measureView(View view)
    {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }
}
