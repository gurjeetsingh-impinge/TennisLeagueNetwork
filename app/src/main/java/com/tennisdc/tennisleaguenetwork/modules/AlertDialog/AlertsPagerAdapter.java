package com.tennisdc.tennisleaguenetwork.modules.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tennisdc.tln.R;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.NetworkImageView;
import com.tennisdc.tennisleaguenetwork.model.AlertDetail;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;

import java.util.List;
import androidx.core.content.ContextCompat;

/**
 * Created by Rajath.Akki on 12/01/18.
 */

public class AlertsPagerAdapter extends PagerAdapter {
    Context mContext;
    List<AlertDetail>alertsList;
    LayoutInflater inflater;

    public AlertsPagerAdapter(Context context, List<AlertDetail> alerts){
        this.mContext = context;
        this.alertsList = alerts;

        inflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        return this.alertsList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.frag_alert_list_item,container,false);

        TextView textView = (TextView) view.findViewById(R.id.alertTitle);
        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.alertImage);
        TextView textDescView = (TextView) view.findViewById(R.id.alertDesc);

        AlertDetail model = alertsList.get(position);

        textView.setText(Html.fromHtml(model.title));
        textDescView.setText(Html.fromHtml(model.tagDesc));

        if (!TextUtils.isEmpty(model.getBackImageUrl())) {

            Glide.with(mContext).load(model.getBackImageUrl()).placeholder(android.R.drawable.ic_menu_gallery).into(imageView);
//            imageView.setImageUrl(model.getBackImageUrl(), VolleyHelper.getInstance(this.mContext).getImageLoader());
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        view.setTag(position);

        ((ViewPager) container).addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View)object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
