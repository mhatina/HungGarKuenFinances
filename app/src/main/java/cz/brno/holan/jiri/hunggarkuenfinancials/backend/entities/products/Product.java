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
 * Created by mhatina on 3/8/16.
 */
public class Product {
    String name;
    int value;
    boolean isAvailable;

    public Product(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (value != product.value) return false;
        return !(name != null ? !name.equals(product.name) : product.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
               ", value=" + value +
               ", isAvailable=" + isAvailable;
    }
}