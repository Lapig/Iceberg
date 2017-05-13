package xyz.lapig.iceberg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.Snackbar;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Lapig on 4/23/2017.
 */

public class SettingsActivity extends Activity {
    EditText userTextField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        userTextField = (EditText) findViewById(R.id.settingsText);
        userTextField.setText(user);
        final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(userTextField.getWindowToken(), 0);
        mgr.showSoftInput(userTextField, InputMethodManager.SHOW_IMPLICIT);


        userTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgr.showSoftInput(userTextField, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        userTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText lost focus
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.user), (userTextField.getText().toString()).toLowerCase());
                    editor.commit();
                    Globals.setUser(userTextField.getText().toString().toLowerCase());
                    snackAttack("committed change");
                }
                else{
                    snackAttack("else block");
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        //SharedPreferences sharedPref = getParent().getPreferences(Context.MODE_PRIVATE);
        //userTextField.setText(sharedPref.getString(getString(R.string.user), "lapigr"));
        userTextField.setText(Globals.getUser());
    }
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus){

        }
    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public void snackAttack(String msg){
        Snackbar.make(findViewById(R.id.settingsView), msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}
