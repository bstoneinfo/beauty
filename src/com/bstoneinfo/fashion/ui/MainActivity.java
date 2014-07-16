package com.bstoneinfo.fashion.ui;

import java.util.ArrayList;

import android.os.Bundle;

import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSTabBarController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.R;

public class MainActivity extends BSActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExploreViewController exploreViewController = new ExploreViewController(this);
        HistroyViewController histroyViewController = new HistroyViewController(this);
        FavoriteViewController favoriteViewController = new FavoriteViewController(this);
        SettingsViewController settingsViewController = new SettingsViewController(this);

        ArrayList<BSViewController> childViewControllers = new ArrayList<BSViewController>();
        childViewControllers.add(exploreViewController);
        childViewControllers.add(histroyViewController);
        childViewControllers.add(favoriteViewController);
        childViewControllers.add(settingsViewController);

        BSViewController mainViewController = new BSTabBarController(this, R.layout.maintabbar, childViewControllers, 0);
        setMainViewController(mainViewController);
    }

    @Override
    protected void onDestroy() {
        CategoryManager.getInstance().reset();
        super.onDestroy();
    }

}
