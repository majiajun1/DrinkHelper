package com.example.drinkhelper.view;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;

/**
 * @author jiajun
 */
public class GeneralPickerDialog {

    public interface PickedListener {
        void onPicked(int valuex);
    }

    public static void show(Context context, String title,String[] items,int defaultValueIndex, PickedListener listener)
    {
        NumberPicker picker = new NumberPicker(context);


        picker.setMinValue(0);
        picker.setMaxValue(items.length-1);
        picker.setDisplayedValues(items);
        int validDefaultValue = defaultValueIndex;
        if (validDefaultValue < picker.getMinValue()) {
            validDefaultValue = picker.getMinValue();
        }
        if (validDefaultValue > picker.getMaxValue()) {
            validDefaultValue = picker.getMaxValue();
        }
        picker.setValue(validDefaultValue);
        picker.setWrapSelectorWheel(true);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(picker)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {

                            int selectedIndex = picker.getValue();
                            String selectedItem = items[selectedIndex];
                            Integer value = Integer.parseInt(selectedItem);
                            listener.onPicked(value);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public static void show(Context context, String title, int min, int max,int defaultValue, PickedListener listener) {
        NumberPicker picker = new NumberPicker(context);
        picker.setWrapSelectorWheel(true);
        if (min < 0) {
            min = 0;
        }
        if (max <= min) {
            max = min + 1;
        }
        picker.setMinValue(min);
        picker.setMaxValue(max);

        if (defaultValue < min) {
            defaultValue = min;
        }
        if (defaultValue > max) {
            defaultValue = max;
        }
        picker.setValue(defaultValue);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(picker)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onPicked(picker.getValue());
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
