package com.bstoneinfo.fashion.app;

import java.util.ArrayList;

import android.os.Bundle;

import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.fashion.ui.main.CategoryViewController;
import com.bstoneinfo.fashion.ui.main.FavoriteViewController;
import com.bstoneinfo.fashion.ui.main.SettingsViewController;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSTabBarController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.R;

public class MainActivity extends BSActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CategoryViewController category51ViewController = new CategoryViewController(this, "51");
        CategoryViewController category52ViewController = new CategoryViewController(this, "52");
        FavoriteViewController favoriteViewController = new FavoriteViewController(this);
        SettingsViewController settingsViewController = new SettingsViewController(this);

        ArrayList<BSViewController> childViewControllers = new ArrayList<BSViewController>();
        childViewControllers.add(category51ViewController);
        childViewControllers.add(category52ViewController);
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
