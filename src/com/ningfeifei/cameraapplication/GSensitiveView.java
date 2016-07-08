/**  
 * <p><b>Description:</b>  </p>  
 * <p><b>Title:</b> dsd.java </p>
 * <p><b>Package</b> com.example.cameraexample</p> 
 * @author NingFeifei
 * <p><b>date</b> 2016-7-4 下午5:23:26</p> 
 * @version v1.0.0
 */
package com.ningfeifei.cameraapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GSensitiveView extends ImageView {  
    private Context mContext;
    private Bitmap image;  
    private double rotation;  
    private Paint paint;  
 
    public GSensitiveView(Context context) {  
        super(context);
        init(context);  
    }

    public GSensitiveView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	init(context);  
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GSensitiveView);
        
        BitmapDrawable d =  (BitmapDrawable) a.getDrawable(R.styleable.GSensitiveView_src_pic);
        
        if (d != null) {
        	image = d.getBitmap();
        	//setImageDrawable(d);
        }
        a.recycle();
    }
    
    public GSensitiveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context); 
        
    }
    
    private void init(Context context) {
		this.mContext = context;
        paint = new Paint();
	} 
    
    public void setImage(int id){
    	BitmapDrawable drawble = (BitmapDrawable) mContext.getResources().getDrawable(id);
    	image = drawble.getBitmap();  
	}
 
    @SuppressLint("DrawAllocation") @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        if(image==null){
        	return;
        }
        double w = image.getWidth();  
        double h = image.getHeight();  
 
        Rect rect = new Rect();  
        getDrawingRect(rect);  
 
        int degrees = (int) (180 * rotation / Math.PI);  
        canvas.rotate(degrees, rect.width() / 2, rect.height() / 2);  
        canvas.drawBitmap(image, //  
                (float) ((rect.width() - w) / 2),//    
                (float) ((rect.height() - h) / 2),//    
                paint);  
    }  
 
    public void setRotation(double rad) {  
        rotation = rad;  
        invalidate();  
    }
 
}