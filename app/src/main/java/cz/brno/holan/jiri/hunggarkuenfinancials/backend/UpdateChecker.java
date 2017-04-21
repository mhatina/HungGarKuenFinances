/*
 * Copyright (C) 2017  Martin Hatina
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.backend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.File;

import cz.brno.holan.jiri.hunggarkuenfinancials.BuildConfig;
import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.Log;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpdateChecker {
    private final Activity activity;

    public UpdateChecker(@NonNull Activity activity) {
        this.activity = activity;
    }

    public boolean check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        String currentVersion = remoteConfig.getString(Constant.KEY_FIREBASE_CONFIG_CURRENT_VERSION);
        String appVersion = BuildConfig.VERSION_NAME;
        String updateUrlTemplate = remoteConfig.getString(Constant.KEY_FIREBASE_CONFIG_UPDATE_URL);
        String updateUrl = String.format(updateUrlTemplate, currentVersion, currentVersion);

        boolean hasNewUpdate = !currentVersion.equals(appVersion);
        if (hasNewUpdate) {
            onNewUpdate(updateUrl, currentVersion);
        }

        return hasNewUpdate;
    }

    private void onNewUpdate(final String updateUrl, final String version) {
        final boolean forceUpdate = FirebaseRemoteConfig.getInstance().getBoolean(Constant.KEY_FIREBASE_CONFIG_FORCE_UPDATE);

        new AlertDialog.Builder(activity)
                .setTitle(R.string.new_version)
                .setMessage(R.string.update_app)
                .setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download(Uri.parse(updateUrl), version);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (forceUpdate)
                            activity.finish();
                        else
                            dialog.dismiss();
                    }
                })
                .show();
    }

    private void download(Uri uri, String version) {
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        final String apkName = String.format("HungGarKuenFinances-%s.apk", version);

        request.setTitle(apkName);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, apkName);

        activity.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                File externalDirs = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if (externalDirs == null) {
                    Log.info(activity, activity.getString(R.string.update_error));
                    return;
                }

                File file = new File(String.format("%s/%s", externalDirs.getAbsolutePath(), apkName));
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                activity.startActivity(install);
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        downloadManager.enqueue(request);
    }
}