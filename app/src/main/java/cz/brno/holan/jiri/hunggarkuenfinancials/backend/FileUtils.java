package cz.brno.holan.jiri.hunggarkuenfinancials.backend;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.Log;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;

public class FileUtils {

    public static void showFileChooser(AppCompatActivity activity, String description) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(
                    Intent.createChooser(intent, description),
                    Constant.FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.info(activity, R.string.install_file_manager);
        }
    }
}
