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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members;

import java.util.Date;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

public class Child extends Member {

    public static final int ICON_PATH = R.drawable.child_icon;

    public Child() {
    }

    public Child(long id, String firstName, String surname) {
        super(id, firstName, surname);
    }

    public Child(long id, String firstName, String surname, Date paidUntil) {
        super(id, firstName, surname, paidUntil);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String toString() {
        return "Child{" +
                super.toString() +
                '}';
    }
}
