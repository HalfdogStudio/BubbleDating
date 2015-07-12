package halfdog.bupt.edu.bubbledating.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.constants.Configurations;
import halfdog.bupt.edu.bubbledating.constants.UserInfoKeys;
import halfdog.bupt.edu.bubbledating.entity.UserEntity;
import halfdog.bupt.edu.bubbledating.tool.CustomRequest;
import halfdog.bupt.edu.bubbledating.constants.ResponseState;
import halfdog.bupt.edu.bubbledating.tool.NetworkStatusTool;
import halfdog.bupt.edu.bubbledating.tool.RequestManager;

public class RegisterAccountActivity extends Activity {
    private static final String REGISTER_URL = Configurations.SERVER_IP + "/BubbleDatingServer/HandleRegistration";
    private static final String TAG = "RegisterAccount";
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_FROM_GALLERY = 2;
    private final int REQUEST_PHOTO_ZOOM = 3;

    private EditText userName;
    private EditText userPassword;
    private EditText userEmail;
    private RadioGroup userGender;
    private ButtonRectangle submit;
    private ButtonRectangle quit;
    private RadioButton male;
    private RadioButton female;
    private ImageView userAvatar;
    private ProgressDialog progressDialog;

    private String uName;
    private String uPw;
    private String uEmail;
    private String uGender;
    private String uAvatarString;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        initUI();
    }

    public void initUI() {
        context = this;

        userName = (EditText) findViewById(R.id.register_activity_user_name);
        userPassword = (EditText) findViewById(R.id.register_activity_password);
        userEmail = (EditText) findViewById(R.id.register_activity_email);
        userGender = (RadioGroup) findViewById(R.id.register_activity_gender_select);
        submit = (ButtonRectangle) findViewById(R.id.register_activity_submit);
        quit = (ButtonRectangle) findViewById(R.id.register_activity_quit);
        male = (RadioButton) findViewById(R.id.register_activity_radio_male);
        female = (RadioButton) findViewById(R.id.register_activity_radio_female);
        userAvatar = (ImageView) findViewById(R.id.register_activity_user_avatar);

        userGender.setOnCheckedChangeListener(checkedChangeListener);
        submit.setOnClickListener(clickListener);
        quit.setOnClickListener(clickListener);
        userAvatar.setOnClickListener(clickListener);

        progressDialog = new ProgressDialog(RegisterAccountActivity.this, this.getResources().getString(R.string.wait_a_moment));
    }

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.register_activity_radio_male:
                    if (!male.isChecked()) {
                        male.toggle();
                        female.toggle();
                    }
                    break;
                case R.id.register_activity_radio_female:
                    if (!female.isChecked()) {
                        male.toggle();
                        female.toggle();
                    }
                    break;
            }
        }
    };

    Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            {
                Log.d(TAG, "-->on response:" + jsonObject.toString());
                try {
                    int response = (int) jsonObject.get(ResponseState.RESPONSE_STATUS_KEY);
                    switch (response) {
                        case ResponseState.OK:
                            Toast.makeText(RegisterAccountActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            BubbleDatingApplication.userEntity = new UserEntity(-1, uName, uPw, uEmail
                                    , uGender, null, true);

                            /* save user info to "account" shared preference */
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences(Configurations.ACOUNT_SHARE_PREFERENCE,
                                    MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong(UserInfoKeys.U_ID, -1);
                            editor.putString(UserInfoKeys.U_NAME, uName);
                            editor.putString(UserInfoKeys.U_PASSWORD, uPw);
                            editor.putString(UserInfoKeys.U_EMAIL, uEmail);
                            editor.putString(UserInfoKeys.U_GENDER, uGender);
                            editor.commit();


                            Intent jumpToMainActivity = new Intent(RegisterAccountActivity.this, MainActivity.class);
                            progressDialog.dismiss();
                            startActivity(jumpToMainActivity);
                            RegisterAccountActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                            break;
                        case ResponseState.USER_NAME_DUPLICATE:
                            progressDialog.dismiss();
                            Toast.makeText(RegisterAccountActivity.this, "用户名已被使用，请重新输入", Toast.LENGTH_SHORT).show();
                            break;
                        case ResponseState.EMAIL_DUPLICATE:
                            progressDialog.dismiss();
                            Toast.makeText(RegisterAccountActivity.this, "邮箱已被使用，请重新输入", Toast.LENGTH_SHORT).show();
                            break;
                        case ResponseState.UNKNOWN_ERROR:
                            progressDialog.dismiss();
                            Toast.makeText(RegisterAccountActivity.this, "很抱歉，发生了未知的错误，请联系管理员", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            progressDialog.dismiss();
                            Toast.makeText(RegisterAccountActivity.this, "未知", Toast.LENGTH_SHORT).show();
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            Toast.makeText(RegisterAccountActivity.this, R.string.volley_request_timeout_error, Toast.LENGTH_LONG).show();
        }
    };


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_activity_submit:

                    uName = userName.getText().toString().trim();
                    Pattern pattern = Pattern.compile("[a-z0-9_]*");
                    Matcher matcher = pattern.matcher(uName);
                    boolean res = matcher.matches();
                    if(!res){
                        Toast.makeText(context,context.getResources().getString(R.string.register_user_name_not_compliant),Toast.LENGTH_LONG).show();
                        userName.setText("");
                        return ;
                    }
                    uPw = userPassword.getText().toString();
                    uEmail = userEmail.getText().toString();
                    uGender = male.isChecked() ? "m" : "f";
                    if (TextUtils.isEmpty(uName)) {
                        Toast.makeText(RegisterAccountActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (TextUtils.isEmpty(uPw)) {
//                            userPassword.setError("密码不能为空");
                        Toast.makeText(RegisterAccountActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (TextUtils.isEmpty(uEmail)) {
//                            userEmail.setError("邮箱不能为空");
                        Toast.makeText(RegisterAccountActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()) {
//                            userEmail.setError("邮箱格式不合法");
                        Toast.makeText(RegisterAccountActivity.this, "邮箱格式不合法", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (!NetworkStatusTool.isConnected(context)) {
                        Toast.makeText(context, context.getResources().getString(R.string.network_unavailabel), Toast.LENGTH_LONG).show();
                        return;
                    }

                    progressDialog.show();

//                    RequestQueue requestQueue = Volley.newRequestQueue(RegisterAccount.this);
                    Map<String, String> jsonData = new HashMap<String, String>();
                    jsonData.put("username", uName);
                    jsonData.put("password", uPw);
                    jsonData.put("email", uEmail);
                    jsonData.put("gender", uGender);
                    jsonData.put("avatar", uAvatarString);
                    jsonData.put("lat", String.valueOf(BubbleDatingApplication.userLatLng.latitude));
                    jsonData.put("lon", String.valueOf(BubbleDatingApplication.userLatLng.longitude));

                    CustomRequest registerRequest = new CustomRequest(Request.Method.POST, REGISTER_URL, jsonData,
                            responseListener, errorListener);
                    RetryPolicy retryPolicy = new DefaultRetryPolicy(
                            Configurations.REQUEST_TIMEOUT_MS,
                            Configurations.MAX_RETRY_TIMES,
                            Configurations.BACK_OFF_MULTI
                    );
                    registerRequest.setRetryPolicy(retryPolicy);
                    RequestManager.getInstance(context).add(registerRequest);
                    break;
                case R.id.register_activity_quit:
                    RegisterAccountActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.register_activity_user_avatar:
                    showOptionDialog();

            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "default1.png");
                    startPhotoZoom(Uri.fromFile(file));
                }
                break;
            case REQUEST_FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(data.getData());
                }
                break;
            case REQUEST_PHOTO_ZOOM:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap bitmap = extras.getParcelable("data");
                        Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
                        userAvatar.setImageDrawable(drawable);

                        try {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                            byte[] mBitmapData = bos.toByteArray();
                            uAvatarString = Base64.encodeToString(mBitmapData, Base64.DEFAULT);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }


        }
    }


    public void showOptionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(new String[]{"选择本地图片", "拍照"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent mFromGallery = new Intent();
                                mFromGallery.setType("image/*");
                                mFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(mFromGallery, REQUEST_FROM_GALLERY);
                                break;
                            case 1:
                                Intent mFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                File mLocPath = new File(path, "default1.png");
                                Log.d(TAG, "-->path:" + mLocPath);
                                mFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mLocPath));
                                startActivityForResult(mFromCapture, REQUEST_TAKE_PHOTO);
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    /*
    *       图片剪裁的方法
    * */
    public void startPhotoZoom(Uri uri) {

        Log.d(TAG, "-->startPhotoZoom URI:" + uri.toString());
        Intent mStartPhotoZoom = new Intent("com.android.camera.action.CROP");
        mStartPhotoZoom.setDataAndType(uri, "image/*");
        // 设置剪裁参数
        /*
        *       附加选项    数据类型    描述
                crop    String  发送裁剪信号
                aspectX int X方向上的比例
                aspectY int Y方向上的比例
                outputX int 裁剪区的宽
                outputY int 裁剪区的高
                scale   boolean 是否保留比例
                return-data boolean 是否将数据保留在Bitmap中返回
                data    Parcelable  相应的Bitmap数据
                circleCrop  String  圆形裁剪区域？
                MediaStore.EXTRA_OUTPUT ("output")  URI 将URI指向相应的file:///
        *
        * */
        mStartPhotoZoom.putExtra("crop", "true");
        mStartPhotoZoom.putExtra("aspectX", 1);
        mStartPhotoZoom.putExtra("aspectY", 1);
        mStartPhotoZoom.putExtra("outputX", 200);
        mStartPhotoZoom.putExtra("outputY", 200);
        mStartPhotoZoom.putExtra("scale", true);
        mStartPhotoZoom.putExtra("return-data", true);
        startActivityForResult(mStartPhotoZoom, REQUEST_PHOTO_ZOOM);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return true;
    }

}
