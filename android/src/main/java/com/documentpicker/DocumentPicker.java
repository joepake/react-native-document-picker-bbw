package com.documentpicker;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    }

    private Callback callback;

    public DocumentPicker(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void show(ReadableMap args, final Callback callback) {
        Log.e("lllllll ", "args = " + args);
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
//        properties.root = new File(root);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
//        properties.extensions = Config.EXT_MEDIA;

        FilePickerDialog dialog = new FilePickerDialog(getReactApplicationContext(), properties,
                Color.GREEN, Color.GREEN);
        dialog.setTitle("lala");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    Log.e("lalallllllll", files[0]);


                    callback.invoke(null, metaDataFromFile(new File(files[0])));
                }
            }
        });
        dialog.show();

        this.callback = callback;
    }

    private WritableMap metaDataFromFile(File file) {
        WritableMap map = Arguments.createMap();

        if(!file.exists())
            return map;

        map.putInt(Fields.FILE_SIZE, (int) file.length());
        map.putString(Fields.FILE_NAME, file.getName());
        map.putString(Fields.TYPE, mimeTypeFromName(file.getAbsolutePath()));

        return map;
    }

    private static String mimeTypeFromName(String absolutePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(absolutePath);
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            return null;
        }
    }
}
