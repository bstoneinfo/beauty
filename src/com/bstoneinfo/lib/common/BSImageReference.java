package com.bstoneinfo.lib.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.graphics.Bitmap;

public class BSImageReference {

    private HashMap<Object, HashMap<Bitmap, Integer>> mapOwner = new HashMap<Object, HashMap<Bitmap, Integer>>();

    /**
     * 将bitmap对象的引用计数加1
     */
    public void retain(Bitmap bitmap) {
        retain(this, bitmap);
    }

    /**
     * 将bitmap对象的引用计数减1，减为0时recycle释放 如果bitmap为null则直接返回
     */
    public void release(Bitmap bitmap) {
        release(this, bitmap);
    }

    /**
     * 设置owner引用bitmap对象，并将bitmap对象的引用计数加1。如果owner或bitmap为null则直接返回null值
     * 
     * @param owner 表示bitmap由owner占用
     * @param bitmap
     * @return bitmap
     */
    public void retain(Object owner, Bitmap bitmap) {

        if (owner == null || bitmap == null) {
            return;
        }

        synchronized (mapOwner) {
            HashMap<Bitmap, Integer> map = mapOwner.get(owner);
            if (map == null) {
                map = new HashMap<Bitmap, Integer>();
                mapOwner.put(owner, map);
            }
            Integer counter = map.get(bitmap);
            if (counter == null) {
                map.put(bitmap, 1);
            } else {
                map.put(bitmap, counter.intValue() + 1);
            }
            s_retain(bitmap);
        }
    }

    /**
     * 将owner占用的bitmap对象的引用计数减1，减为0时recycle释放
     * 
     * @param owner
     */

    public void release(Object owner, Bitmap bitmap) {

        if (owner == null || bitmap == null) {
            return;
        }

        synchronized (mapOwner) {
            HashMap<Bitmap, Integer> map = mapOwner.get(owner);
            if (map != null) {
                Integer counter = map.get(bitmap);
                if (counter != null) {
                    if (counter == 1) {
                        map.remove(bitmap);
                    } else {
                        map.put(bitmap, counter - 1);
                    }
                    s_release(bitmap);
                }
                if (map.isEmpty()) {
                    mapOwner.remove(owner);
                }
            }
        }
    }

    public void release(Object owner) {

        synchronized (mapOwner) {
            HashMap<Bitmap, Integer> map = mapOwner.remove(owner);
            if (map != null) {
                releaseMap(map);
            }
        }
    }

    /**
     * 清除本对象所管理的全部owner对象引用的bitmap引用计数,即放弃本对象中所有owner对象对bitmap的引用
     */
    public void release() {
        synchronized (mapOwner) {
            Iterator<Entry<Object, HashMap<Bitmap, Integer>>> iter = mapOwner.entrySet().iterator();
            while (iter.hasNext()) {
                HashMap.Entry<Object, HashMap<Bitmap, Integer>> entry = iter.next();
                releaseMap(entry.getValue());
            }
            mapOwner.clear();
        }
    }

    private void releaseMap(HashMap<Bitmap, Integer> map) {
        if (map != null) {
            Iterator<Entry<Bitmap, Integer>> iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                HashMap.Entry<Bitmap, Integer> entry = iter.next();
                Integer counter = entry.getValue();
                if (counter != null) {
                    s_release(entry.getKey(), counter);
                }
            }
            map.clear();
        }
    }

    private static HashMap<Bitmap, Integer> mapCounter;

    private static synchronized HashMap<Bitmap, Integer> getCounterMap() {
        if (mapCounter == null) {
            mapCounter = new HashMap<Bitmap, Integer>();
        }
        return mapCounter;
    }

    /**
     * 将bitmap对象的引用计数加1
     */
    static private void s_retain(Bitmap bitmap) {
        s_retain(bitmap, 1);
    }

    static private void s_retain(Bitmap bitmap, int addCount) {
        if (bitmap != null) {
            HashMap<Bitmap, Integer> mapCounter = getCounterMap();
            synchronized (mapCounter) {
                Integer counter = mapCounter.get(bitmap);
                if (counter == null) {
                    mapCounter.put(bitmap, addCount);
                } else {
                    mapCounter.put(bitmap, counter.intValue() + addCount);
                }
            }
        }
    }

    /**
     * 将bitmap对象的引用计数减1，减为0时recycle释放
     */
    static private void s_release(Bitmap bitmap) {
        s_release(bitmap, 1);
    }

    static private void s_release(Bitmap bitmap, int subCount) {

        if (bitmap == null) {
            return;
        }

        HashMap<Bitmap, Integer> mapCounter = getCounterMap();
        synchronized (mapCounter) {
            Integer counter = mapCounter.get(bitmap);
            if (counter != null) {
                if (counter <= subCount) {
                    mapCounter.remove(bitmap);
                    bitmap.recycle();
                    bitmap = null;
                } else {
                    mapCounter.put(bitmap, counter - subCount);
                }
            }
        }

    }

}
