package wasif.whatevervalue.com.instagramclone.dagger2;

import android.app.Activity;
import android.widget.TextView;


import javax.inject.Inject;

import wasif.whatevervalue.com.instagramclone.dagger2.model.SomeData;

/**
 * Created by erikb on 1/21/16.
 *
 * A dumb class that consumes {@link DataSource} in order to
 * populate some critical view in our application
 */
public class DataPresenter {

    private DataSource mData;

    public DataPresenter(DataSource ds) {
        this.mData = ds;
    }

    public TextView present(Activity context) {
        SomeData sd = mData.getData().get(0);
        TextView tv = new TextView(context);
        tv.setText(String.format("%s %s", sd.mDataName, sd.mDataValue));
        return tv;
    }
}
