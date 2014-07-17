package com.bstoneinfo.lib.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

public abstract class BSGridAdapter extends BSBaseAdapter {

    protected final int numColumns;
    protected final int itemWidth, itemHeight, horzSpacing, vertSpacing;

    public BSGridAdapter(Context context, ArrayList<?> dataList, int numColumns, int itemWidth, int itemHeight, int horzSpacing, int vertSpacing) {
        super(context, dataList);
        this.numColumns = numColumns;
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.horzSpacing = horzSpacing;
        this.vertSpacing = vertSpacing;
    }

    @Override
    public int getCount() {
        return dataList.size() % numColumns == 0 ? dataList.size() / numColumns : dataList.size() / numColumns + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LinearLayout layout = new LinearLayout(context);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            layout.setBackgroundColor(Color.WHITE);
            layout.setLayoutParams(params);
            layout.setPadding(horzSpacing, 0, 0, vertSpacing);

            holder.itemview = new View[numColumns];
            for (int i = 0; i < numColumns; i++) {
                BSViewCell cell = createCell();
                cellList.add(cell);
                cell.position = position * numColumns + i;
                holder.itemview[i] = cell.getRootView();
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(itemWidth, itemHeight);
                itemParams.setMargins(0, 0, horzSpacing, 0);
                layout.addView(holder.itemview[i], itemParams);
                holder.itemview[i].setTag(cell);
            }
            convertView = layout;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.rowNo = position;

        for (int i = 0; i < numColumns; i++) {
            final int index = position * numColumns + i;
            View view = holder.itemview[i];
            if (index < dataList.size()) {
                view.setVisibility(View.VISIBLE);
                BSViewCell cell = (BSViewCell) view.getTag();
                cell.position = position * numColumns + i;
                if (cell != null) {
                    cell.loadContent(getItem(index));
                }
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        int rowNo;
        View itemview[];
    }

}
