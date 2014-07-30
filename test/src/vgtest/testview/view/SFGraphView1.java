// Copyright (c) 2014, https://github.com/rhcad/touchvg

package vgtest.testview.view;

import rhcad.touchvg.IGraphView;
import rhcad.touchvg.IViewHelper;
import rhcad.touchvg.ViewFactory;
import rhcad.touchvg.view.SFGraphView;
import vgtest.testview.TestFlags;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import democmds.core.DemoCmdsGate;

public class SFGraphView1 extends SFGraphView {
    protected static final String PATH = "mnt/sdcard/TouchVG/";

    static {
        System.loadLibrary("democmds");
    }

    public SFGraphView1(Context context) {
        this(context, null);
    }

    public SFGraphView1(Context context, Bundle savedInstanceState) {
        super(context, savedInstanceState);

        final int flags = ((Activity) context).getIntent().getExtras().getInt("flags");
        final IViewHelper helper = ViewFactory.createHelper(this);

        if (savedInstanceState == null
                && (flags & (TestFlags.RECORD | TestFlags.RAND_SHAPES)) != 0) {
            setOnFirstRegenListener(new OnFirstRegenListener() {

                public void onFirstRegen(IGraphView view) {
                    if ((flags & TestFlags.RAND_SHAPES) != 0) {
                        helper.addShapesForTest();
                    }
                    if ((flags & TestFlags.RECORD) != 0) {
                        helper.startRecord(PATH + "record");
                    }
                }
            });
        }
        if ((flags & TestFlags.SWITCH_CMD) != 0) {
            setOnGestureListener(new OnDrawGestureListener() {

                public boolean onPreGesture(int gestureType, float x, float y) {
                    helper.switchCommand();
                    Toast.makeText(getContext(), helper.getCommand(), Toast.LENGTH_SHORT).show();
                    return true;
                }

                public void onPostGesture(int gestureType, float x, float y) {
                }
            });
        }

        switch (flags & TestFlags.CMD_MASK) {
        case TestFlags.SELECT_CMD:
            helper.setCommand("select");
            break;
        case TestFlags.SPLINES_CMD:
            helper.setCommand("splines");
            break;
        case TestFlags.LINE_CMD:
            helper.setCommand("line");
            break;
        case TestFlags.LINES_CMD:
            helper.setCommand("lines");
            break;
        case TestFlags.HITTEST_CMD:
            int n = DemoCmdsGate.registerCmds(helper.cmdViewHandle());
            helper.setCommand("hittest");
            Log.d("Test", "DemoCmdsGate.registerCmds = " + n + ", " + helper.getCommand());
            break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        int flags = ((Activity) getContext()).getIntent().getExtras().getInt("flags");
        if ((flags & TestFlags.HAS_BACKDRAWABLE) != 0) {
            ViewGroup layout = (ViewGroup) getParent();
            this.setBackgroundDrawable(layout.getBackground());
        }
    }
}
