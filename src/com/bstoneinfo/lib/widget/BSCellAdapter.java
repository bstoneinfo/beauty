package com.bstoneinfo.lib.widget;

public interface BSCellAdapter {

    public int getCount();

    public Object getData(int position);

    public BSViewCell createCell();
}
