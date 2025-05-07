package unionware.base.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import unionware.base.R;

public class TriangleButton extends AppCompatButton {
    private Path trianglePath;
    private Paint trianglePaint;

    private int triangleSeat = 0x01;

    public TriangleButton(Context context) {
        super(context);
        init(null);
    }

    public TriangleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TriangleButton);
        init(array);
    }

    public TriangleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TriangleButton);
        init(array);
    }

    private void init(TypedArray array) {
        trianglePath = new Path();
        trianglePaint = new Paint();
        if (array != null && array.hasValue(R.styleable.TriangleButton_triangleColor)) {
            trianglePaint.setColor(array.getColor(R.styleable.TriangleButton_triangleColor, Color.RED));
        } else {
            trianglePaint.setColor(Color.RED);
        }
        trianglePaint.setStyle(Paint.Style.FILL);

        if (array != null && array.hasValue(R.styleable.TriangleButton_triangleSeat)) {
            triangleSeat = array.getInt(R.styleable.TriangleButton_triangleSeat, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        /*trianglePath.moveTo(0, 0);
        trianglePath.lineTo(width, 0);
        trianglePath.lineTo(width, height);
        trianglePath.close();*/
        drawTriangleSeat(width, height);
        canvas.drawPath(trianglePath, trianglePaint);

        super.onDraw(canvas);
    }

    private void drawTriangleSeat(int width, int height) {
        switch (triangleSeat) {
            case 0x01://topRight
                trianglePath.moveTo(0, 0);
                trianglePath.lineTo(width, 0);
                trianglePath.lineTo(width, height);
                trianglePath.close();
                break;
            case 0x02://topLeft
                trianglePath.moveTo(0, 0);
                trianglePath.lineTo(width, 0);
                trianglePath.lineTo(0, height);
                trianglePath.close();
                break;
            case 0x03://endRight
                trianglePath.moveTo(0, height);
                trianglePath.lineTo(width, height);
                trianglePath.lineTo(width, 0);
                trianglePath.close();
                break;
            case 0x04://endLeft
                trianglePath.moveTo(0, 0);
                trianglePath.lineTo(0, height);
                trianglePath.lineTo(width, height);
                trianglePath.close();
                break;
            default:
                break;
        }
    }
}