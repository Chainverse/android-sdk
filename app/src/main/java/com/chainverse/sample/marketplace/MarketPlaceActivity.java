package com.chainverse.sample.marketplace;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chainverse.sample.R;
import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseItem;
import com.chainverse.sdk.model.MarketItem.Categories;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;
import com.chainverse.sdk.ChainverseSDK;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class MarketPlaceActivity extends AppCompatActivity {

    private String developerAddress;
    private String gameAddress;
    private final static String TAG = "MarketPlaceActivity";

    ArrayList<ChainverseItemMarket> listNFT = new ArrayList<>();

    GridView gridView;
    ProgressBar loadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Market Place");

        setContentView(R.layout.market_place);


        gridView = (GridView) findViewById(R.id.listItem);
        loadingBar = (ProgressBar) findViewById(R.id.loading);

        Intent intent = getIntent();

        developerAddress = intent.getStringExtra("developerAddress");
        gameAddress = intent.getStringExtra("gameAddress");

        listenerSDK(developerAddress, gameAddress);
        getListNFTMarket();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChainverseItemMarket item = (ChainverseItemMarket) gridView.getItemAtPosition(i);

                String categories = "";
                if (item.getCategories() != null) {
                    for (Categories cate : item.getCategories()) {
                        if (categories.isEmpty()) {
                            categories += cate.getName();
                        } else {
                            categories += ", " + cate.getName();
                        }
                    }

                }

                Intent intent = new Intent(MarketPlaceActivity.this, DetailNFTActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", item);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    protected void getListNFTMarket() {
        ChainverseSDK.getInstance().getItemOnMarket(0, 10, "");
    }

    protected void listenerSDK(String developerAddress, String gameAddress) {
        ChainverseSDK.getInstance().init(developerAddress, gameAddress, this, new ChainverseCallback() {

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
                onReceivedMarketItems(items);
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

            }
        });
    }

    void onReceivedMarketItems(ArrayList<ChainverseItemMarket> items) {
        Log.i(TAG, "onReceivedMarket Items");
        loadingBar.setVisibility(View.GONE);
        listNFT = items;
        ListNFTAdapter adapter = new ListNFTAdapter(MarketPlaceActivity.this, listNFT);
        gridView.setAdapter(adapter);
        // Get more info
        for (int i = 0; i < items.size(); i++) {
            ChainverseItemMarket item = items.get(i);
            NftProgress nftProgress = new NftProgress(item, i);
            nftProgress.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    class NftProgress extends AsyncTask<Void, NftProgress, ChainverseItemMarket> {
        private ChainverseItemMarket chainverseItemMarket;
        private int index;

        public NftProgress(ChainverseItemMarket chainverseItemMarket, int index) {
            this.chainverseItemMarket = chainverseItemMarket;
            this.index = index;
        }

        @Override
        protected ChainverseItemMarket doInBackground(Void... voids) {
            ChainverseItemMarket nftInfo = ChainverseSDK.getInstance().getNFT(chainverseItemMarket.getNft(), chainverseItemMarket.getTokenId());
            return nftInfo;
        }

        @Override
        protected void onPostExecute(ChainverseItemMarket nftInfo) {
            if (nftInfo != null && !nftInfo.getListingId().equals(BigInteger.ZERO)) {
                Log.i(TAG, "Update information for the item");
                if (nftInfo.getImage() != null)
                    this.chainverseItemMarket.setImage(nftInfo.getImage());
                if (nftInfo.getImage_preview() != null)
                    this.chainverseItemMarket.setImage_preview(nftInfo.getImage_preview());
                if (nftInfo.getName() != null)
                    this.chainverseItemMarket.setName(nftInfo.getName());
                if (nftInfo.getAttributes() != null) {
                    this.chainverseItemMarket.setAttributes(nftInfo.getAttributes());
                }
                if (nftInfo.getListingId() != null) {
                    this.chainverseItemMarket.setListingId(nftInfo.getListingId());
                }
                if (nftInfo.getPrice() != null)
                    this.chainverseItemMarket.setPrice(nftInfo.getPrice());
                if (nftInfo.getAuctionInfo() != null)
                    this.chainverseItemMarket.setAuctionInfo(nftInfo.getAuctionInfo());
                if (nftInfo.getListingInfo() != null)
                    this.chainverseItemMarket.setListingInfo(nftInfo.getListingInfo());
                if (nftInfo.isAuction() != null)
                    this.chainverseItemMarket.setAuction(nftInfo.isAuction());
                listNFT.set(index, this.chainverseItemMarket);
            } else {
                Log.e(TAG, "Updated information for the item not found or invalid, remove it from list");
                listNFT.remove(index);
            }
            ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }
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
