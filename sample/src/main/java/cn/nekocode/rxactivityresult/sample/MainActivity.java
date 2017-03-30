/*
 * Copyright 2017. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.rxactivityresult.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import cn.nekocode.rxactivityresult.ActivityResult;
import cn.nekocode.rxactivityresult.compact.RxActivityResultCompact;
import rx.functions.Action1;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);
        textView.setText(getString(R.string.result_text, ""));

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSubActivityForResult();
            }
        });
    }

    private void startSubActivityForResult() {
        final Intent intent = new Intent(this, SubActivity.class);

        RxActivityResultCompact.startActivityForResult(this, intent, REQUEST_CODE)
                .subscribe(new Action1<ActivityResult>() {
                    @Override
                    public void call(ActivityResult result) {
                        if (result.isOk()) {
                            final String rlt = result.getData().getStringExtra(SubActivity.RLT_TEXT);
                            textView.setText(getString(R.string.result_text, rlt));
                        }
                    }
                });
    }
}
