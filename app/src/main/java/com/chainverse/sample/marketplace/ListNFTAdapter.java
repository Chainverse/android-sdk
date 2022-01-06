package com.chainverse.sample.marketplace;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chainverse.sample.R;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.model.MarketItem.Categories;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.List;

public class ListNFTAdapter extends BaseAdapter {
    private List<ChainverseItemMarket> data;
    private LayoutInflater layoutInflater;
    private Context context;
    ViewHolder holder;

    public ListNFTAdapter(Context aContext, List<ChainverseItemMarket> listData) {
        this.context = aContext;
        this.data = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_market, null);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            holder.iconToken = (ImageView) view.findViewById(R.id.iconToken);
            holder.categories = (TextView) view.findViewById(R.id.categories);
            holder.name = (TextView) view.findViewById(R.id.nameNft);
            holder.txtPrice = (TextView) view.findViewById(R.id.txtPrice);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ChainverseItemMarket item = this.data.get(i);

        setProperties(item);

        return view;
    }

    private void setProperties(ChainverseItemMarket item) {
        holder.name.setText(item.getName() + " #" + item.getTokenId());
        holder.txtPrice.setText(foo(item.getPrice()));
        setIconToken(item);
        if (item.getImage_preview() != null) {
            new DownloadImageTask(holder.thumbnail).execute(item.getImage());
        }

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
        holder.categories.setText(categories);
    }

    static class ViewHolder {
        ImageView thumbnail, iconToken;
        TextView categories, name, txtPrice;
    }

    private void setIconToken(ChainverseItemMarket item) {
        if (item.getCurrency() != null) {
            switch (item.getCurrency().getSymbol().toLowerCase()) {
                case "usdt":
                    holder.iconToken.setImageResource(R.drawable.usdt);
                    break;
                case "bnb":
                case "tbnb":
                    holder.iconToken.setImageResource(R.drawable.bnb);
                    break;
                case "cvt":
                    holder.iconToken.setImageResource(R.drawable.cvt);
                    break;
                default:
                    break;
            }
        }
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

