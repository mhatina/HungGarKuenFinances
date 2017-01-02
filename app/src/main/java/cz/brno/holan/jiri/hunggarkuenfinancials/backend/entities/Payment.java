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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities;

import java.util.List;

public class Payment extends BaseEntity {

    private List<Long> memberIds;
    private long productId;
    private int price;
    private int payed;
    private float discount;
    private String note;

    private int updatePropertiesSwitch = 0;

    public Payment() {
    }

    public Payment(long id, List<Long> memberIds, long productId) {
        super(id);
        this.memberIds = memberIds;
        this.productId = productId;
    }

    public int getUpdatePropertiesSwitch() {
        return updatePropertiesSwitch;
    }

    public void clearUpdatePropertiesSwitch() {
        this.updatePropertiesSwitch = 0;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
        this.payed = payed;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        if (getId() != payment.getId()) return false;
        if (memberIds != payment.memberIds) return false;
        return productId == payment.productId;

    }

    @Override
    public int hashCode() {
        int result = memberIds.hashCode();
        result = 31 * result + (int) (productId ^ (productId >>> 32));
        return result;
    }
}
