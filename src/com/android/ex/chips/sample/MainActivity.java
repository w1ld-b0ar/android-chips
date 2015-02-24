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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;

public class MainActivity extends Activity {

    private final static int MAX_NUMBER_OF_CHIPS = 30;


    private RecipientEditTextView mPhoneRecipientField;
    private EditText mMessageField;

    private View.OnClickListener mShowListButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DrawableRecipientChip[] recipientsList = mPhoneRecipientField.getSortedRecipients();
            if (recipientsList == null || recipientsList.length == 0) {
                mMessageField.setText("No recipients.");
                return;
            }

            StringBuilder recipientList = new StringBuilder();
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
    };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneRecipientField = (RecipientEditTextView) findViewById(R.id.phone_retv);
        mPhoneRecipientField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        // Limits the number of chips that can be added to MAX_NUMBER_OF_CHIPS
        mPhoneRecipientField.setMaxNumberOfChipsAllowed(MAX_NUMBER_OF_CHIPS);

        BaseRecipientAdapter adapter =
                new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this);
        adapter.setShowMobileOnly(true);

        mPhoneRecipientField.setAdapter(adapter);
        mPhoneRecipientField.dismissDropDownOnItemSelected(true);

        mMessageField = (EditText) findViewById(R.id.message_field);

        Button demoButton = (Button) findViewById(R.id.demo_button);
        demoButton.setOnClickListener(mShowListButtonClickListener);
    }

}
