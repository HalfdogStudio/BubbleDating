package halfdog.bupt.edu.bubbledating.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import halfdog.bupt.edu.bubbledating.R;
import halfdog.bupt.edu.bubbledating.activity.halfdog.bupt.edu.bubbledating.entity.User;
import halfdog.bupt.edu.bubbledating.activity.halfdog.bupt.edu.bubbledating.tool.CustomRequest;
import halfdog.bupt.edu.bubbledating.constants.ResponseState;
import halfdog.bupt.edu.bubbledating.constants.UserInfoKeys;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final String LOGIN_URL = "http://10.108.245.37:8080/BubbleDatingServer/HandleLogin";

    private com.gc.materialdesign.views.ButtonRectangle useWithoutLogin;
    private com.gc.materialdesign.views.ButtonRectangle registerButton;
    private com.gc.materialdesign.views.ButtonRectangle loginButton;

    private static EditText loginName;
    private static EditText loginPw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        useWithoutLogin = (com.gc.materialdesign.views.ButtonRectangle)findViewById(R.id.use_without_login);
        registerButton = (ButtonRectangle)findViewById(R.id.login_activity_register);
        loginButton = (ButtonRectangle)findViewById(R.id.login_activity_launch);
        loginName = (EditText)findViewById(R.id.login_activity_username);
        loginPw = (EditText)findViewById(R.id.login_activity_password);



        useWithoutLogin.setOnClickListener(buttonListener);
        registerButton.setOnClickListener(buttonListener);
        loginButton.setOnClickListener(buttonListener);

    }

    View.OnClickListener buttonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.use_without_login:
                    Intent toMainActivity = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(toMainActivity);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    break;
                case R.id.login_activity_launch:
                    login(LoginActivity.this);
                    break;
                case R.id.login_activity_register:
                    Intent toRegisterActivity = new Intent(LoginActivity.this,RegisterAccount.class);
                    startActivity(toRegisterActivity);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;

            }

        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public static void  login(final Context context){
        final String username = loginName.getText().toString();
        String pw = loginPw.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(context,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(context,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,String> loginInfo = new HashMap<>();
        loginInfo.put("username",username);
        loginInfo.put("password",pw);


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomRequest loginRequest = new CustomRequest(Request.Method.POST,LOGIN_URL,loginInfo,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG,"-->log in response:"+jsonObject.toString());
                try {
                    int status = jsonObject.getInt(ResponseState.RESPONSE_STATUS_KEY);
                    switch(status){
                        case ResponseState.OK:
                            Toast.makeText(context,"登陆成功",Toast.LENGTH_SHORT).show();
                            JSONObject res = (JSONObject)jsonObject.get("user_info");
                            Intent toMainAcvitiy = new Intent(context,MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(UserInfoKeys.U_ID,res.getInt(UserInfoKeys.U_ID));
                            bundle.putString(UserInfoKeys.U_NAME, res.getString(UserInfoKeys.U_NAME));
                            bundle.putString(UserInfoKeys.U_PASSWORD, res.getString(UserInfoKeys.U_PASSWORD));
                            bundle.putString(UserInfoKeys.U_EMAIL, res.getString(UserInfoKeys.U_EMAIL));
                            bundle.putString(UserInfoKeys.U_GENDER, res.getString(UserInfoKeys.U_GENDER));
                            bundle.putBoolean(UserInfoKeys.U_ONLINE, res.getBoolean(UserInfoKeys.U_ONLINE));
                            toMainAcvitiy.putExtras(bundle);
                            context.startActivity(toMainAcvitiy);

                            ((Activity)context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            break;
                        case ResponseState.UNKOWN_USERNAME:
                            Toast.makeText(context,"用户名错误",Toast.LENGTH_SHORT).show();
                            break;
                        case ResponseState.USERNAME_PASSWORD_UNCOMPATIBLE:
                            Toast.makeText(context,"密码与用户名不匹配",Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context,"未知的错误",Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        requestQueue.add(loginRequest);
    }
}
