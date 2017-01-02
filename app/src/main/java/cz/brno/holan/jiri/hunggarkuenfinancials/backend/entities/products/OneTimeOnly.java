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

public class OneTimeOnly extends Product {

    public OneTimeOnly() {
    }

    public OneTimeOnly(long id, String name, float value, int stock) {
        super(id, name, value);
        detail = stock;
    }

    public int getStock() {
        return detail;
    }

    public void setStock(int stock) {
        this.detail = stock;
    }
}
