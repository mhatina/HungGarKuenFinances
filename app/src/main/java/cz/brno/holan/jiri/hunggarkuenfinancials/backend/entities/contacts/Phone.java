/*
 * Copyright (C) 2016  Martin Hatina
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 22/09/16.
 */
public class Phone extends Contact {
    public static final int ICON_PATH = R.drawable.phone_black;

    public Phone(String content, String note) {
        super(content, note);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getRunDescription() {
        return "Call " + getContent();
    }

    @Override
    public String toString() {
        return "Phone{" + super.toString() + "}";
    }

    @Override
    public void run(Context context) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + getContent()));
        context.startActivity(callIntent);
    }
}
