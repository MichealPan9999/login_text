package com.example.panzq.webserviceclient;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements OnClickListener {

    EditText et_userName;
    EditText et_password;
    Button bt_Login;
    Button bt_Register;
    Context mContext;
    private static final String URL = "http://10.0.3.2:8080/code_10_login/LoginServlet";
    private static final int REGISTER = 0;
    private static final int LOGIN = 1;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        et_userName = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_Login = (Button) findViewById(R.id.bt_login);
        bt_Login.setOnClickListener(this);
        bt_Register = (Button) findViewById(R.id.bt_register);
        bt_Register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                login(et_userName.getText().toString(), et_password.getText().toString(),LOGIN);
                break;
            case R.id.bt_register:
                login(et_userName.getText().toString(), et_password.getText().toString(),REGISTER);
                break;

        }
    }

    private void login(String username, String password,int type) {
        if (valid(username, password)) {
            this.type = type;
            getHttpgetResult(URL,username,password,type);
        } else {
        }
    }

    private boolean valid(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, "帐号及密码都不可为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        /**
         * 执行访问服务器，通过返回值来判断是否登录成功。
         */
        return true;
    }

    private void getHttpgetResult(String url, String name, String pwd,int type) {

        //new SendHttpTask().execute(url,name,pwd);
        //new MyGETTask().execute(url,name,pwd);
        new MyPostTask().execute(url,name,pwd,String.valueOf(type));
    }

    private class MyPostTask extends AsyncTask<String, Integer, JSONObject> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        String result = "";
        @Override
        protected void onPreExecute() {
            Log.i("panzqww", "onPreExecute() called");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected JSONObject doInBackground(String... params) {
            Log.i("panzqww", "doInBackground(Params... params) called");
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post  = new HttpPost(params[0]);
                List<NameValuePair> paramsValue=new ArrayList<NameValuePair>();
                Log.d("panzqww","params[0] = "+params[0]);
                Log.d("panzqww","params[1] = "+params[1]);
                Log.d("panzqww","params[2] = "+params[2]);
                Log.d("panzqww","params[3] = "+params[3]);
                paramsValue.add(new BasicNameValuePair("name", params[1]));
                paramsValue.add(new BasicNameValuePair("pwd", params[2]));
                paramsValue.add(new BasicNameValuePair("type", params[3]));
                post.setEntity(new UrlEncodedFormEntity(paramsValue,"UTF-8"));
                HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    long total = entity.getContentLength();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int count = 0;
                    int length = -1;
                    while ((length = is.read(buf)) != -1) {
                        baos.write(buf, 0, length);
                        count += length;
                        //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                        publishProgress((int) ((count / (float) total) * 100));
                        //为了演示进度,休眠500毫秒
                        Thread.sleep(500);
                    }
                    result = new String(baos.toByteArray(), "utf-8");
                    if (TextUtils.isEmpty(result))
                    {
                        return null;
                    }
                    return new JSONObject(result);
                }
            } catch (Exception e) {
                Log.e("panzqww", e.getMessage());
            }
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i("panzqww", "onProgressUpdate(Progress... progresses) called ");
            //progressBar.setProgress(progresses[0]);
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result == null)
            {
                Toast.makeText(mContext, "服务器访问失败，服务器未正常启动！", Toast.LENGTH_SHORT).show();
                return;
            }
            String type = "";
            boolean flag = false;
            try {
                type = (String) result.get("type");
                flag = result.getBoolean("result");
                if (flag) {
                    if (Integer.valueOf(type) == LOGIN) {
                        Toast.makeText(mContext, " 登录成功, " + "欢迎" + result.get("name"), Toast.LENGTH_SHORT).show();
                    } else if (Integer.valueOf(type) == REGISTER) {
                        Toast.makeText(mContext, "注册成功，" + result.get("name") + "您可以登录啦,have fun!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (Integer.valueOf(type) == LOGIN) {
                        Toast.makeText(mContext, "登录失败！", Toast.LENGTH_SHORT).show();
                    }else if(Integer.valueOf(type) == REGISTER)
                    {
                        Toast.makeText(mContext, "注册失败，该用户名已被注册！", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i("panzqww", "onCancelled() called");
            //progressBar.setProgress(0);
        }
    }
    private class MyGETTask extends AsyncTask<String, Integer, JSONObject> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            Log.i("panzqww", "----MyGETTask onPreExecute() called");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected JSONObject doInBackground(String... params) {
            Log.i("panzqww", "doInBackground(Params... params) called");
            try {
                HttpClient client = new DefaultHttpClient();
                String url = params[0]+"?name="+params[1]+"&pwd="+params[2];
                Log.d("panzqww","=====url = "+url);
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);
                Log.d("panzqww","=======response.getStatusLine() = "+response.getStatusLine());
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    long total = entity.getContentLength();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int count = 0;
                    int length = -1;
                    while ((length = is.read(buf)) != -1) {
                        baos.write(buf, 0, length);
                        count += length;
                        //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                        publishProgress((int) ((count / (float) total) * 100));
                        //为了演示进度,休眠500毫秒
                        Thread.sleep(500);
                    }
                    String result = new String(baos.toByteArray(), "utf-8");
                    return new JSONObject(result);
                }
            } catch (Exception e) {
                Log.e("panzqww", e.getMessage());
            }
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i("panzqww", "onProgressUpdate(Progress... progresses) called");
            //progressBar.setProgress(progresses[0]);
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(JSONObject result) {
            Log.i("panzq", "onPostExecute(Result result) called result = "+result);
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i("panzqww", "onCancelled() called");
            //progressBar.setProgress(0);
        }
    }

    private JSONObject receiveJson(String result)
    {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }
}
