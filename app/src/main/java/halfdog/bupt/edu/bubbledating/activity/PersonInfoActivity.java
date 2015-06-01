package halfdog.bupt.edu.bubbledating.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.cache.image.ImageCacheManager;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.constants.Mode;

public class PersonInfoActivity extends ActionBarActivity {

    private static final String TAG = "PersonInfoActivity";

    private de.hdodenhof.circleimageview.CircleImageView mAvator;
    private TextView mName;
    private ImageView mGender;
    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        mAvator = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.person_info_avatar);
        mName = (TextView)findViewById(R.id.person_info_name);
        mGender = (ImageView)findViewById(R.id.person_info_gender);
        mEmail = (TextView)findViewById(R.id.person_info_email);

        mName.setText(BubbleDatingApplication.userEntity.getmName());
        mEmail.setText(BubbleDatingApplication.userEntity.getmEmail());

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean isMale = BubbleDatingApplication.userEntity.getmGender().equals("m");
        String name = BubbleDatingApplication.userEntity.getmName();
        ImageLoader.ImageListener userAvatorListener = null;
        if(isMale){
            userAvatorListener = ImageLoader.getImageListener(mAvator,
                    R.drawable.avatar_default_m, R.drawable.avatar_default_m);
            mGender.setImageDrawable(getResources().getDrawable(R.mipmap.ic_m));
        }else{
            userAvatorListener = ImageLoader.getImageListener(mAvator,
                    R.drawable.avatar_default_f, R.drawable.avatar_default_f);
            mGender.setImageDrawable(getResources().getDrawable(R.mipmap.ic_w));
        }
        String ServerImgUrl = Configuration.SERVER_IMG_CACHE_DIR + File.separator + name + ".png";
        if(Mode.DEBUG){
            Log.d(TAG, "-->ServerImgUrl of " + name + " is : " + ServerImgUrl);
        }
        ImageCacheManager.getInstance().getImage(ServerImgUrl,userAvatorListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                PersonInfoActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
