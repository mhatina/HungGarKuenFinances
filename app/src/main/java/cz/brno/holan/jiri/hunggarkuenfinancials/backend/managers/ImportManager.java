package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

public interface ImportManager {

    void importFromFile(Context context, Uri uri) throws IOException;
    int importDescription();
}
