package xyz.lapig.iceberg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Lapig on 4/23/2017.
 */

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        final EditText editText = (EditText) findViewById(R.id.settingsText);
        editText.setText(user);
        final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);


        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        editText = (EditText)findViewById(R.id.@string/t);
        /*txtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void TexttView.onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                }
            }
        });*/
    }
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if(hasFocus)

    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
