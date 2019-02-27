/**
 * @author Ben Pitts
 * Timeline Calendar project from CS495 (Android app development)
 * Won't do much in emulator, as it needs calendar data and multitouch.
 * email me if you do anything cool with this idea: methodermis@gmail.com
 */
package com.yhsoft.photoremember.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.event.Action_UpEvent;
import com.yhsoft.photoremember.fragment.MyTimeTraceFragment;
import com.yhsoft.photoremember.util.BusProvider;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.Preference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TLView extends View {
    private static final String LogTag = "drgn";
    private static final long SEC_MILLIS = 1000;
    private static final long MIN_MILLIS = 60 * SEC_MILLIS;
    private static final long HOUR_MILLIS = 60 * MIN_MILLIS;
    private static final long DAY_MILLIS = HOUR_MILLIS * 24;

    private Paint blackline, redline, magentaline, redtext20, blacktext30, blacktext20, blacktext15, backgroundgradient, blueline, whitetext30, graytext30;
    private CalStuff calstuff = null;

    private static int SCALING_MODE = 2; // 0 is expansion (1 is reduction) default is 2
    float static_finger1x, static_finger2x; //처음 찍은 finger 들의 좌표
    long delta1xinmillis;


    /**
     * width of view in pixels
     */
    int width;
    /**
     * left and right limit of the ruler in view, in milliseconds
     */
    long left, right, pre_left, pre_right;
    /**
     * how many fingers are being used? 0, 1, 2
     */
    int fingers;
    /**
     * holds pointer id of #1/#2 fingers
     */
    int finger1id, finger2id;
    /**
     * holds x/y in pixels of #1/#2 fingers from last frame
     */
    float finger1x, finger1y, finger2x, finger2y;

    //private static final int rulery = 160;
    private int rulery = 160;
    private static final int yearStrt = 10;

    /**
     * width of the view in milliseconds, cached value of (right-left)
     */
    float span, span2;
    /**
     * how many pixels does each millisecond correspond to?
     */
    float pixels_per_milli;
    /**
     * length in pixels of time units, at current zoom scale
     */
    //중요 변수
    float sec_pixels, min_pixels, hour_pixels, day_pixels;

    /**
     * reusable calendar class object for rounding time to nearest applicable unit in onDraw
     */
    Calendar acalendar;

    private long start, end;
    private Context mContext;
    PhoTrace app;

    int startx;

    //	public static String strDate ;
    public TLView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    public TLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TLView(Context context) {
        super(context);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        // width/height are set here

        backgroundgradient = new Paint();
        backgroundgradient.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.WHITE, 0xFFAAAAAA, Shader.TileMode.CLAMP));
    }

    private void init() {

        DisplayMetrics dm = getResources().getDisplayMetrics();

        rulery = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);

        blackline = new Paint();
        blackline.setColor(Color.WHITE);
        blackline.setStrokeWidth(1f);
        blackline.setAntiAlias(true);
        blackline.setStyle(Style.STROKE);

        int dpSize = 20;
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);

        //timeslider top line (pink)
        redline = new Paint();
        redline.setColor(Color.argb(255, 251, 216, 220));
        redline.setStrokeWidth(strokeWidth);
        redline.setAntiAlias(true);
        redline.setStyle(Style.STROKE);

        //timeslider bottom line (white pink not magenta)
        magentaline = new Paint();
        magentaline.setColor(Color.argb(255, 243, 137, 141));
        magentaline.setStrokeWidth(strokeWidth);
        magentaline.setAntiAlias(true);
        magentaline.setStyle(Style.STROKE);

        blueline = new Paint();
        blueline.setColor(Color.argb(255, 118, 118, 118));
        blueline.setStrokeWidth(5f);
        blueline.setAntiAlias(true);
        blueline.setStyle(Style.FILL);

        float fontsize = getResources().getDimensionPixelSize(R.dimen.fontsize);

        whitetext30 = new Paint();
        whitetext30.setColor(Color.WHITE);
        whitetext30.setStrokeWidth(3f);
        whitetext30.setAntiAlias(true);
        whitetext30.setStyle(Style.FILL);
        whitetext30.setTextSize(fontsize);

        graytext30 = new Paint();
        graytext30.setColor(Color.argb(255, 118, 118, 118));
        graytext30.setStrokeWidth(5f);
        graytext30.setAntiAlias(true);
        graytext30.setStyle(Style.FILL);
        graytext30.setTextSize(fontsize);

        acalendar = new GregorianCalendar();

        if (Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM) == Long.MIN_VALUE) {
            left = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_BOTTOM);
            right = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_TOP);
        } else {
            left = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM);
            right = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP);
        }

        start = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_BOTTOM_STATIC);
        end = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_TOP_STATIC);

        span = right - left; //현 줌에맞게 밀리세컨드로 계산(float)
        span2 = right - left; // 자꾸 바뀌는 span값 대신 안바뀌는 span값 저장 (초기 span)

        mContext = PhoTrace.getContext();
        app = (PhoTrace) mContext.getApplicationContext();
    }

    private String DayShort(int daynumber, boolean shortnotlong) {
        if (shortnotlong) {
            switch (daynumber % 7) {
                case 0:
                    return " ";
                case 1:
                    return " ";
                case 2:
                    return " ";
                case 3:
                    return " ";
                case 4:
                    return " ";
                case 5:
                    return " ";
                case 6:
                    return " ";
                default:
                    return "-";
            }
        } else {
            switch (daynumber % 7) {
                case 0:
                    return " ";
                case 1:
                    return " ";
                case 2:
                    return " ";
                case 3:
                    return " ";
                case 4:
                    return " ";
                case 5:
                    return " ";
                case 6:
                    return " ";
                default:
                    return "derpday";
            }
        }
    }


    public String getDateForTimeCursor(int timePoint) {
        String strDate;
        width = getWidth(); // width of view in pixels
        //calculate the righttop point in milliseconds at the current zoom
        long PointinMillis = left + (long) (timePoint * span / width);
        Date rightCursorDate = new Date(PointinMillis);
        DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
        Log.d(LogTag, "rightCursorDate: " + df.format(rightCursorDate));
        strDate = df.format(rightCursorDate);
        return strDate;
    }

    public String getDateforTimeSlider(long miliSeconds) {
        Date date = new Date(miliSeconds);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Log.d(LogTag, "rightCursorDate: " + df.format(date));
        String strDate = df.format(date);
        return strDate;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (MyTimeTraceFragment.timeSliderFlag == true) {
            left = app.leftInMillis;
            right = app.rightInMillis;
            span = right - left; //현 줌에맞게 밀리세컨드로 계산(float)
            if (span == 0) {
                init();
            } else if (span <= DateUtils.DAY_IN_MILLIS) {
                span = DateUtils.DAY_IN_MILLIS;
            }
            MyTimeTraceFragment.timeSliderFlag = false;
        }

        long next, now;

        width = getWidth(); // width of view in pixels
        // calculate span/width
        pixels_per_milli = (float) width / (float) span;
        sec_pixels = (float) SEC_MILLIS * pixels_per_milli;
        min_pixels = (float) MIN_MILLIS * pixels_per_milli;
        hour_pixels = (float) HOUR_MILLIS * pixels_per_milli;
        day_pixels = (float) DAY_MILLIS * pixels_per_milli;

        if (left < start && start < right) { //사진중 가장 오래된 날짜 표시
            float dist = (start - left) / span;
            startx = (int) (dist * width);
        }
        //canvas.drawText(DateUtil.getDateString(left, DateUtil.RANGE_YEAR), startx + 100, rulery - 120, graytext30);

        if (left < end && start < end) {  //사진중 가장 최근 날짜 표시
            float dist = (end - left) / span;
            int endx = (int) (dist * width);
            //canvas.drawLine(endx, 0, endx, getHeight(), blueline);
        }


        DisplayMetrics dm = getResources().getDisplayMetrics();
        int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, dm);
        int bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, dm);
        // draw ruler
        canvas.drawLine(0, rulery - top, width, rulery - top, magentaline); //timeline top
        canvas.drawLine(0, rulery - bottom, width, rulery - bottom, redline); //timeline bottom


        // round calendar down to leftmost hour
        acalendar.setTimeInMillis(left);
        // floor the calendar to various time units to find where (in ms) they start
        acalendar.set(Calendar.MILLISECOND, 0); // second start
        acalendar.set(Calendar.SECOND, 0); // minute start
        acalendar.set(Calendar.MINUTE, 0); // hour start


        // draw months
        // round calendar down to leftmost month
        acalendar.set(Calendar.HOUR_OF_DAY, 0); // day start
        acalendar.set(Calendar.DAY_OF_MONTH, 1); // month start
        next = acalendar.getTimeInMillis(); // set to start of leftmost month

        do {
            // draw each month
            int monthnumber = acalendar.get(Calendar.MONTH);
            int daysthismonth = acalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int daynumber = acalendar.get(Calendar.DAY_OF_WEEK);

            long daymsx = next; // first day starts at start of month
            int x = (int) ((daymsx - left) / span * width); // convert to pixels 픽셀단위로 변환

            if(span >= DateUtils.YEAR_IN_MILLIS) {
                if (monthnumber == 0) {
                    canvas.drawLine(x, rulery - 150, x, rulery + 30, blackline);
                    int yearposition = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, dm);
                    String year = Integer.toString(acalendar.get(Calendar.YEAR));
                    canvas.drawText(year, x + 8, rulery - yearposition, graytext30);
                }
            }else{
                canvas.drawLine(x, rulery - 150, x, rulery + 30, blackline);
            }

            if (span >= DateUtils.YEAR_IN_MILLIS) {
                String year = DateUtil.getDateString(left, DateUtil.RANGE_YEAR); //on the timeline year text part
                int yearposition = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, dm);
                float fontsize = getResources().getDimensionPixelSize(R.dimen.fontsize);
                int dpSize = 20;
                float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);
                Paint mPaint = new Paint();
                mPaint.setColor(Color.WHITE);
                mPaint.setStyle(Paint.Style.FILL);
                RectF mRectF = new RectF(0 , 0 , fontsize * 3, strokeWidth);
                canvas.drawRect(mRectF, mPaint);
                //canvas.drawText(year, x + 8, rulery - yearposition, graytext30);
                canvas.drawText(year, yearStrt + 8, rulery - yearposition, graytext30);
            } else {
                String year = DateUtil.getDateString(left, DateUtil.RANGE_MONTH);
                canvas.drawLine(x, rulery - 150, x, rulery + 30, blackline);
                //canvas.drawLine(x, rulery - 150, x, rulery + 30, blackline);
                int yearposition = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, dm);
                float fontsize = getResources().getDimensionPixelSize(R.dimen.fontsize);
                int dpSize = 20;
                float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);
                Paint mPaint = new Paint();
                mPaint.setColor(Color.WHITE);
                mPaint.setStyle(Paint.Style.FILL);
                RectF mRectF = new RectF(0 , 0 , fontsize * 4, strokeWidth);
                canvas.drawRect(mRectF, mPaint);

                canvas.drawText(year, yearStrt + 8, rulery - yearposition, graytext30);
            }

            // draw month names
            if (day_pixels < 3) ;
            else if (day_pixels < 5) {
                // sideways month name
                int position = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, dm);
                canvas.save();
                canvas.drawText(String.valueOf(monthnumber + 1), x + 10, rulery - position, whitetext30);
                canvas.restore();
            } else if (day_pixels < 7) {
                int position = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, dm);
                canvas.drawText(String.valueOf(monthnumber + 1), x + 10, rulery - position, whitetext30);
            } else {
                int position = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, dm);
                canvas.drawText(String.valueOf(monthnumber + 1), x + 10, rulery - position, whitetext30);
            }

            // draw days, weeks
            for (int date = 1; date <= daysthismonth; date++, daynumber++, daymsx += DAY_MILLIS) {
                x = (int) ((daymsx - left) / span * width);

                if (daynumber == 7) daynumber = 0;

                if (day_pixels < 3) ;
                if (day_pixels < 10) {
                } else if (day_pixels < 20) {
                } else if (day_pixels > 60) {
                    // big days
                    if (daynumber == 1)
                        canvas.drawLine(x, rulery - 150, x, rulery + 30, blackline);
                    else
                        canvas.drawLine(x, rulery - 150, x, rulery + 30, blackline);
                    //drawstar(x, rulery - 100, 20, 20, canvas, blackline);
                    int position = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
                    canvas.drawText(Integer.toString(date), x + 10, rulery + position, graytext30);
                    canvas.drawText(DayShort(daynumber, false), x + 10, rulery + position, graytext30);
                }
            }
            acalendar.add(Calendar.MONTH, 1);
            next = acalendar.getTimeInMillis();
        } while (next < right);
        // done drawing months

        // draw some events maybe
        if (calstuff != null) {
            for (int i = 0; i < calstuff.ourEvents.size(); i++) {
                CalStuff.Evnt e = calstuff.ourEvents.get(i);

                if ((e.dtend < left) || (e.dtstart > right)) continue;

                int x = (int) ((e.dtstart - left) / span * width);
                int x2 = (int) ((e.dtend - left) / span * width);
                int y = rulery + 25;
                float h = 40;
                RectF r = new RectF(x, y, x2, y + h); // left top right bottom

                CalStuff.Clndr cal = calstuff.CalendarsMap.get(e.calendar_id);
                canvas.drawRect(r, cal.paint);
                canvas.drawRect(r, blackline);

                // draw text labels
                if ((e.title == null) || (day_pixels < 35)) ;
                else if (day_pixels < 60) {
                    Paint p = blacktext15;
                    String t = e.title;
                    canvas.save();
                    canvas.rotate(90, x, y);
                    canvas.drawText(e.title, x + h, y, p);
                    canvas.restore();

                    if (e.desc != null) if (e.desc.length() > 0)
                        canvas.drawText("...", x + h / 2, y + 3 * h / 2, blacktext15);
                } else {
                    String t = e.title;

                    canvas.save();
                    canvas.clipRect(r);
                    canvas.drawText(t, x, y + h / 2, blacktext20);
                    canvas.restore();

                    Paint p = blacktext20;
                    canvas.save();
                    canvas.rotate(90, x, y);
                    canvas.drawText(e.title, x + h, y, p);
                    canvas.restore();

                    if (e.desc != null) if (e.desc.length() > 0)
                        canvas.drawText(e.desc, x + h / 2, y + 3 * h / 2, blacktext15);
                }
            }
        }
    }

    private void drawhourtext(Canvas canvas, float x, float y, int h24, int h12) {
        if (h24 < 12)
            canvas.drawText(Integer.toString(h12) + "a", x, y, redtext20);
        else
            canvas.drawText(Integer.toString(h12) + "p", x, y, redtext20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionevent) {

        VelocityTracker mVtracker = null;
        if (mVtracker == null) {
            mVtracker = mVtracker.obtain();
        }
        mVtracker.addMovement(motionevent);

        switch (motionevent.getActionMasked()) {
            // First finger down, start panning
            case MotionEvent.ACTION_DOWN:

                fingers = 1; // panning mode

                // save id and coords
                finger1id = motionevent.getPointerId(motionevent.getActionIndex());
                finger1x = motionevent.getX();
                finger1y = motionevent.getY();

                static_finger1x = finger1x;

                pre_left = left;
                pre_right = right;

                SCALING_MODE = 2;

                Log.d(LogTag, "down " + finger1x);
                invalidate(); // redraw
                return true;

            // Second finger down, start scaling
            case MotionEvent.ACTION_POINTER_DOWN:  //두번째 손가락 터치시

                if (fingers == 2) // if already tracking 2 fingers
                    break; // ignore 3rd finger
                // else fingers == 1
                fingers = 2; // scaling mode

                // save id and coords
                finger2id = motionevent.getPointerId(motionevent.getActionIndex());
                finger2x = motionevent.getX(finger2id);
                finger2y = motionevent.getY(finger2id);
                static_finger2x = finger2x;

                Log.d(LogTag, "2down " + finger2x);
                invalidate(); // redraw
                return true;

            case MotionEvent.ACTION_MOVE:
                mVtracker.computeCurrentVelocity(10);
                float velocity = mVtracker.getXVelocity();
                //Log.d(LogTag, "velocity: " + velocity);
                if (fingers == 0) // if not tracking fingers as down
                    return false; // ignore move events

                float new1x,
                        new1y,
                        new2x,
                        new2y; // Hold new positions of two fingers

                // get finger 1 position
                int pointerindex = motionevent.findPointerIndex(finger1id);
                if (pointerindex == -1) // no change
                {
                    new1x = finger1x; // use values from previous frame
                    new1y = finger1y;
                } else
                // changed
                {
                    // get new values
                    new1x = motionevent.getX(pointerindex);
                    new1y = motionevent.getY(pointerindex);
                }

                // get finger 2 position
                pointerindex = motionevent.findPointerIndex(finger2id);
                if (pointerindex == -1) {
                    new2x = finger2x;
                    new2y = finger2y;
                } else {
                    new2x = motionevent.getX(pointerindex);
                    new2y = motionevent.getY(pointerindex);
                }

                // panning
                if (fingers == 1) {
                    // how far to scroll in milliseconds to match the scroll input in pixels
                    // (deltax)*span/width = delta-x in milliseconds
                    delta1xinmillis = (long) ((finger1x - new1x) * span / width) + (long) Math.abs(velocity);
                    Log.d(LogTag, "velocity:" + velocity);
                    left = left + delta1xinmillis;
                    right = right + delta1xinmillis;
                    Log.d(LogTag, "left:" + left);
                    Log.d(LogTag, "right:" + right);


                }
                // scaling
                else if (fingers == 2) {
                    // don't scale if fingers too close, or past each other
                    if (Math.abs(new1x - new2x) < 10) return true;
                    if (finger1x > finger2x) if (new1x < new2x) return true;
                    if (finger1x < finger2x) if (new1x > new2x) return true;

                    // find ruler time in ms under each finger at start of move
                    // y = mx+b, b = left, span = right - left [ms]
                    double m = (double) span / (double) width; // m = span/width
                    double y1 = m * finger1x + left; // ms at finger1
                    double y2 = m * finger2x + left; // ms at finger2
                    // y values are set to the millisecond time shown at the old finger1x and
                    // finger2x, using old left and right span
                    // construct a new line equation through points (new1x,y1),(new2x,y2)
                    // f(x) = y1 + (x - new1x) * (y2 - y1) / (new2x - new1x)
                    span2 = pre_right - pre_left; // 이전 span
                    left = (long) (y1 + (0 - new1x) * (y2 - y1) / (new2x - new1x));
                    right = (long) (y1 + (width - new1x) * (y2 - y1) / (new2x - new1x));
                    span = right - left; // span of milliseconds in view
                    Log.d(LogTag, "span :" + span);
                }
                // save
                finger1x = new1x;
                finger1y = new1y;
                finger2x = new2x;
                finger2y = new2y;

                invalidate(); // redraw with new left,right
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                int id = motionevent.getPointerId(motionevent.getActionIndex());

                if (Math.abs(static_finger1x - static_finger2x) > Math.abs(finger1x - finger2x)) {
                    SCALING_MODE = 1; // 축소
                } else {
                    SCALING_MODE = 0; // 확대
                }

                if (id == finger1id) {
                    // 1st finger went up, make 2nd finger new first finger and go back to panning
                    finger1id = finger2id;
                    finger1x = finger2x; // copy coords so view won't jump to other finger
                    finger1y = finger2y;
                    fingers = 1; // panning
                } else if (id == finger2id) {
                    // 2nd finger went up, just go back to panning
                    fingers = 1; // panning
                    if (SCALING_MODE == 1) {
                        if (start >= left && end <= right) {
                            span = span2;
                        }
                    }

                } else {
                    return false; // ignore 3rd finger ups
                }
                invalidate(); // redraw
                return true;

            case MotionEvent.ACTION_CANCEL:
                Log.d(LogTag, "cancel!"); // herpderp
                return true;

            case MotionEvent.ACTION_UP:
                if (mVtracker != null) {
                    mVtracker.recycle();
                    mVtracker = null;
                }
                // last pointer up, no more motionevents
                Log.d(LogTag, "mode :" + SCALING_MODE);
                fingers = 0;
                if (SCALING_MODE == 2 || SCALING_MODE == 0) {
                    if (start >= left) { // Start time bound
                        left = start;
                        right = left + (long) span;
                    }
                    if (end <= right) { // End time bound
                        right = end;
                        left = right - (long) span;
                    }
                    if (SCALING_MODE == 0) {
                        if (span <= DateUtils.DAY_IN_MILLIS) {
                            init();
                        }
                    }
                } else {
                    if (SCALING_MODE == 1) {
                        if (start >= left && end <= right) {
                            right = end;
                            left = start;
                            span = span2;
                        } else if (end <= right) {
                            right = end;
                            left = right - (long) span;
                            //조건문 필요
                            long x = right - left;
                            if (x < span) {
                                init();
                            }
                        } else if (start >= left) {
                            left = start;
                            right = left + (long) span;
                            long x = right - left;
                            if (x < span) {
                                init();
                            }
                        }
                    }

                }

                BusProvider.getBus().post(new Action_UpEvent(left, right));
                /*
                 *for bucket start mode following timeslider range (year, month, day)
                 */
                if (span >= DateUtils.YEAR_IN_MILLIS) {
                    Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_YEAR);
                } else if (span >= (DateUtils.WEEK_IN_MILLIS * 4) && span < DateUtils.YEAR_IN_MILLIS) {
                    Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_MONTH);
                } else {
                    Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_DAY);
                }
                 /*
                 *for bucket start mode following timeslider range (year, month, day)
                 */

                invalidate(); // redraw

                return true;
        }
        return super.onTouchEvent(motionevent);
    }

    private void drawstar(int x, int y, int halfwidth, int halfheight, Canvas canvas, Paint paint) {
        int midx = x;
        int midy = y - halfheight;
        Path path = new Path();
        path.moveTo(x, y);
        path.quadTo(midx, midy, x - halfwidth, y - halfheight);
        path.quadTo(midx, midy, x, y - halfheight - halfheight);
        path.quadTo(midx, midy, x + halfwidth, y - halfheight);
        path.quadTo(midx, midy, x, y);
        canvas.drawPath(path, paint);
    }
}


