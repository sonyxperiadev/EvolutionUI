/*
 * Copyright (C) 2014 Sony Mobile Communications AB
 *
 * This file is part of EvolutionUI.
 *
 * EvolutionUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * EvolutionUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EvolutionUI. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sonymobile.evolutionui.service.status;

import com.sonymobile.evolutionui.BasicConcept;
import com.sonymobile.evolutionui.R;
import com.sonymobile.evolutionui.service.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

public class StatusAdapter extends BaseAdapter {

    private Vector<BasicConcept> mData = new Vector<BasicConcept>();
    private Context mContext;

    public StatusAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(mContext);
            convertView = li.inflate(R.layout.item_layout, parent, false);
        }
        BasicConcept item = mData.get(position);

        ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
        Drawable d = Util.fetchIconFor(mContext, item);
        iv.setImageDrawable(d);

        TextView tv = (TextView) convertView.findViewById(R.id.text);
        tv.setText(item.getName());

        convertView.setTag(item);
        return convertView;
    }

    public void clear() {
        mData.removeAllElements();
    }

    public void add(BasicConcept item) {
        mData.add(item);
    }

    public boolean remove(BasicConcept item) {
        mData.remove(item);
        return mData.size() == 0;
    }

}
