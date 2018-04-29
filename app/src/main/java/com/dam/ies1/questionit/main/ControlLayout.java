package com.dam.ies1.questionit.main;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

public class ControlLayout {

    public static void deshabilitarLyt(Context context){
        ((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void habilitarLyt(Context context){
        ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
