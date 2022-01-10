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

import com.chainverse.sample.MainActivity;
import com.chainverse.sample.R;
import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseItem;
import com.chainverse.sdk.model.MarketItem.Categories;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.model.NFT.InfoSell;
import com.chainverse.sdk.model.NFT.NFT;
import com.chainverse.sdk.model.Params.FilterMarket;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class MarketPlaceActivity extends AppCompatActivity {

    private String developerAddress;
    private String gameAddress;
    private String type;
    private final static String TAG = "MarketPlaceActivity";
    ArrayList<NFT> listNFT = new ArrayList<>();

    GridView gridView;
    ProgressBar loadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.market_place);


        gridView = (GridView) findViewById(R.id.listItem);
        loadingBar = (ProgressBar) findViewById(R.id.loading);

        Intent intent = getIntent();

        developerAddress = MainActivity.CONTRACT.developerAddress;
        gameAddress = MainActivity.CONTRACT.gameAddress;
        type = intent.getStringExtra("type");

        setTitle("Market Place");
        getListNFTMarket();

        listenerSDK(developerAddress, gameAddress);

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
        FilterMarket filterMarket = new FilterMarket();
        filterMarket.setPageSize(20);
        filterMarket.setPage(0);
        ChainverseSDK.getInstance().getListItemOnMarket(filterMarket);
    }

    protected void getListMyAssets() {
        ChainverseSDK.getInstance().getMyAsset();
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
//                System.out.println("run herere " + items);
            }

            @Override
            public void onGetListItemMarket(ArrayList<NFT> items) {
                onReceivedMarketItems(items);
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
            public void onSignTransaction(String signed) {
            }

            @Override
            public void onBuy(String tx) {

            }
        });
    }

    void onReceivedMarketItems(ArrayList<NFT> items) {
        Log.i(TAG, "onReceivedMarket Items");
        loadingBar.setVisibility(View.GONE);
        listNFT = items;
        ListNFTAdapter adapter = new ListNFTAdapter(MarketPlaceActivity.this, listNFT);
        gridView.setAdapter(adapter);
        // Get more info
        for (int i = 0; i < items.size(); i++) {
            NFT item = items.get(i);
            NftProgress nftProgress = new NftProgress(item, i);
            nftProgress.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    class NftProgress extends AsyncTask<Void, NftProgress, NFT> {
        private NFT nft;
        private int index;

        public NftProgress(NFT nft, int index) {
            this.nft = nft;
            this.index = index;
        }

        @Override
        protected NFT doInBackground(Void... voids) {
            NFT nftInfo = ChainverseSDK.getInstance().getNFT(nft.getNft(), nft.getTokenId());
            return nftInfo;
        }

        @Override
        protected void onPostExecute(NFT nftInfo) {
            if (nftInfo != null && nftInfo.getInfoSell() != null && !nftInfo.getInfoSell().getListingId().equals(BigInteger.ZERO)) {
                InfoSell infoSell = new InfoSell();

                NFT updatedItem = listNFT.get(index);
                updatedItem.setName(nftInfo.getName());
                updatedItem.setImagePreview(nftInfo.getImagePreview());
                updatedItem.setImage(nftInfo.getImage());
                updatedItem.setAttributes(nftInfo.getAttributes());
                updatedItem.setAuction(nftInfo.getAuction());
                updatedItem.setListing(nftInfo.getListing());

                infoSell.setListingId(nftInfo.getInfoSell().getListingId());
                infoSell.setPrice(nftInfo.getInfoSell().getPrice());
                infoSell.setIsAuction(nftInfo.getInfoSell().isAuction());
                infoSell.setCurrencyInfo(nft.getInfoSell().getCurrencyInfo());

                updatedItem.setInfoSell(infoSell);
                listNFT.set(index, updatedItem);
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
