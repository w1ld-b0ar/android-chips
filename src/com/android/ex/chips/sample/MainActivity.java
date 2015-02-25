/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ex.chips.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;

public class MainActivity extends Activity {

    private final static int MAX_NUMBER_OF_CHIPS = 30;

    private final static String MESSAGE_MODE = "MESSAGE_MODE";


    private RecipientEditTextView mPhoneRecipientField;
    private View mSubjectBlock;
    private EditText mSubjectField, mMessageField;

    private boolean mIsInEmailMode = false;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSubjectBlock = findViewById(R.id.subject_block);
        mSubjectField = (EditText) findViewById(R.id.subject_field);
        mMessageField = (EditText) findViewById(R.id.message_field);

        mPhoneRecipientField = (RecipientEditTextView) findViewById(R.id.phone_retv);
        mPhoneRecipientField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        // Limits the number of chips that can be added to MAX_NUMBER_OF_CHIPS
        mPhoneRecipientField.setMaxNumberOfChipsAllowed(MAX_NUMBER_OF_CHIPS);

        if (savedInstanceState != null && savedInstanceState.getBoolean(MESSAGE_MODE)) {
            sendEmailMode();
        } else {
            // Sending SMS by default, but can be changed in the menu
            sendSmsMode();
        }

        mPhoneRecipientField.dismissDropDownOnItemSelected(true);
        // The RecipientEditTextView must request the focus when the screen orientation changes
        mPhoneRecipientField.post(new Runnable() {
            public void run() {
                mPhoneRecipientField.requestFocus();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MESSAGE_MODE, mIsInEmailMode);
    }

    private void showRecipientList() {
        StringBuilder recipientList = new StringBuilder();

        if (mIsInEmailMode) {
            recipientList.append("Subject: ");
            recipientList.append(mSubjectField.getText());
            recipientList.append("\n\n");
        }

        DrawableRecipientChip[] recipientsList = mPhoneRecipientField.getSortedRecipients();
        if (recipientsList == null || recipientsList.length == 0) {
            recipientList.append("No recipients.");
            mMessageField.setText(recipientList);
            return;
        }

        recipientList.append("List of recipients:");
        for (int i = 0; i < recipientsList.length; i++) {
            recipientList.append("\n * Recipient ");
            recipientList.append(i + 1);
            recipientList.append(": ");
            recipientList.append(recipientsList[i].getValue());
            recipientList.append(";");
        }
        mMessageField.setText(recipientList);
    }

    private void sendSmsMode() {
        if (!mIsInEmailMode && mSubjectField.getVisibility() == View.GONE)
            return;

        BaseRecipientAdapter adapter =
                new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this);
        adapter.setShowMobileOnly(true);

        mPhoneRecipientField.clearRecipients();
        mPhoneRecipientField.setAdapter(adapter);

        mSubjectBlock.setVisibility(View.GONE);
        mSubjectField.setText("");
        mMessageField.setText("");

        mIsInEmailMode = false;
    }

    private void sendEmailMode() {
        if (mIsInEmailMode)
            return;

        BaseRecipientAdapter adapter =
                new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_EMAIL, this);
        adapter.setShowMobileOnly(false);

        mPhoneRecipientField.clearRecipients();
        mPhoneRecipientField.setAdapter(adapter);

        mSubjectBlock.setVisibility(View.VISIBLE);
        mSubjectField.setText("");
        mMessageField.setText("");

        mIsInEmailMode = true;
    }


    //
    // Menu
    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_send:
                showRecipientList();
                return true;
            case R.id.action_switch_to_sms:
                sendSmsMode();
                return true;
            case R.id.action_switch_to_email:
                sendEmailMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
