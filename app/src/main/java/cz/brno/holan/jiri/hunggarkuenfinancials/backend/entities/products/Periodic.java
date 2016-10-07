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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products;

/**
 * Created by mhatina on 01/10/16.
 */
public class Periodic extends Product {

    long validTime;
    int perWeek;

    public Periodic(long id, String name, int value, long validTime, int perWeek) {
        super(id, name, value);
        this.validTime = validTime;
        this.perWeek = perWeek;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", validTime=" + validTime +
                ", perWeek=" + perWeek;
    }
}
