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

import java.util.ArrayList;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.OneTimeOnly;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Periodic;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;

/**
 * Created by mhatina on 03/10/16.
 */
public class ProductManager extends BaseManager {
    private ArrayList<Product> mProducts;

    private static ProductManager ourInstance = new ProductManager();

    public static ProductManager getInstance() {
        return ourInstance;
    }

    private ProductManager() {
        mProducts = new ArrayList<>();
        // TODO remove
        addProduct(new Periodic(0, "Training", 3000, 0, 3));
        addProduct(new OneTimeOnly(0, "Training", 3000));
    }



    public List<Product> getProducts() {
        return mProducts;
    }

    public void addProduct(Product product) {
        mProducts.add(product);
    }

    public void deleteProduct(Product product) {
        mProducts.remove(product);
    }


    @Override
    public String toString() {
        String exportText = "";
        for (Product product : mProducts) {
            exportText += product.toString() + "\n";
        }

        return exportText;
    }

    @Override
    public void load(MainActivity activity) {

    }

    @Override
    public void upload(BaseEntity entity) {

    }

    @Override
    public void update(BaseEntity entity) {

    }

    @Override
    public void delete(BaseEntity entity) {

    }
}
