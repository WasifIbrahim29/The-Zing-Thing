package wasif.whatevervalue.com.instagramclone.dagger2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;
import javax.inject.Provider;

import wasif.whatevervalue.com.instagramclone.R;

public class DaggerActivity extends AppCompatActivity {

    @Inject
    Provider<BoomWidget> mBoomProvider;

    @Inject DataPresenter mPresenter;

    private TextView mBoomGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger);

        ExampleApplication.component(getApplicationContext())
                .inject(this);

        mBoomGroup = (TextView) findViewById(R.id.boom_txt);
        ViewGroup dataContainer = (ViewGroup)findViewById(R.id.data_container);
        View button = findViewById(R.id.boom_btn);
        button.setOnClickListener(mOnClick);
        dataContainer.addView(mPresenter.present(this));
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            BoomWidget bw = mBoomProvider.get();
            mBoomGroup.setText(bw.provideText());
        }
    };
}
