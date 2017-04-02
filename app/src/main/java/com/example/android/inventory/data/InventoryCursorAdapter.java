package com.example.android.inventory.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventory.R;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by aizulhussin on 02/04/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView productNameTv = (TextView) view.findViewById(R.id.product_name);
        TextView productPriceTv = (TextView) view.findViewById(R.id.product_price);
        TextView productQuantityTv = (TextView)view.findViewById(R.id.product_quantity);
        ImageView productImageView = (ImageView)view.findViewById(R.id.product_image);

        String productName = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        String productPrice = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE));
        String productQuantity = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
        String productImagePath = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE_PATH));


        productNameTv.setText(productName);
        productPriceTv.setText(productPrice);
        productQuantityTv.setText(productQuantity);
        //productImageView.setImageURI(Uri.parse(productImagePath));


    }
}
