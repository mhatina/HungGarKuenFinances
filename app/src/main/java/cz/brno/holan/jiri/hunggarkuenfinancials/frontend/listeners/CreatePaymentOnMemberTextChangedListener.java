/*
 * Copyright (C) 2016  Martin Hatina
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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

public class CreatePaymentOnMemberTextChangedListener implements TextWatcher {
    private TextInputLayout inputLayout;
    private boolean watcherOn = true;
    private CharSequence oldSequence = "";

    public CreatePaymentOnMemberTextChangedListener(TextInputLayout inputLayout) {
        this.inputLayout = inputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        int oldLength = oldSequence.length();
        if (!watcherOn)
            return;

        oldSequence = charSequence.toString();
        String str = charSequence.toString();

        // this is true only when deleting letters
        if (charSequence.length() <= oldLength || str.lastIndexOf(",") == str.length() - 1) {
            inputLayout.setError(null);
            return;
        }

        String split[] = str.split(",");
        String last = split[split.length - 1];

        if (last.startsWith(" "))
            last = last.substring(1);

        String splitLast[] = last.split(" ");
        Member member = null;
        List<Member> members = null;

        if (splitLast.length != 0)
            members = MemberManager.getInstance().getMembers(splitLast);

        if (members != null && members.size() != 0) {
            member = members.get(0);
            inputLayout.setError(null);
        } else
            inputLayout.setError("No such member");

        if (member != null) {
            String textToSet = "";
            if (str.contains(","))
                textToSet = str.substring(0, str.lastIndexOf(",") + 2);
            if (member.getName().toUpperCase().startsWith(splitLast[0].toUpperCase()))
                textToSet += member.getName() + " " + member.getSurname();
            else
                textToSet += member.getSurname() + " " + member.getName();
            watcherOn = false;
            inputLayout.getEditText().setText(textToSet);

            if (start + 1 < textToSet.length())
                inputLayout.getEditText().setSelection(start + 1, textToSet.length());
            else
                inputLayout.getEditText().setSelection(textToSet.length());
            watcherOn = true;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
