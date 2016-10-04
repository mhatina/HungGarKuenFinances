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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;

/**
 * Created by mhatina on 27/08/16.
 */
public class SearchProvider extends ContentProvider {

    public static final String ON_SEARCH_SUGGESTION_CLICK = SearchProvider.class.getName() + ".ON_SEARCH_SUGGESTION_CLICK";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(
                new String[]{
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA
                }
        );

        ArrayList<String> values = new ArrayList<>();
        String query = uri.getLastPathSegment().toUpperCase();
        String firstPartOfQuery;
        String secondPartOfQuery;
        String possibleDuplicate1;
        String possibleDuplicate2;
        int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));
        int indexOfSpace = query.lastIndexOf(' ');

        if (indexOfSpace != -1) {
            firstPartOfQuery = query.substring(0, indexOfSpace).toUpperCase();
            secondPartOfQuery = query.substring(indexOfSpace + 1);
        } else {
            firstPartOfQuery = query;
            secondPartOfQuery = null;
        }

        for (Member searchMember : MemberManager.getInstance().getMembers()) {
            if (cursor.getCount() >= limit)
                break;

            possibleDuplicate1 = indexOfSpace != -1 ? searchMember.getName() : searchMember.getSurname();
            possibleDuplicate2 = indexOfSpace != -1 ? searchMember.getSurname() : searchMember.getName();

            if (searchMember.getSurname().toUpperCase().startsWith(firstPartOfQuery) && values.indexOf(possibleDuplicate1) == -1) {
                addSuggestionToCursor(cursor, values, secondPartOfQuery, possibleDuplicate1);
            } else if (searchMember.getName().toUpperCase().startsWith(firstPartOfQuery) && values.indexOf(possibleDuplicate2) == -1) {
                addSuggestionToCursor(cursor, values, secondPartOfQuery, possibleDuplicate2);
            }
        }

        return cursor;
    }

    private void addSuggestionToCursor(MatrixCursor cursor, ArrayList<String> values, String needle, String possibleDuplicate) {
        if (needle == null)
            needle = "";

        if (possibleDuplicate.toUpperCase().startsWith(needle.toUpperCase())) {
            cursor.addRow(new Object[]{cursor.getCount() + 1, possibleDuplicate, possibleDuplicate});
            values.add(possibleDuplicate);
        }
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
