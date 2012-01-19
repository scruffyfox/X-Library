package x.type;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * @brief Allows the use of drawing text as a drawable instance to use as view images
 */
public class TextDrawable extends Drawable
{
	private String text;
	private Paint paint;

	/**
	 * Default Constructor
	 * @param text The text to be displayed in the drawable
	 */
	public TextDrawable(String text)
	{
		this.text = text;

		this.paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(22f);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.LEFT);
	}
	
	/**
	 * Default Constructor
	 * @param text The text to be displayed in the drawable
	 * @param fontSize The desired font size
	 * @param fontColor The color for the font
	 */
	public TextDrawable(String text, float fontSize, int fontColor)
	{
		this.text = text;

		this.paint = new Paint();
		paint.setColor(fontColor);
		paint.setTextSize(fontSize);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);		
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.LEFT);
	}
	
	/**
	 * Default Constructor
	 * @param text The text to be displayed in the drawable
	 * @param p The paint to use when drawing the text
	 */
	public TextDrawable(String text, Paint p)
	{
		this.text = text;
		this.paint = p;
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.LEFT);
	}

	/**
	 * Gets the current set text size
	 * @return The text size as a float
	 */
	public float getTextSize()
	{
		return this.paint.getTextSize();
	}
	
	/**
	 * Sets the text size
	 * @param size The size to draw the text
	 */
	public void setTextSize(float size)
	{
		this.paint.setTextSize(size);
	}		
	
	/**
	 * Gets the text being drawn
	 * @return The set text
	 */
	public String getText()
	{
		return text;
	}
	
	/**
	 * Sets the text to be drawn
	 * @param text The text to be set
	 */
	public void setText(String text)
	{
		this.text = text;
	}
	
	/**
	 * Sets the text color 
	 * @param color The color to set the text
	 */
	public void setTextColor(int color)
	{
		this.paint.setColor(color);
	}
	
	/**
	 * Gets the text color
	 * @return The color of the drawn text
	 */
	public int getTextColor()
	{
		return paint.getColor();
	}
	
	/**
	 * Called by the system when the drawable has been requested to be drawn
	 * @param canvas The canvas which is being used to draw onto
	 */
	@Override public void draw(Canvas canvas)
	{
		canvas.drawText(text, 0, 0, paint);
	}

	/**
	 * Sets the alpha of the drawable
	 * @param alpha The amount of alpha to set. 0-255
	 */
	@Override public void setAlpha(int alpha)
	{
		paint.setAlpha(alpha);
	}

	/**
	 * Sets the color filter of the drawable
	 * @param cf The color filter to set
	 */
	@Override public void setColorFilter(ColorFilter cf)
	{
		paint.setColorFilter(cf);
	}

	/**
	 * Gets the opacity of the drawable
	 * @return The opacity of the drawable
	 */
	@Override public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}
}