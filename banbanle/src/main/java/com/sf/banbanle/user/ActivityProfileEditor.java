package com.sf.banbanle.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.basesmartframe.baseui.BaseActivity;
import com.sf.banbanle.R;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

/**
 * Created by NetEase on 2016/12/5 0005.
 */

public class ActivityProfileEditor extends BaseActivity {
    public static final String MODIFY_CONTENT = "modify_content";
    private EditTextClearDroidView mModifyEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);
        mModifyEt = (EditTextClearDroidView) findViewById(R.id.modify_et);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (TextUtils.isEmpty(mModifyEt.getEditText().getText())) {
            SFToast.showToast(R.string.profile_edit_empty_tips);
            return false;
        }
        String content = mModifyEt.getEditText().getText().toString();
        Intent intent = new Intent();
        intent.putExtra(MODIFY_CONTENT, content);
        setResult(RESULT_OK, intent);
        finish();
        return super.onOptionsItemSelected(item);
    }
}
