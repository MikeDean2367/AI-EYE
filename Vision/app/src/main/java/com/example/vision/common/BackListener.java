package com.example.vision.common;

import android.app.Instrumentation;
import android.view.KeyEvent;
import android.view.View;

public class BackListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            }
        }).start();
    }
}
