package com.snc.zero.activity;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Abstract base activity
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context getContext() {
        return this;
    }

}
