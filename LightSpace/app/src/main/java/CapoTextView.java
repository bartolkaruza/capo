import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Florian on 9/20/2014.
 */
public class CapoTextView extends TextView {

    public static final String FONT = "fonts/cafeandbrewery.ttf";
    public CapoTextView(Context context) {
        super(context);
        init(context);
    }

    public CapoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CapoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context ctx)
    {
        Typeface myTypeface = Typeface.createFromAsset(getResources().getAssets(), FONT);
        this.setTypeface(myTypeface);
    }
}
