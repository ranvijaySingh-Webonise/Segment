package com.segment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Segment extends RadioGroup implements OnCheckedChangeListener{

	private static final int SEGMENT_BORDER_WIDTH = 1;
	private final String TAG = "Segment";
	private final String BUTTON_TAG = "radioButton";
	private String count = "";
	private String text = "";
	private String textSize = "";
	private String textColor = "";
	private String segmentColor = "";
	private View view;
	private RadioGroup radioGroup;
	private static OnClickSegmentButton mOnClickSegmentButton;


	public Segment(Context context) {
		super(context);
	}

	public Segment(Context context, AttributeSet attrs) {
		super(context, attrs);		
		initializeView(context,attrs);
	}

	public Segment(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);		
		initializeView(context,attrs);
	}

	/**
	 * Function to initialize the components and get attributes from xml.
	 * @param context Context
	 * @param attrs AttributeSet
	 */
	public void initializeView(Context context,AttributeSet attrs){
		view = LayoutInflater.from(context).inflate(R.layout.main_layout, this);
		radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Segment,0, 0);
		try {
			count = typedArray.getString(R.styleable.Segment_count);
			text = typedArray.getString(R.styleable.Segment_text);
			textColor = typedArray.getString(R.styleable.Segment_textColor);
			segmentColor = typedArray.getString(R.styleable.Segment_segmentColor);
			textSize = typedArray.getString(R.styleable.Segment_textSize);
		}finally {
			typedArray.recycle();
		}
		setButtonCount(radioGroup,count);
		setButtonText(radioGroup,text,textSize);
		setSegmentThemeColor(segmentColor);
		setTextThemeColor(radioGroup,textColor,segmentColor);
		radioGroup.setOnCheckedChangeListener(this);
	}


	/**
	 * Function to set the button count based on the attributes given
	 * @param radioGroup 
	 * @param count String
	 */
	private void setButtonCount(RadioGroup radioGroup, String count) {
		int buttonCount = 2;
		try {
			buttonCount = Integer.parseInt(count);
			for (int i = 0; i < buttonCount - 2  ; i++) {
				RadioButton button = new RadioButton(getContext());
				button.setId(i);
				button.setTextSize(16.0f);
				XmlResourceParser xrp = getResources().getXml(R.drawable.rbtn_textcolor_selector);  
				try {  
				    ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);  
				    button.setTextColor(csl);
				} catch (Exception e) {  } 
				button.setTag(BUTTON_TAG);
				button.setGravity(Gravity.CENTER);
				button.setSingleLine(true);
				button.setPadding(20,5,20,5);
				if (i==(buttonCount-3)) {
					button.setBackgroundResource(R.drawable.button_filter_background_right);
				}else{
					button.setBackgroundResource(R.drawable.button_filter_background);
				}
				button.setButtonDrawable(android.R.color.transparent);
				LayoutParams layoutParams = new LayoutParams(0,LayoutParams.MATCH_PARENT,1.0f);
				button.setLayoutParams(layoutParams);
				radioGroup.addView(button);	
			}
			radioGroup.invalidate();
		} catch (Exception e) {
			Log.e(TAG, "setButtonCount : catch block : "+e);
			e.printStackTrace();
		}
	}

	/**
	 * Function to set the component values based on the attribute values.
	 * @param radioGroup 
	 * @param text String
	 * @param textSize 
	 */
	private void setButtonText(RadioGroup radioGroup, String text, String textSize) {
		try {
			String []texts = text.split(",");
			int componentCount = radioGroup.getChildCount();
			textSize = textSize.substring(0, textSize.length()-2);
			int textSizeValue = Integer.parseInt(textSize);
			if (componentCount > 0) {
				for (int i = 0; i < componentCount; i++) {
					RadioButton radioButton = (RadioButton)radioGroup.getChildAt(i);
					if (!TextUtils.isEmpty(texts[i])) {
						radioButton.setText(texts[i]);	
					}else {
						radioButton.setText(i+1);
					}					
					radioButton.setTextSize(dpToPx(textSizeValue));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "setButtonCount : catch block : "+e);
			e.printStackTrace();
		}
	}

	/**
	 * Function to convert dp to px.
	 * @param dp int
	 * @return px 
	 */
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	/**
	 * Function to set segment color
	 * @param segmentColor color reference id
	 */
	private void setSegmentThemeColor(String segmentColor) {
		GradientDrawable drawable = (GradientDrawable)radioGroup.getBackground();
		drawable.setStroke(2, Color.parseColor(segmentColor));
	}	
	
	/**
	 * Function to set segment color
	 * @param segmentColor color reference id
	 */
	private void setTextThemeColor(RadioGroup radioGroup,String textColor,String segmentColor) {
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			RadioButton button = (RadioButton)radioGroup.getChildAt(i);
			StateListDrawable drawable = (StateListDrawable)button.getBackground();
			DrawableContainerState dcs = (DrawableContainerState)drawable.getConstantState();
			Drawable[] drawableItems = dcs.getChildren();
			GradientDrawable gradientDrawableChecked = (GradientDrawable)drawableItems[0];
			GradientDrawable gradientDrawableUnChecked = (GradientDrawable)drawableItems[1];
			gradientDrawableUnChecked.setStroke(SEGMENT_BORDER_WIDTH, Color.parseColor(segmentColor));
			if (button.isChecked()) {
				button.setTextColor(Color.parseColor(textColor));
				gradientDrawableChecked.setColor(Color.parseColor(segmentColor));
				gradientDrawableUnChecked.setColor(Color.parseColor(textColor));
			}else{
				button.setTextColor(Color.parseColor(segmentColor));
				gradientDrawableChecked.setColor(Color.parseColor(textColor));
				gradientDrawableUnChecked.setColor(Color.parseColor(textColor));
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		setTextThemeColor(radioGroup,textColor, segmentColor);
		int radioButtonID = radioGroup.getCheckedRadioButtonId();
		View radioButton = radioGroup.findViewById(radioButtonID);
		int index = radioGroup.indexOfChild(radioButton);
		mOnClickSegmentButton.onButtonSelected(radioButton, index);
	}

	public String getCount(){
		return count;
	}

	public void setCount(String count){
		this.count = count;
	}

	public String getSegmentColor(){
		return segmentColor;
	}

	public void setSegmentColor(String segmentColor){
		this.segmentColor = segmentColor;
	}
	
	public String getTextColor(){
		return textColor;
	}

	public void setTextColor(String textColor){
		this.textColor = textColor;
	}
	
	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}
	
	
	public String getTextSize(){
		return textSize;
	}

	public void setTextSize(String textSize){
		this.textSize = textSize;
	}

	public interface OnClickSegmentButton{
		public void onButtonSelected(View view,int position);
	}
	
	public void setOnClickSegmentButton(OnClickSegmentButton onClickSegmentButton){
		mOnClickSegmentButton = onClickSegmentButton;
	}
}
