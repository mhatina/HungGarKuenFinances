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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.comparators;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;

/**
 * Comparator for showing all members
 */
public class MemberComparator implements java.util.Comparator<Member> {

    @Override
    public int compare(Member lhs, Member rhs) {
        int statusComparator = lhs.getStatus().getValue() - rhs.getStatus().getValue();
        if (statusComparator == 0) {
            if (lhs.getSurname().equals(rhs.getSurname()))
                return lhs.getName().compareTo(rhs.getName());

            return lhs.getSurname().compareTo(rhs.getSurname());
        }

        return statusComparator;
    }
}
