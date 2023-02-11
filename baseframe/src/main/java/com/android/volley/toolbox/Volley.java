/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.UploadFileQueue;

import java.io.File;

public class Volley {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    private static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    private static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

    private static RequestQueue sQueue;
    private static Integer sSequence;
    private static Object sTag;

    public static RequestQueue getRequestQueue(){
        return sQueue;
    }

    public static void addToTagQueue(Request<?> request){
        if (sQueue != null){
            request.setTag(sTag);
            sQueue.add(request);
        }

    }

    public static void add(Request<?> request){
        if (sQueue != null){
            sQueue.add(request);
        }
    }

    public static void init(Context context){
        sQueue = newRequestQueue(context.getApplicationContext());
        sSequence = new Integer(0);
        sUploadFileQueue = new UploadFileQueue();
        sUploadFileQueue.start();
    }

    public static void cancelAll(Object obj){
        if (sQueue != null)
            sQueue.cancelAll(obj);
    }

    public static void setTag(Object obj){
        sTag = obj;
    }

    public static Object getTag(){
        return sSequence++;
    }

    private static UploadFileQueue sUploadFileQueue;

    public static void uploadFileRequest(UploadFileRequest request){
        sUploadFileQueue.add(request);
    }

    public static void uploadFileToQiNiuRequest(UploadFileToQiNiuRequest request){
        sUploadFileQueue.setStack(new UploadFileToQiNiuStack());
        sUploadFileQueue.add(request);
    }
}
