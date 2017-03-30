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

package cn.nekocode.rxactivityresult;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import rx.Observable;
import rx.functions.Func1;


/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class RxActivityResult {
    private static final String FRAGMENT_TAG = "_RESULT_HANDLE_FRAGMENT_";

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static Observable<ActivityResult> startActivityForResult(
            @NonNull Activity activity, @NonNull Intent intent, int requestCode) {

        return startActivityForResult(activity.getFragmentManager(), intent, requestCode, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static Observable<ActivityResult> startActivityForResult(
            @NonNull Activity activity, @NonNull Intent intent, int requestCode, @Nullable Bundle options) {

        return startActivityForResult(activity.getFragmentManager(), intent, requestCode, options);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static Observable<ActivityResult> startActivityForResult(
            @NonNull Fragment fragment, @NonNull Intent intent, int requestCode) {

        return startActivityForResult(fragment.getFragmentManager(), intent, requestCode, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static Observable<ActivityResult> startActivityForResult(
            @NonNull Fragment fragment, @NonNull Intent intent, int requestCode, @NonNull Bundle options) {

        return startActivityForResult(fragment.getFragmentManager(), intent, requestCode, options);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private static Observable<ActivityResult> startActivityForResult(
            @NonNull FragmentManager fragmentManager, @NonNull final Intent intent, final int requestCode, @Nullable final Bundle options) {

        ResultHandleFragment _fragment = (ResultHandleFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (_fragment == null) {
            _fragment = new ResultHandleFragment();

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(_fragment, FRAGMENT_TAG);
            transaction.commit();

        } else if (Build.VERSION.SDK_INT >= 13 && _fragment.isDetached()) {
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.attach(_fragment);
            transaction.commit();
        }

        final ResultHandleFragment fragment = _fragment;
        return fragment.getIsAttachedBehavior()
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean isAttached) {
                        return isAttached;
                    }
                })
                .flatMap(new Func1<Boolean, Observable<ActivityResult>>() {
                    @Override
                    public Observable<ActivityResult> call(Boolean aBoolean) {
                        if (Build.VERSION.SDK_INT >= 16) {
                            fragment.startActivityForResult(intent, requestCode, options);
                        } else {
                            fragment.startActivityForResult(intent, requestCode);
                        }

                        return fragment.getResultPublisher();
                    }
                })
                .filter(new Func1<ActivityResult, Boolean>() {
                    @Override
                    public Boolean call(ActivityResult result) {
                        return result.getRequestCode() == requestCode;
                    }
                });
    }
}
