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

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;

@SuppressWarnings("SameParameterValue")
public abstract class Contact extends BaseEntity {
    private static final int ICON_PATH = R.drawable.home_black;
    private String content;
    private String note;

    Contact() {
    }

    public Contact(long id, String contact, String note) {
        super(id);
        this.content = contact;
        this.note = note;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String toString() {
        return "content='" + content + '\'' +
                ", note='" + note + '\'';
    }

    public abstract String getRunDescription(Context context);

    public abstract void run(Context context);


}
