package com.chainverse.sample.marketplace;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chainverse.sample.R;
import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseItem;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.model.MarketItem.Categories;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;

public class DetailNFTActivity extends Activity {
    ChainverseItemMarket itemInfo;

    ImageView assetImage;
    TextView txtCategories, txtName, txtPrice, txtDesc;
    Button btnAction;

    boolean isAuction;
    Long tokenId;
    String image;
    String image_preview;
    Double price;
    String symbol;
    String name;
    String categories;
    String currency;
    Long listingId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_nft);

        Intent intent = getIntent();

        isAuction = intent.getBooleanExtra("is_auction", false);
        tokenId = intent.getLongExtra("token_id", 0);
        image = intent.getStringExtra("asset_image");
        image_preview = intent.getStringExtra("image_preview");
        price = intent.getDoubleExtra("price", 0);
        symbol = intent.getStringExtra("symbol_currency");
        name = intent.getStringExtra("name");
        categories = intent.getStringExtra("categories");
        currency = intent.getStringExtra("currency");
        listingId = intent.getLongExtra("listing_id", 0);

        assetImage = (ImageView) findViewById(R.id.imageView);
        txtCategories = (TextView) findViewById(R.id.txtCategories);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPrice = (TextView) findViewById(R.id.txtPriceDetail);
        txtDesc = (TextView) findViewById(R.id.txtPrice);
        btnAction = (Button) findViewById(R.id.button);

        System.out.println(image + " " + image_preview);
        if (image != null) {
            new DownloadImageTask(assetImage).execute(image);
        }

        txtName.setText(name);
        txtPrice.setText(foo(price));
        txtCategories.setText(categories);

        if (isAuction) {
            btnAction.setText("Bid");
        } else {
            btnAction.setText("Buy now");
        }

        ChainverseSDK.getInstance().init(this, new ChainverseCallback() {
            @Override
            public void onInitSDKSuccess() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onItemUpdate(ChainverseItem item, int type) {

            }

            @Override
            public void onGetItems(ArrayList<ChainverseItem> items) {

            }

            @Override
            public void onGetItemMarket(ArrayList<ChainverseItemMarket> items) {

            }

            @Override
            public void onConnectSuccess(String address) {

            }

            @Override
            public void onLogout(String address) {

            }

            @Override
            public void onSignMessage(String signed) {

            }

            @Override
            public void onSignTransaction(String signed) {

            }

            @Override
            public void onBuy(String tx) {
                btnAction.setVisibility(View.GONE);
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listingId != null) {
                    ChainverseSDK.getInstance().buyNFT(currency, listingId, price, isAuction);
                }
            }
        });

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public String foo(double value) {
        String[] values = String.valueOf(value).split("E-");
        String newValue = String.valueOf(value);

        if (values.length > 1) {
            int e = Integer.parseInt(values[1]);
            value *= Math.pow(10, e - 1);
            newValue = "0.";
            for (int i = 0; i < e - 1; i++) {
                newValue += "0";
            }
            newValue += String.valueOf(value).split("0.")[1];
        }
        return newValue;
    }
}
