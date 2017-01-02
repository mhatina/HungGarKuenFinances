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

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;

public class Product extends BaseEntity {

    private String name;
    private String note;
    private long validTime;
    private int validGroup;
    private float price;
    private int group;
    protected int detail;

    private int updatePropertiesSwitch = 0;

    public Product() {
    }

    public Product(long id, String name, float value) {
        super(id);
        this.name = name;
        this.price = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name == null || !this.name.equals(name)) {
            this.name = name;
            updatePropertiesSwitch |= Constant.NAME_SWITCH;
        }
    }

    public float getPrice() {
        return price;
    }

    public long getValidTime() {
        return validTime;
    }

    public void setValidTime(long validTime) {
        if (this.validTime != validTime) {
            this.validTime = validTime;
            updatePropertiesSwitch |= Constant.VALID_TIME_SWITCH;
        }
    }

    public void setPrice(float price) {
        if (this.price != price) {
            this.price = price;
            updatePropertiesSwitch |= Constant.PRICE_SWITCH;
        }
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        if (this.group != group) {
            this.group = group;
            updatePropertiesSwitch |= Constant.GROUP_SWITCH;
        }
    }

    public void toggleGroup(int group, boolean state) {
        boolean defaultState = (this.group & group) != 0;
        if (defaultState != state) {
            this.group ^= group;
            updatePropertiesSwitch |= Constant.GROUP_SWITCH;
        }
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if (this.note == null || !this.note.equals(note)) {
            this.note = note;
            updatePropertiesSwitch |= Constant.NOTE_SWITCH;
        }
    }

    public int getValidGroup() {
        return validGroup;
    }

    public void setValidGroup(int validGroup) {
        if (this.validGroup != validGroup) {
            this.validGroup = validGroup;
            updatePropertiesSwitch |= Constant.VALID_GROUP_SWITCH;
        }
    }

    public int getDetail() {
        return detail;
    }

    public void setDetail(int detail) {
        if (this.detail != detail) {
            this.detail = detail;
            updatePropertiesSwitch |= Constant.DETAIL_SWITCH;
        }
    }

    public int getUpdatePropertiesSwitch() {
        return updatePropertiesSwitch;
    }

    public void clearUpdatePropertiesSwitch() {
        this.updatePropertiesSwitch = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (price != product.price) return false;
        return !(name != null ? !name.equals(product.name) : product.name != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (validTime ^ (validTime >>> 32));
        result = 31 * result + validGroup;
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + group;
        result = 31 * result + detail;
        return result;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
               ", price=" + price;
    }
}
