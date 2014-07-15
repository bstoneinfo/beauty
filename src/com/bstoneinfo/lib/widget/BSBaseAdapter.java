package com.bstoneinfo.lib.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BSBaseAdapter extends BaseAdapter {

    protected final Context context;
    protected final ArrayList<?> dataList;
    protected final ArrayList<BSViewCell> cellList = new ArrayList<BSViewCell>();

    public abstract BSViewCell createCell();

    public BSBaseAdapter(Context context, ArrayList<?> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<?> getDataList() {
        return dataList;
    }

    public ArrayList<BSViewCell> getCellList() {
        return cellList;
    }

    public BSViewCell getCell(int position) {
        for (BSViewCell cell : cellList) {
            if (cell.position == position) {
                return cell;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position >= 0 && position < dataList.size() ? dataList.get(position) : null;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BSViewCell cell;
        if (convertView == null) {
            cell = createCell();
            cellList.add(cell);
            convertView = cell.getRootView();
            convertView.setTag(cell);
        } else {
            cell = (BSViewCell) convertView.getTag();
        }
        cell.position = position;
        cell.loadContent(getItem(position));
        return convertView;
    }

}
