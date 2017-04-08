package com.example.android.inventory.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.R;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by aizulhussin on 02/04/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    private String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();


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
        final TextView productQuantityTv = (TextView) view.findViewById(R.id.product_quantity);
        ImageView productImageView = (ImageView)view.findViewById(R.id.product_image);
        Button buttonSell = (Button) view.findViewById(R.id.button_sell);

        final int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        String productName = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        String productPrice = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE));
        final String productQuantity = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
        String productImagePath = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE_PATH));


        productNameTv.setText(productName);
        productPriceTv.setText(productPrice);
        productQuantityTv.setText(productQuantity);
        productImageView.setImageURI(Uri.parse(productImagePath));

        //final int prodQuantityInteger = Integer.parseInt(productQuantity);

        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {


                int prodQuantityInteger = Integer.parseInt(productQuantityTv.getText().toString());

                Log.d(LOG_TAG, "Sell Quantity= " + prodQuantityInteger);

                if (prodQuantityInteger > 0) {
                    prodQuantityInteger--;
                    productQuantityTv.setText(String.valueOf(prodQuantityInteger));

                    Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_QUANTITY, productQuantityTv.getText().toString());

                    int rowsUpdated = view.getContext().getContentResolver().update(currentUri, values, null, null);

                    if (rowsUpdated > 0) {
                        Toast.makeText(view.getContext(), "Sold!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), "Ooops!Not sold..", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(view.getContext(), "Oops no items to sell", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

}
