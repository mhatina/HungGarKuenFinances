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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Youngster;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

import static cz.brno.holan.jiri.hunggarkuenfinancials.Constant.ADULT_GROUP;
import static cz.brno.holan.jiri.hunggarkuenfinancials.Constant.CHILD_GROUP;
import static cz.brno.holan.jiri.hunggarkuenfinancials.Constant.JUNIOR_GROUP;
import static cz.brno.holan.jiri.hunggarkuenfinancials.Constant.YOUNGSTER_GROUP;

public class Utils {

    public static int mapMemberClassToCode(Member member) {
        if (member instanceof Adult)
            return ADULT_GROUP;
        if (member instanceof Youngster)
            return YOUNGSTER_GROUP;
        if (member instanceof Junior)
            return JUNIOR_GROUP;
        if (member instanceof Child)
            return CHILD_GROUP;

        return 0;
    }

    public static <T extends BaseEntity> void copyContent(Collection<T> destination, Collection<T> source) {
        destination.clear();
        destination.addAll(source);
    }

    public static List<Long> getMemberIdsFromString(String members, List<Long> memberIds) {
        for (long id : memberIds) {
            Member member = MemberManager.getInstance().findMember(id);
            String toReplace = member.getName() + " " + member.getSurname();
            members = members.replace(toReplace, "");
        }
        members = members.replace(", ", ",");
        String split[] = members.split(",");
        if (split.length != 0)
            for (String str : split) {
                String memberSplit[] = str.split(" ");
                if (memberSplit.length == 0)
                    continue;

                List<Member> list = MemberManager.getInstance().getMembers(memberSplit);
                if (list.isEmpty())
                    continue;
                Member member = list.get(0);
                memberIds.add(member.getId());
            }
        return memberIds;
    }

    public static int getGroupsFromMemberString(String members, List<Long> memberIds) {
        int groups = 0;
        for (long id : memberIds) {
            Member member = MemberManager.getInstance().findMember(id);
            groups |= Utils.mapMemberClassToCode(member);
            String toReplace = member.getName() + " " + member.getSurname();
            members = members.replace(toReplace, "");
        }
        members = members.replace(", ", ",");
        String split[] = members.split(",");
        if (split.length != 0)
            for (String str : split) {
                String memberSplit[] = str.split(" ");
                if (memberSplit.length == 0)
                    continue;

                List<Member> list = MemberManager.getInstance().getMembers(memberSplit);
                if (list.isEmpty())
                    continue;
                Member member = list.get(0);
                groups |= Utils.mapMemberClassToCode(member);
            }
        return groups;
    }
}
