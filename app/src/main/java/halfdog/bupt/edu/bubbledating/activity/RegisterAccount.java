package halfdog.bupt.edu.bubbledating.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.AndroidCharacter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import halfdog.bupt.edu.bubbledating.BubbleDatingApplication;
import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.constants.Configuration;
import halfdog.bupt.edu.bubbledating.entity.UserEntity;
import halfdog.bupt.edu.bubbledating.tool.CustomRequest;
import halfdog.bupt.edu.bubbledating.constants.ResponseState;
import halfdog.bupt.edu.bubbledating.tool.RequestManager;

public class RegisterAccount extends Activity {
    private static final String REGISTER_URL = Configuration.SERVER_IP + "/BubbleDatingServer/HandleRegistration";
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
    private  File mCroppedAvatar;
    private ProgressDialog progressDialog;

    private String uName;
    private String uPw;
    private String uEmail;
    private String uGender;
    private String uAvatarString;

    private final Context context  = RegisterAccount.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

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

        progressDialog = new ProgressDialog(RegisterAccount.this,"请稍候");
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

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_activity_submit:
                    uName = userName.getText().toString();
                    uPw = userPassword.getText().toString();
                    uEmail = userEmail.getText().toString();
                    uGender = male.isChecked() ? "m" : "f";
                    if (TextUtils.isEmpty(uName)) {
                        Toast.makeText(RegisterAccount.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (TextUtils.isEmpty(uPw)) {
//                            userPassword.setError("密码不能为空");
                        Toast.makeText(RegisterAccount.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (TextUtils.isEmpty(uEmail)) {
//                            userEmail.setError("邮箱不能为空");
                        Toast.makeText(RegisterAccount.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()) {
//                            userEmail.setError("邮箱格式不合法");
                        Toast.makeText(RegisterAccount.this, "邮箱格式不合法", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    progressDialog.show();

//                    RequestQueue requestQueue = Volley.newRequestQueue(RegisterAccount.this);
                    Map<String, String> jsonData = new HashMap<String, String>();
                    jsonData.put("username", uName);
                    jsonData.put("password", uPw);
                    jsonData.put("email", uEmail);
                    jsonData.put("gender", uGender);
                    jsonData.put("avatar",uAvatarString);
                    jsonData.put("lat",String.valueOf(BubbleDatingApplication.userLatLng.latitude));
                    jsonData.put("lon",String.valueOf(BubbleDatingApplication.userLatLng.longitude));

                    CustomRequest registerRequest = new CustomRequest(Request.Method.POST, REGISTER_URL, jsonData, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d(TAG, "-->on response:" + jsonObject.toString());
                            try {
                                int response = (int) jsonObject.get(ResponseState.RESPONSE_STATUS_KEY);
                                switch (response) {
                                    case ResponseState.OK:
                                        Toast.makeText(RegisterAccount.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        BubbleDatingApplication.userEntity = new UserEntity(-1,uName,uPw,uEmail
                                        ,uGender,null,true);
                                        Intent jumpToMainActivity = new Intent(RegisterAccount.this,MainActivity.class);
                                        progressDialog.dismiss();
                                        startActivity(jumpToMainActivity);
                                        RegisterAccount.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                        break;
                                    case ResponseState.USER_NAME_DUPLICATE:
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterAccount.this, "用户名已被使用，请重新输入", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ResponseState.EMAIL_DUPLICATE:
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterAccount.this, "邮箱已被使用，请重新输入", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ResponseState.UNKNOWN_ERROR:
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterAccount.this, "很抱歉，发生了未知的错误，请联系管理员", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterAccount.this, "未知", Toast.LENGTH_SHORT).show();
                                        break;

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.i(TAG, "--> on error response:" + volleyError.toString());
                        }
                    });
                    RequestManager.getInstance(context).add(registerRequest);

                    break;
                case R.id.register_activity_quit:
                    RegisterAccount.this.finish();
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
//                    Bundle bundle = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap)bundle.get("data");
//                    Log.d(TAG,"-->imageBitmap:"+imageBitmap.toString());
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"default1.png");
//                    if(file.exists()){
//                        file.delete();
//                        try {
//                            file.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    startPhotoZoom(Uri.fromFile(file));
                }
                break;
            case REQUEST_FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(data.getData());
//                    try {
//                        Bitmap b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                        userAvatar.setImageBitmap(b);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
                break;
            case REQUEST_PHOTO_ZOOM:
                if(data != null){
                    Bundle extras = data.getExtras();
                    if(extras != null){
                        Bitmap bitmap = extras.getParcelable("data");
                        Drawable drawable = new BitmapDrawable(this.getResources(),bitmap);
                        userAvatar.setImageDrawable(drawable);

                        try {
                            //create a file
//                            mCroppedAvatar = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"user.png");
//                            mCroppedAvatar.createNewFile();
                            //convert bitmap to byte array
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
                            byte[] mBitmapData = bos.toByteArray();
                            uAvatarString = Base64.encodeToString(mBitmapData,Base64.DEFAULT);

                            //write bytes in file
//                            FileOutputStream fileOutputStream = new FileOutputStream(mCroppedAvatar);
//                            fileOutputStream.write(mBitmapData);
//                            fileOutputStream.flush();
//                            fileOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }


        }
    }

    Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.d(TAG, "-->response:" + jsonObject.toString());
        }
    };

    Response.ErrorListener jsonOjectErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.d(TAG, "-->error response :" + volleyError.toString());
        }
    };

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

        Log.d(TAG,"-->startPhotoZoom URI:"+uri.toString());
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
        mStartPhotoZoom.putExtra("crop","true");
        mStartPhotoZoom.putExtra("aspectX",1);
        mStartPhotoZoom.putExtra("aspectY",1);
        mStartPhotoZoom.putExtra("outputX",200);
        mStartPhotoZoom.putExtra("outputY",200);
        mStartPhotoZoom.putExtra("scale",true);
        mStartPhotoZoom.putExtra("return-data",true);
        startActivityForResult(mStartPhotoZoom,REQUEST_PHOTO_ZOOM);




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
