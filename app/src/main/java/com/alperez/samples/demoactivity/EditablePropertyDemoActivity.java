package com.alperez.samples.demoactivity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alperez.library.widget.EditablePropertyView;
import com.alperez.samples.R;

/**
 * Created by stanislav.perchenko on 2/21/2019
 */
public class EditablePropertyDemoActivity extends BaseDemoActivity {

    private EditablePropertyView vEdtFullName;
    private EditablePropertyView vEdtUserName;
    private EditablePropertyView vEdtEmail;
    private EditablePropertyView vEdtPwd1;
    private EditablePropertyView vEdtPwd2;

    private View vActionReg;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_editable_property_demo;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vActionReg = findViewById(R.id.action_register);
        vEdtFullName = findViewById(R.id.edt_fullname);
        vEdtUserName = findViewById(R.id.edt_username);
        vEdtUserName.setOnValueValidityListener((v, isValid) -> updateActionEnabled());
        vEdtEmail = findViewById(R.id.edt_email);
        vEdtEmail.setOnValueValidityListener((v, isValid) -> updateActionEnabled());
        vEdtPwd1 = findViewById(R.id.edt_password_1);
        vEdtPwd1.setOnValueValidityListener((v, isValid) -> updateActionEnabled());
        vEdtPwd2 = findViewById(R.id.edt_password_2);
        vEdtPwd2.setOnValueValidityListener((v, isValid) -> updateActionEnabled());

        updateActionEnabled();
    }

    private void updateActionEnabled() {
        boolean en = vEdtFullName.isPropertyValueValid()
                && vEdtUserName.isPropertyValueValid()
                && vEdtEmail.isPropertyValueValid()
                && vEdtPwd1.isPropertyValueValid()
                && vEdtPwd2.isPropertyValueValid();
        vActionReg.setEnabled(en);
    }
}
