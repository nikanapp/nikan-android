package com.bloomlife.android.framework;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface ActivityResultCallback {

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

}
