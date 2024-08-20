package com.tennisdc.tln.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
import com.tennisdc.tennisleaguenetwork.model.Location;

import java.util.List;

/**
 * Created  on 22-01-2015.
 */
public class CustomSpinnerWithErrorAdapter<T> extends ArrayAdapter<T> {

    public CustomSpinnerWithErrorAdapter(Context mContext, List<T> posts) {
        super(mContext, android.R.layout.simple_spinner_dropdown_item, posts);
        setDropDownViewResource(R.layout.view_spinner_item);
    }

    public CustomSpinnerWithErrorAdapter(Context mContext, T[] posts) {
        super(mContext, android.R.layout.simple_spinner_dropdown_item, posts);
        setDropDownViewResource(R.layout.view_spinner_item);
    }

    public void setError(View v, CharSequence s) {
        TextView name = (TextView) v.findViewById(android.R.id.text1);
        name.setError(s);
        name.requestFocus();
    }

    public static class CourtsAdapter extends CustomSpinnerWithErrorAdapter<Court> {

        public CourtsAdapter(Context mContext, List<Court> courts) {
            super(mContext, courts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            ((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position).getName());

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            ((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position).getName());

            return view;
        }
    }

    public static class LocationsAdapter extends CustomSpinnerWithErrorAdapter<Location> {

        public LocationsAdapter(Context mContext, List<Location> locations) {
            super(mContext, locations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            ((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position).getName());

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            ((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position).getName());

            return view;
        }
    }

    public static class CourtRegionsAdapter extends CustomSpinnerWithErrorAdapter<CourtRegion> {

        public CourtRegionsAdapter(Context mContext, List<CourtRegion> courtRegions) {
            super(mContext, courtRegions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            ((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position).getName());

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            ((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position).getName());

            return view;
        }
    }

}
