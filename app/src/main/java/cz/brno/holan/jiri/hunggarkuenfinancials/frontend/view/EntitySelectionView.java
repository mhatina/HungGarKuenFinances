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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view;

import android.content.Context;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;

public class EntitySelectionView implements SearchView.OnQueryTextListener {

    private final View view;

    public EntitySelectionView(Context context) {
        view = View.inflate(context, R.layout.layout_payment_choose_list, null);

        ListView listView = (ListView) view.findViewById(R.id.choose_list_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        SearchView searchView = (SearchView) view.findViewById(R.id.choose_list_search);
        searchView.setOnQueryTextListener(this);
    }

    public View getView() {
        return view;
    }

    public ListView getListView() {
        return (ListView) view.findViewById(R.id.choose_list_list);
    }

    public void setAdapter(ListAdapter adapter) {
        ListView listView = (ListView) view.findViewById(R.id.choose_list_list);
        listView.setAdapter(adapter);
    }

    public void setOnPositiveButtonClickListener(View.OnClickListener listener) {
        view.findViewById(R.id.choose_list_positive_button).setOnClickListener(listener);
    }

    public void setOnNegativeButtonClickListener(View.OnClickListener listener) {
        view.findViewById(R.id.choose_list_negative_button).setOnClickListener(listener);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        ListView listView = getListView();

        int position = getMatchPosition(s);
        if (position != -1) {
            listView.smoothScrollToPosition(position);
        }
        return true;
    }

    private String getShowedTextFromEntity(BaseEntity entity) {
        if (entity instanceof Member) {
            Member member = (Member) entity;
            return member.getName() + " " + member.getSurname();
        } else if (entity instanceof Product) {
            return ((Product) entity).getName();
        }

        return "";
    }

    private int getMatchPosition(String s) {
        ListView list = getListView();

        for (int i = 0; i < list.getCount(); i++) {
            BaseEntity entity = (BaseEntity) list.getItemAtPosition(i);
            String text = getShowedTextFromEntity(entity);

            for (String split : text.split(" "))
                if (split.toLowerCase().startsWith(s.toLowerCase()))
                    return i;
        }

        return -1;
    }
}
