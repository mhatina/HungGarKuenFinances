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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.comparators;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;

public class ProductComparator implements java.util.Comparator<Product> {

    @Override
    public int compare(Product lhs, Product rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
}
