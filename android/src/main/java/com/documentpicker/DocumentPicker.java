package com.documentpicker;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.documentpicker.controller.DialogSelectionListener;
import com.documentpicker.model.DialogConfigs;
import com.documentpicker.model.DialogProperties;
import com.documentpicker.view.FilePickerDialog;

import java.io.File;

public class DocumentPicker extends ReactContextBaseJavaModule {
    private static final String NAME = "RNDocumentPicker";

    private static class Fields {
        private static final String FILE_SIZE = "fileSize";
        private static final String FILE_NAME = "fileName";
        private static final String TYPE = "type";
        private static final String URI = "uri";
    }

    public DocumentPicker(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void show(ReadableMap args, final Callback callback) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(root);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
//        properties.extensions = Config.EXT_MEDIA;

        int color = getThemeAccentColor(getCurrentActivity());

        FilePickerDialog dialog = new FilePickerDialog(getCurrentActivity(), properties,
                color, color);
        dialog.setTitle(args.getString("title"));

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    callback.invoke(null, metaDataFromFile(new File(files[0])));
                }
            }
        });
        dialog.show();
    }

    private WritableMap metaDataFromFile(File file) {
        WritableMap map = Arguments.createMap();

        if (!file.exists())
            return map;

        map.putInt(Fields.FILE_SIZE, (int) file.length());
        map.putString(Fields.FILE_NAME, file.getName());
        map.putString(Fields.TYPE, mimeTypeFromName(file.getAbsolutePath()));
        map.putString(Fields.URI, "file://" + file.getAbsolutePath());

        return map;
    }

    private String mimeTypeFromName(String absolutePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(absolutePath);
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            return null;
        }
    }

    private int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }
}
