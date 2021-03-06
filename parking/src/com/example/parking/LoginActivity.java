package com.example.parking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"gouyf@ehualu.com:123456"};

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
    private CheckBox mKeepUserinfo;
    private CheckBox mKeepPassword;
    private static final String SAVE_FILE_NAME = "save_spref";
	private static final int ATTENDANCE_TYPE_START=301;
	private static final int ATTENDANCE_TYPE_END=302;
	private static final int ERROR_TYPE_EMAIL=401;
	private static final int ERROR_TYPE_PASSWD=402;
	private static final int ERROR_TYPE_NO_ERROR=403;
	private int mErrorType = ERROR_TYPE_NO_ERROR;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
        mKeepUserinfo = (CheckBox) findViewById(R.id.ck_userinfo);
        mKeepPassword = (CheckBox) findViewById(R.id.ck_password);
        mKeepUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBoolean(mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
            }
        });
        mKeepPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBoolean(mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
            }
        });
        initView();
        IntentFilter filter = new IntentFilter();  
        filter.addAction("ExitApp");  
        registerReceiver(mReceiver, filter); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

    private void initView() {
        if (readBoolean("isuserinfo")) {
        	mKeepUserinfo.setChecked(true);
        	mEmailView.setText(readData("userinfo").toString());
        }
        if (readBoolean("ispassword")) {
        	mKeepPassword.setChecked(true);
            mPasswordView.setText(readData("password").toString());
        }
    }

    private String readData(String data) {
        SharedPreferences pref = getSharedPreferences(SAVE_FILE_NAME, MODE_MULTI_PROCESS);
        String str = pref.getString(data, "");
        return str;
    }

    private boolean writeData(String userinfo, String password, boolean isUserinfo, boolean isPassword) {
        SharedPreferences.Editor share_edit = getSharedPreferences(SAVE_FILE_NAME,
                MODE_MULTI_PROCESS).edit();
        share_edit.putString("userinfo", userinfo);
        share_edit.putString("password", password);
        share_edit.putBoolean("isuserinfo", isUserinfo);
        share_edit.putBoolean("ispassword", isPassword);
        share_edit.commit();
        return true;
    }

    private boolean readBoolean(String data) {
        SharedPreferences pref = getSharedPreferences(SAVE_FILE_NAME, MODE_MULTI_PROCESS);
        return pref.getBoolean(data, false);
    }

    private void writeBoolean(boolean isUserinfo, boolean isPassword) {
        SharedPreferences.Editor share_edit = getSharedPreferences(SAVE_FILE_NAME,
                MODE_MULTI_PROCESS).edit();
        share_edit.putBoolean("isuserinfo", isUserinfo);
        share_edit.putBoolean("ispassword", isPassword);
        share_edit.commit();
    }

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_passwd_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_email_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					if(pieces[1].equals(mPassword)){
						return true;
					}else{
						mErrorType = ERROR_TYPE_PASSWD;
					}
				}else{
					mErrorType = ERROR_TYPE_EMAIL;
				}
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				String userinfo = mEmailView.getText().toString();
		        String password = mPasswordView.getText().toString();
		        writeData(userinfo, password, mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
				Intent intent = new Intent(LoginActivity.this,WorkAttendanceActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("attendancetype", ATTENDANCE_TYPE_START);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			} else {
				if(mErrorType == ERROR_TYPE_EMAIL){
					mEmailView.setError(getString(R.string.error_incorrect_email));
					mEmailView.requestFocus();
				}else if(mErrorType == ERROR_TYPE_PASSWD){
					mPasswordView
					.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				}
			}
			mErrorType=ERROR_TYPE_NO_ERROR;
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction()!=null && intent.getAction().equals("ExitApp")){
				finish();
			}
		}            
    }; 
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
	/**
	 * Add for request login's state
	public void login(String name, String pwd)throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  String strurl = "//此处url待定";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.addHeader("Content-Type","application/json");//还可以自定义增加header
		  JSONObject param = new JSONObject();//定义json对象
		  param.put("type", "login");
		  param.put("userinfo", mEmail);
		  param.put("passwd", mPassword);
		  Log.e("yifan", param.toString());
		  StringEntity se = new StringEntity(param.toString());
		  request.setEntity(se);//发送数据
		  HttpResponse httpResponse = httpClient.execute(request);//获得响应
		  int code = httpResponse.getStatusLine().getStatusCode();
		  if(code==HttpStatus.SC_OK){
			  String strtResult = EntityUtils.toString(httpResponse.getEntity());
			  JSONObject result = new JSONObject(strtResult);
			  String loginResult = (String) result.get("loginresult");
			  if(loginResult.equals("ok")){
				  String parkName = (String) result.get("parkname");
				  String parkNumber = (String) result.get("parknumber");
				  String userNumber = (String) result.get("usernumber");
				  String workStartTIme = (String) result.get("workstarttime");
				  String workEndTIme = (String) result.get("workendtime");
			  }
		  }else{
			  Log.e("yifan", Integer.toString(code));
		  }
		 }
	//Client's json:{ "type":"login", "userinfo":"gouyf@ehualu.com", "passwd":"123456"}
	//Server's json:{ "loginresult":"ok", "usernumber":"101884", "parkname":"易华录停车场", "parknumber":"P1234", "workstarttime":"9:00", "workendtime":"17:30"}
    //Server's json:{ "loginresult":"wrongemail"}
    //Server's json:{ "loginresult":"wrongpasswd"}
   */
}
