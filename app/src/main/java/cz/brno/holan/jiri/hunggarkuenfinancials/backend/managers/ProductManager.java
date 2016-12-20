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
import android.net.Uri;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.OneTimeOnly;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Periodic;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ProductsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.EntityTabManager;

public class ProductManager extends BaseManager {
    private ArrayList<Product> mProducts;
    private long newProductId;

    private static ProductManager ourInstance = new ProductManager();

    public static ProductManager getInstance() {
        return ourInstance;
    }

    private ProductManager() {
        mProducts = new ArrayList<>();
        getDatabaseReference().keepSynced(true);
    }

    public List<Product> getProducts() {
        return mProducts;
    }

    public void addProduct(Product product) {
        mProducts.add(product);
        upload(product);
    }

    public void deleteProduct(Product product) {
        mProducts.remove(product);
        delete(product);
    }

    public Product findProduct(long id) {
        int size = mProducts.size();
        for (int i = 0; i < size; i++) {
            if (mProducts.get(i).getId() == id)
                return mProducts.get(i);
        }

        return null;
    }

    public Product createProduct(Class<?> type, String name, int price, int detail) {
        newProductId++;
        getDatabaseReference().child("id").setValue(newProductId);

        if (type == Periodic.class)
            return new Periodic(newProductId, name, price, detail);
        else if (type == OneTimeOnly.class)
            return new OneTimeOnly(newProductId, name, price, detail);
        else
            return new Product(newProductId, name, price);
    }

    public void replaceProduct(Product oldProduct, Product newProduct) {
        newProduct.setId(oldProduct.getId());
        deleteProduct(oldProduct);
        addProduct(newProduct);
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
    public String exportDescription() {
        return null;
    }

    @Override
    public void load() {
        mProducts.clear();

        getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        newProductId = dataSnapshot.child("id").getValue(long.class);

                        Class<?>[] classes = new Class[]{Periodic.class, OneTimeOnly.class, Product.class};

                        for (Class<?> aClass : classes) {
                            for (DataSnapshot postSnapshot : dataSnapshot.child(aClass.getSimpleName()).getChildren()) {
                                loadProduct(postSnapshot, aClass);
                            }
                        }

                        ListView productList = EntityTabManager.getInstance().getProductList();
                        if (productList != null) {
                            ProductsAdapter adapter = (ProductsAdapter) productList.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadProduct(DataSnapshot postSnapshot, Class<?> productClass) {
        Product product = (Product) postSnapshot.getValue(productClass);
        mProducts.add(product);
    }

    @Override
    public void upload(BaseEntity entity) {
        Product product = (Product) entity;
        DatabaseReference reference = getDatabaseReference().child(product.getClass().getSimpleName())
                .child(String.valueOf(product.getId()));

        reference.child("id").setValue(product.getId());
        reference.child("name").setValue(product.getName());
        reference.child("note").setValue(product.getNote());
        reference.child("validTime").setValue(product.getValidTime());
        reference.child("validGroup").setValue(product.getValidGroup());
        reference.child("price").setValue(product.getPrice());
        reference.child("group").setValue(product.getGroup());
        reference.child("detail").setValue(product.getDetail());
    }

    @Override
    public void update(BaseEntity entity) {
        Product product = (Product) entity;
        DatabaseReference reference = getDatabaseReference().child(product.getClass().getSimpleName())
                .child(String.valueOf(product.getId()));

        if ((product.getUpdatePropertiesSwitch() & Constant.NAME_SWITCH) > 0)
            reference.child("name").setValue(product.getName());
        if ((product.getUpdatePropertiesSwitch() & Constant.NOTE_SWITCH) > 0)
            reference.child("note").setValue(product.getNote());
        if ((product.getUpdatePropertiesSwitch() & Constant.VALID_TIME_SWITCH) > 0)
            reference.child("validTime").setValue(product.getValidTime());
        if ((product.getUpdatePropertiesSwitch() & Constant.VALID_GROUP_SWITCH) > 0)
            reference.child("validGroup").setValue(product.getValidGroup());
        if ((product.getUpdatePropertiesSwitch() & Constant.PRICE_SWITCH) > 0)
            reference.child("price").setValue(product.getPrice());
        if ((product.getUpdatePropertiesSwitch() & Constant.GROUP_SWITCH) > 0)
            reference.child("group").setValue(product.getGroup());
        if ((product.getUpdatePropertiesSwitch() & Constant.DETAIL_SWITCH) > 0)
            reference.child("detail").setValue(product.getDetail());

        product.clearUpdatePropertiesSwitch();
    }

    @Override
    public void delete(BaseEntity entity) {
        Product product = (Product) entity;
        getDatabaseReference().child(product.getClass().getSimpleName())
                .child(String.valueOf(product.getId()))
                .removeValue();
    }

    @Override
    public DatabaseReference getDatabaseReference() {
        return mDatabase.child("products");
    }

    @Override
    public void importFromFile(Context context, Uri uri) throws IOException {

    }

    @Override
    public String importDescription() {
        return null;
    }
}
