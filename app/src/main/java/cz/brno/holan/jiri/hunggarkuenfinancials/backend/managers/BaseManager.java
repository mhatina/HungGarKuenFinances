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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;

public abstract class BaseManager implements DatabaseManager, ExportManager, ImportManager {

    int groupFilter;

    BaseManager() {
        resetGroupFilter();
    }

    public void toggleGroupFilter(int groupFilter) {
        this.groupFilter |= groupFilter;
    }

    public void resetGroupFilter() {
        this.groupFilter = 0;
    }

    @Override
    public void delete(BaseEntity entity) {
        getDatabaseReference().child(entity.getClass().getSimpleName())
                .child(String.valueOf(entity.getId()))
                .removeValue();
    }

    @Override
    public void export(Context context, String filename) {
        writeToFile(context, filename, toString());
    }

    private void writeToFile(Context context, String filename, String data) {
        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            return;
        }

        try {
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException ignored) {
        }
    }
}
