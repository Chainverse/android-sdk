package com.chainverse.sample.marketplace;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.chainverse.sample.R;
import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseItem;
import com.chainverse.sdk.model.MarketItem.Categories;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;
import com.chainverse.sdk.ChainverseSDK;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class MarketPlaceActivity extends Activity {
    Thread thread;

    private String developerAddress;
    private String gameAddress;


    ArrayList<ChainverseItemMarket> listNFT = new ArrayList<>();

    GridView gridView;
    ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_place);

        gridView = (GridView) findViewById(R.id.listItem);
        btnBack = (ImageButton) findViewById(R.id.imageButtonBack);

        Intent intent = getIntent();

        developerAddress = intent.getStringExtra("developerAddress");
        gameAddress = intent.getStringExtra("gameAddress");

        listenerSDK(developerAddress, gameAddress);
        getListNFTMarket();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChainverseItemMarket o = (ChainverseItemMarket) gridView.getItemAtPosition(i);

                String categories = "";

                for (Categories cate : o.getCategories()) {
                    if (categories.isEmpty()) {
                        categories += cate.getName();
                    } else {
                        categories += ", " + cate.getName();
                    }
                }

                Intent intent = new Intent(MarketPlaceActivity.this, DetailNFTActivity.class);

                intent.putExtra("token_id", o.getTokenId().longValue());
                intent.putExtra("asset_image", o.getImage());
                intent.putExtra("image_preview", o.getImage_preview());
                intent.putExtra("price", o.getPrice());
                intent.putExtra("symbol_currency", o.getCurrency().getSymbol());
                intent.putExtra("is_auction", o.isAuction());
                intent.putExtra("name", o.getName());
                intent.putExtra("categories", categories);
                intent.putExtra("listing_id", o.getListingId().longValue());
                intent.putExtra("currency", o.getCurrency().getCurrency());

                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                listNFT = items;
                ListNFTAdapter adapter = new ListNFTAdapter(MarketPlaceActivity.this, listNFT);
                gridView.setAdapter(adapter);

                new UpdateUI().execute(listNFT);

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
        });
    }

    protected void getListNFTMarket() {
        ChainverseSDK.getInstance().getItemOnMarket(0, 10, "");
    }

    protected class UpdateUI extends AsyncTask<ArrayList<ChainverseItemMarket>, NftProgress, Void> {
        @Override
        protected Void doInBackground(ArrayList<ChainverseItemMarket>... arg0) {
            ArrayList<ChainverseItemMarket> items = arg0[0];
            for (int i = 0; i < items.size(); i++) {
                ChainverseItemMarket item = items.get(i);
                ChainverseItemMarket nftInfo = ChainverseSDK.getInstance().getNFT(item.getNft(), item.getTokenId());

                if (nftInfo != null) {
                    NftProgress nftProgress = new NftProgress(nftInfo, i);
                    publishProgress(nftProgress);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(NftProgress... item) {
            NftProgress itemMarket = item[0];
            int i = itemMarket.index;
            ChainverseItemMarket nftInfo = itemMarket.chainverseItemMarket;
            if (nftInfo.getListingId() != BigInteger.ZERO) {
                listNFT.get(i).setName(nftInfo.getName());
                listNFT.get(i).setImage_preview(nftInfo.getImage_preview());
                listNFT.get(i).setImage(nftInfo.getImage());
                listNFT.get(i).setAttributes(nftInfo.getAttributes());
                listNFT.get(i).setPrice(nftInfo.getPrice());
                listNFT.get(i).setAuctionInfo(nftInfo.getAuctionInfo());
                listNFT.get(i).setListingId(nftInfo.getListingId());
                listNFT.get(i).setListingInfo(nftInfo.getListingInfo());
                listNFT.get(i).setAuction(nftInfo.isAuction());
            } else {
                listNFT.remove(i);
            }
            ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();

        }
    }

    class NftProgress {
        ChainverseItemMarket chainverseItemMarket;
        int index;

        public NftProgress(ChainverseItemMarket chainverseItemMarket, int index) {
            this.chainverseItemMarket = chainverseItemMarket;
            this.index = index;
        }
    }
}
