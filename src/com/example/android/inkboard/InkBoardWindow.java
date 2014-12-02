package com.example.android.inkboard;

import com.example.android.softkeyboard.LatinKeyboard;
import com.example.android.softkeyboard.LatinKeyboardView;
import com.example.android.softkeyboard.SoftKeyboard;
import com.example.android.softkeyboard.R;

import android.content.Context;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.widget.PopupWindow;

public class InkBoardWindow extends PopupWindow {
	
	private static final String TAG = "InkBoardWindow";
	
	private static final boolean DEBUG = true;
	
	private WindowManager mWindowManager;
	
	private InputMethodService mInputMethodService;
	
	private InputConnection mInputConnection;
	
	private InkBoardView mInkBoardView;
	
	private View mWindowView;
	
	private View mAnchorView;
	
	private boolean mRegister;
	
	private boolean mViewHide;
	
	private int mScreenWidth;
	
	private int mScreenHeight;
	
	public InkBoardWindow(InputMethodService service) {
		mWindowView = service.getLayoutInflater().inflate(R.layout.inkboardview, null);
		mWindowManager = (WindowManager) service.getSystemService(Context.WINDOW_SERVICE);
		mInkBoardView = (InkBoardView) mWindowView.findViewById(R.id.inkboardview);
		mInkBoardView.setWindow(this);
		mInputMethodService = service;
		mRegister = false;
		mViewHide = false;
	}
	
	private void updateWindowSize() {
		DisplayMetrics displayMetrics = mInputMethodService.getResources().getDisplayMetrics();
		mScreenWidth = displayMetrics.widthPixels;
		mScreenHeight = displayMetrics.heightPixels;
		//LatinKeyboard keyboard = ((SoftKeyboard) mInputMethodService).getQwertyKeyboard();
		this.setWidth(mScreenWidth);
		this.setHeight(mScreenHeight);
	}
	
	private int getStatusBarHeight() {
		int height = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
		if (DEBUG) Log.e(TAG, "" + height);
        return height;
    }
	
	private void updateInputConnection() {
		mInputConnection = mInputMethodService.getCurrentInputConnection();
	}
	
	private void show() throws WindowUnRegisterException {
		if(mRegister == false) throw new WindowUnRegisterException();
		this.showAsDropDown(mAnchorView, 0, 0 - mScreenHeight);
	}
	
	public void registerAndShowAtFirst(View view) {
		mAnchorView = view;
		this.showAsDropDown(mAnchorView, 0, 0 - mScreenHeight);
		mRegister = true;
	}
	
	public boolean isRegister() {
		return mRegister;
	}
	
	public void onWindowCreate() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on create");
		this.setContentView(mWindowView);
	}
	
	public void onWindowInitializeInterface() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on initialize");
		updateWindowSize();
	}
	
	public void onWindowCreateInputView() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on create input view");
	}
	
	public void onWindowStartInput() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on start input");
		updateInputConnection();
	}
	
	public void onWindowStartInputView() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on start input view");
		try {
			show();
		} catch (WindowUnRegisterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onWindowFinishInputView() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on finish input view");
		dismiss();
	}
	
	public void onWindowFinishInput() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on finish input");

	}
	
	public void onWindowDestroy() {
		if (DEBUG) Log.v(TAG, "Ink board popup window on destroy");
		dismiss();
	}
	
	public void commitTextToService() {
		mInputConnection.commitText("8", 1);
	}
	
	public void allCancel() {
		this.dismiss();
		mInputMethodService.hideWindow();
	}
	
	public boolean checkPoint(MotionEvent event) {
		LatinKeyboard keyboard = ((SoftKeyboard) mInputMethodService).getEmptyKeyboard();
		if(event.getY() >= 0 && event.getY() < mScreenHeight - keyboard.getHeight() - getStatusBarHeight()) {
			return true;
		}
		return false;
	}
	
	public void checkEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//this.cancel();
		LatinKeyboard keyboard = ((SoftKeyboard) mInputMethodService).getEmptyKeyboard();
		if(checkPoint(event)) {
			allCancel();
		}
		else {
			MotionEvent eventDown = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, event.getX(),
					event.getY() + keyboard.getHeight() - mScreenHeight + getStatusBarHeight(), 0);
			MotionEvent eventUp = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, event.getX(), 
					event.getY() + keyboard.getHeight() - mScreenHeight + getStatusBarHeight(), 0);
			//mInputMethodService.getWindow().cancel();
			LatinKeyboardView keyboardView = ((SoftKeyboard) mInputMethodService).getInputView();
			boolean down = keyboardView.onTouchEvent(eventDown);
			boolean up = keyboardView.onTouchEvent(eventUp);
			//mInputMethodService.getWindow().dispatchTouchEvent(event);
		}
	}
	
	class WindowUnRegisterException extends Exception {
		private static final String TAG = "WindowUnRegisterException";
		private static final String DETAIL_MESSAGE = "Your popup window not register a displayed view";
		WindowUnRegisterException () {
			super(DETAIL_MESSAGE);
		}
	}
	
}
