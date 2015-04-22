package halfdog.bupt.edu.bubbledating.activity;

import android.app.Activity;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.activity.halfdog.bupt.edu.bubbledating.tool.CustomRequest;
import halfdog.bupt.edu.bubbledating.constants.ResponseState;

public class RegisterAccount extends Activity {
    private static final String REGISTER_URL = "http://10.108.245.37:8080/BubbleDatingServer/HandleRegistration";
    private static final String TAG = "RegisterAccount";

    private EditText userName;
    private EditText userPassword;
    private EditText userEmail;
    private RadioGroup userGender;
    private ButtonRectangle submit;
    private ButtonRectangle quit;
    private RadioButton male;
    private RadioButton female;

    private String uName;
    private String uPw;
    private String uEmail;
    private String uGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        userName = (EditText)findViewById(R.id.register_activity_user_name);
        userPassword = (EditText)findViewById(R.id.register_activity_password);
        userEmail = (EditText)findViewById(R.id.register_activity_email);
        userGender = (RadioGroup)findViewById(R.id.register_activity_gender_select);
        submit = (ButtonRectangle)findViewById(R.id.register_activity_submit);
        quit = (ButtonRectangle)findViewById(R.id.register_activity_quit);
        male = (RadioButton)findViewById(R.id.register_activity_radio_male);
        female = (RadioButton)findViewById(R.id.register_activity_radio_female);

        userGender.setOnCheckedChangeListener(checkedChangeListener);
        submit.setOnClickListener(clickListener);
        quit.setOnClickListener(clickListener);


    }

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.register_activity_radio_male:
                    if(!male.isChecked()){
                        male.toggle();
                        female.toggle();
                    }
                    break;
                case R.id.register_activity_radio_female:
                    if(!female.isChecked()){
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
                switch(v.getId()){
                    case R.id.register_activity_submit:
                        uName = userName.getText().toString();
                        uPw = userPassword.getText().toString();
                        uEmail = userEmail.getText().toString();
                        uGender = male.isChecked()?"m":"f";
                        if(TextUtils.isEmpty(uName)){
//                            userName.setError("用户名不能为空");
//                            Toast toast = Toast.makeText(RegisterAccount.this, "用户名不能为空", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,20);
//                            toast.show();
                            Toast.makeText(RegisterAccount.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if(TextUtils.isEmpty(uPw)){
//                            userPassword.setError("密码不能为空");
                            Toast.makeText(RegisterAccount.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if(TextUtils.isEmpty(uEmail)){
//                            userEmail.setError("邮箱不能为空");
                            Toast.makeText(RegisterAccount.this,"邮箱不能为空",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if(!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()){
                            userEmail.setError("邮箱格式不合法");
                            Toast.makeText(RegisterAccount.this,"邮箱格式不合法",Toast.LENGTH_SHORT).show();
                            break;
                        }

                        RequestQueue requestQueue = Volley.newRequestQueue(RegisterAccount.this);
                        Map<String,String> jsonData = new HashMap<String,String>();
                        jsonData.put("username",uName);
                        jsonData.put("password",uPw);
                        jsonData.put("email",uEmail);
                        jsonData.put("gender",uGender);

                        CustomRequest registerRequest = new CustomRequest(Request.Method.POST,REGISTER_URL,jsonData, new Response.Listener <JSONObject>(){

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(TAG,"-->on response:"+jsonObject.toString());
                                try {
                                    int response =  (int)jsonObject.get(ResponseState.RESPONSE_STATUS_KEY);
                                    switch(response){
                                        case ResponseState.OK:
                                            Toast.makeText(RegisterAccount.this, "注册成功", Toast.LENGTH_SHORT).show();
                                            LoginActivity.login(RegisterAccount.this);
                                            break;
                                        case ResponseState.USER_NAME_DUPLICATE:
                                            Toast.makeText(RegisterAccount.this,"用户名已被使用，请重新输入",Toast.LENGTH_SHORT).show();
                                            break;
                                        case ResponseState.EMAIL_DUPLICATE:
                                            Toast.makeText(RegisterAccount.this,"邮箱已被使用，请重新输入",Toast.LENGTH_SHORT).show();
                                            break;
                                        case ResponseState.UNKNOWN_ERROR:
                                            Toast.makeText(RegisterAccount.this,"很抱歉，发生了未知的错误，请联系管理员",Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            Toast.makeText(RegisterAccount.this,"未知",Toast.LENGTH_SHORT).show();
                                            break;

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.i(TAG,"--> on error response:"+volleyError.toString());
                            }
                        });
                        requestQueue.add(registerRequest);

                        break;
                    case R.id.register_activity_quit:
                        RegisterAccount.this.finish();
                        break;

                }
        }
    };


    Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.d(TAG,"-->response:"+jsonObject.toString());
        }
    };

    Response.ErrorListener jsonOjectErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.d(TAG,"-->error response :"+volleyError.toString());
        }
    };


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

//    public  static class   RegisterResponse {
//        static final int OK = 0;
//        static final int USER_NAME_DUPLICATE= 1;
//        static final int EMAIL_DUPLICATE = 2;
//        static final int UNKNOWN_ERROR = 3;
//    }
}
