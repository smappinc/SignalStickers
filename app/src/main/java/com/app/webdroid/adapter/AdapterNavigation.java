package com.app.webdroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.webdroid.R;
import com.app.webdroid.activity.MainActivity;
import com.app.webdroid.database.prefs.AdsPref;
import com.app.webdroid.database.prefs.SharedPref;
import com.app.webdroid.model.Navigation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.solodroid.ads.sdk.format.NativeAdViewHolder;

import java.util.List;

public class AdapterNavigation extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 0;
    private final int VIEW_AD = 1;
    private List<Navigation> items;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private int clickedItemPosition = -1;
    SharedPref sharedPref;
    AdsPref adsPref;
    public static boolean isFirstItemClicked = false;

    public interface OnItemClickListener {
        void onItemClick(View view, Navigation obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterNavigation(Context context, List<Navigation> items) {
        this.items = items;
        this.context = context;
        this.sharedPref = new SharedPref(context);
        this.adsPref = new AdsPref(context);
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView menuName;
        public ImageView menuIcon;
        public LinearLayout lytItem;
        //public RelativeLayout lytParent;
        public MaterialCardView cardView;

        public OriginalViewHolder(View v) {
            super(v);
            menuName = v.findViewById(R.id.menu_name);
            menuIcon = v.findViewById(R.id.menu_icon);
            lytItem = v.findViewById(R.id.lyt_item);
            //lytParent = v.findViewById(R.id.lyt_parent);
            cardView = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_AD) {
            View view;
            if (adsPref.getNativeAdStyleDrawerMenu().equals("small")) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_radio, parent, false);
            } else if (adsPref.getNativeAdStyleDrawerMenu().equals("medium")) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_news, parent, false);
            } else if (adsPref.getNativeAdStyleDrawerMenu().equals("large")) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            }
            vh = new NativeAdViewHolder(view);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);
            vh = new OriginalViewHolder(v);
        }
        return vh;
    }

//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);
//        return new ViewHolder(view);
//    }

    @SuppressLint({"RecyclerView", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof OriginalViewHolder) {

            final Navigation obj = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.menuName.setText(obj.name);
            Glide.with(context)
                    .load(obj.icon.replace(" ", "%20"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_home)
                    .centerCrop()
                    .into(vItem.menuIcon);


            if (sharedPref.getIsDarkTheme()) {
                vItem.cardView.setBackgroundColor(context.getResources().getColor(R.color.color_dark_navigation_drawer));
            } else {
                vItem.cardView.setBackgroundColor(context.getResources().getColor(R.color.color_light_navigation_drawer));
            }

            vItem.cardView.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, obj, position);
                    clickedItemPosition = position;
                    notifyDataSetChanged();
                    ((MainActivity) context).loadWebPage(obj.name, obj.type, obj.url, obj.url_dark);
                    if (isFirstItemClicked) {
                        ((MainActivity) context).showInterstitialAd2();
                    }
//                if (position == sharedPref.getLastItemPosition()) {
//                    Log.d("Drawer", "item already selected");
//                } else {
//                    sharedPref.setLastItemPosition(position);
//                    ((MainActivity) context).loadWebPage(obj.name, obj.type, obj.url);
//                }
                }
            });

            if (clickedItemPosition == position) {
                vItem.lytItem.setBackgroundResource(R.drawable.bg_selected_item);
                vItem.menuName.setTextColor(ContextCompat.getColor(context, R.color.color_light_primary));
                vItem.menuIcon.setColorFilter(context.getResources().getColor(R.color.color_light_primary), PorterDuff.Mode.SRC_IN);
            } else {
                vItem.lytItem.setBackgroundResource(R.drawable.bg_unselected_item);
                if (sharedPref.getIsDarkTheme()) {
                    vItem.menuName.setTextColor(ContextCompat.getColor(context, R.color.color_dark_text));
                    vItem.menuIcon.setColorFilter(context.getResources().getColor(R.color.color_dark_text), PorterDuff.Mode.SRC_IN);
                } else {
                    vItem.menuName.setTextColor(ContextCompat.getColor(context, R.color.color_light_text));
                    vItem.menuIcon.setColorFilter(context.getResources().getColor(R.color.color_light_text), PorterDuff.Mode.SRC_IN);
                }
            }

        } else if (holder instanceof NativeAdViewHolder) {

            final NativeAdViewHolder vItem = (NativeAdViewHolder) holder;

            if (adsPref.getIsNativeDrawerMenu()) {

                if (adsPref.getAdStatus()) {
                    vItem.loadNativeAd(context,
                            "1",
                            1,
                            adsPref.getMainAds(),
                            adsPref.getBackupAds(),
                            adsPref.getAdMobNativeId(),
                            adsPref.getAdManagerNativeId(),
                            adsPref.getFanNativeUnitId(),
                            adsPref.getAppLovinNativeAdManualUnitId(),
                            adsPref.getAppLovinBannerMrecZoneId(),
                            adsPref.getWortiseNativeAdUnitId(),
                            sharedPref.getIsDarkTheme(),
                            false,
                            adsPref.getNativeAdStyleDrawerMenu(),
                            R.color.color_light_native_ad_background,
                            R.color.color_dark_native_ad_background
                    );
                }

                if (sharedPref.getIsDarkTheme()) {
                    vItem.setNativeAdBackgroundResource( R.color.color_dark_native_ad_background);
                } else {
                    vItem.setNativeAdBackgroundResource(R.color.color_light_native_ad_background);
                }

                int margin = context.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);
                vItem.setNativeAdMargin(0, 0, 0, margin);

            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListData(List<Navigation> items) {
        this.items = items;
        if (adsPref.getIsNativeDrawerMenu()) {
            items.add(0, new Navigation());
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void resetListData() {
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Navigation obj = items.get(position);
        if (obj != null) {
            if (obj.name == null || obj.name.equals("")) {
                return VIEW_AD;
            }
            return VIEW_ITEM;
        } else {
            return VIEW_ITEM;
        }
    }

}