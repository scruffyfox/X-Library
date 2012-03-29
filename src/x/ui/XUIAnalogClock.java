/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package x.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import java.util.TimeZone;

import x.lib.Debug;

/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 */
@RemoteView public class XUIAnalogClock extends View 
{
	private Time mCalendar;
	private long mCurrentTime;

	private Drawable mHourHand;
	private Drawable mMinuteHand;
	private Drawable mDial;

	private int mDialWidth;
	private int mDialHeight;

	private boolean mAttached;
	private boolean mClockFrozen = false;
	private TimerTick mTimerHandler = new TimerTick();

	private final Handler mHandler = new Handler();
	private float mMinutes;
	private float mHour;
	private boolean mChanged;
	private Context mContext;

	/**
	 * Default constructor
	 * @param context
	 */
	public XUIAnalogClock(Context context) 
	{
		this(context, null);
	}

	/**
	 * Default constructor
	 * @param context
	 * @param attrs
	 */
	public XUIAnalogClock(Context context, AttributeSet attrs) 
	{
		this(context, attrs, 0);
	}

	/**
	 * Default constructor
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public XUIAnalogClock(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		mContext = context;

		Resources r = mContext.getResources();
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XUIAnalogClock, defStyle, 0);

		mDial = a.getDrawable(R.styleable.XUIAnalogClock_dial);
		if (mDial == null) 
		{
			mDial = r.getDrawable(R.drawable.clock_dial);
		}

		mHourHand = a.getDrawable(R.styleable.XUIAnalogClock_hand_hour);
		if (mHourHand == null) 
		{        	
			mHourHand = r.getDrawable(R.drawable.clock_hand_hour);
		}

		mMinuteHand = a.getDrawable(R.styleable.XUIAnalogClock_hand_minute);
		if (mMinuteHand == null) 
		{
			mMinuteHand = r.getDrawable(R.drawable.clock_hand_minute);
		}

		mCalendar = new Time();
		mCalendar.switchTimezone("UTC");
		mCurrentTime = System.currentTimeMillis();

		mDialWidth = mDial.getIntrinsicWidth();
		mDialHeight = mDial.getIntrinsicHeight();
	}

	@Override protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		onTimeChanged();		
		mTimerHandler.start();
	}

	@Override protected void onDetachedFromWindow()
	{	
		super.onDetachedFromWindow();

		try
		{
			mTimerHandler.join();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			mTimerHandler.stop();
		}
	}

	/**
	 * Sets the time of the clock face
	 * @param time The time in miliseconds starting at 1970
	 */
	public void setTime(long time)
	{		
		mCurrentTime = time;

		onTimeChanged();
		invalidate();
	}

	/**
	 * Sets whether the clock is frozen or not
	 * @param clockFrozen True if the clock should be frozen
	 */
	public void setClockFrozen(boolean clockFrozen)
	{
		mClockFrozen = clockFrozen;
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float hScale = 1.0f;
		float vScale = 1.0f;

		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth)
		{
			hScale = (float) widthSize / (float) mDialWidth;
		}

		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight)
		{
			vScale = (float) heightSize / (float) mDialHeight;
		}

		float scale = Math.min(hScale, vScale);

		setMeasuredDimension(resolveSize((int) (mDialWidth * scale), widthMeasureSpec), resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
	}

	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		boolean changed = mChanged;
		if (changed)
		{
			mChanged = false;
		}

		int availableWidth = getRight() - getLeft();
		int availableHeight = getBottom() - getTop();

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = mDial;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		if (availableWidth < w || availableHeight < h)
		{
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w, (float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}

		if (changed)
		{
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}

		dial.draw(canvas);

		canvas.save();
		canvas.rotate(mHour / 12.0f * 360.0f, x, y);

		final Drawable hourHand = mHourHand;
		if (changed)
		{
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}

		hourHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

		final Drawable minuteHand = mMinuteHand;
		if (changed)
		{
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}

		minuteHand.draw(canvas);
		canvas.restore();

		if (scaled)
		{
			canvas.restore();
		}
	}

	private void onTimeChanged()
	{			
		if (mClockFrozen) return;

		mCalendar.set(mCurrentTime);		

		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;

		mChanged = true;
	}

	class TimerTick extends Thread
	{
		@Override public void run()
		{					
			mCurrentTime += 1000;
			onTimeChanged();
			invalidate();

			postDelayed(this, 1000);
		}
	}
}
