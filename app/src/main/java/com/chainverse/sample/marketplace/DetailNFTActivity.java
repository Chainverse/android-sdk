package com.chainverse.sample.marketplace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chainverse.sample.MainActivity;
import com.chainverse.sample.R;
import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseItem;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.model.MarketItem.Categories;
import com.chainverse.sdk.model.NFT.NFT;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class DetailNFTActivity extends AppCompatActivity {
    NFT itemInfo;

    ImageView assetImage;
    TextView txtCategories, txtName, txtPrice, txtDesc;
    Button btnAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.detail_nft);

        Intent intent = getIntent();

        itemInfo = (NFT) intent.getSerializableExtra("item");

        setTitle(itemInfo.getName());

        assetImage = (ImageView) findViewById(R.id.imageView);
        txtCategories = (TextView) findViewById(R.id.txtCategories);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPrice = (TextView) findViewById(R.id.txtPriceDetail);
        txtDesc = (TextView) findViewById(R.id.txtPrice);
        btnAction = (Button) findViewById(R.id.button);

        if (itemInfo.getImage() != null) {
            new DownloadImageTask(assetImage).execute(itemInfo.getImage());
        }

//        txtName.setText(itemInfo.getName());
//        txtPrice.setText(foo(itemInfo.getPrice()));
//        txtCategories.setText(parseCategories(itemInfo.getCategories()));

        if (itemInfo.getInfoSell().isAuction()) {
            btnAction.setText("Bid");
        } else {
            btnAction.setText("Buy now");
        }

        ChainverseSDK.getInstance().init(MainActivity.CONTRACT.developerAddress, MainActivity.CONTRACT.gameAddress, this, new ChainverseCallback() {
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
            public void onGetListItemMarket(ArrayList<NFT> items, int count) {

            }

            @Override
            public void onGetMyAssets(ArrayList<NFT> items) {

            }

            @Override
            public void onGetDetailItem(NFT nft) {

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
            public void onSignTransaction(Constants.EFunction function, String signed) {

            }

            @Override
            public void onTransact(Constants.EFunction function, String tx) {
                btnAction.setVisibility(View.GONE);
            }

            @Override
            public void onErrorTransaction(Constants.EFunction function, String error) {

            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (itemInfo.getInfoSell().getListingId() != null) {
//                    ChainverseSDK.getInstance().buyNFT(
//                            itemInfo.getInfoSell().getCurrencyInfo().getCurrency(),
//                            itemInfo.getInfoSell().getListingId().longValue(),
//                            itemInfo.getInfoSell().getPrice(),
//                            itemInfo.getInfoSell().isAuction());
//                }
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

    private String parseCategories(ArrayList<Categories> cates) {
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
