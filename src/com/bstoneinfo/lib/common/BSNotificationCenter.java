package com.bstoneinfo.lib.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

public class BSNotificationCenter {

    public static class BSNotificationEvent {
        public static final String APP_ENTER_FOREGROUND = "com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent.APP_ENTER_FOREGROUND";
        public static final String APP_ENTER_BACKGROUND = "com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent.APP_ENTER_BACKGROUND";
        public static final String LOW_MEMORY_WARNING = "com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent.LOW_MEMORY_WARNING";
        public static final String REMOTE_CONFIG_DID_CHANGE = "com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent.REMOTE_CONFIG_DID_CHANGE";

    }

    private class BSNotificationObservable extends Observable {
        @Override
        public void notifyObservers(Object data) {
            setChanged();
            super.notifyObservers(data);
        }
    }

    private final HashMap<String, BSNotificationObservable> mapEventObservable = new HashMap<String, BSNotificationObservable>();
    private final HashMap<Object, ArrayList<Observer>> mapOwnerObservers = new HashMap<Object, ArrayList<Observer>>();

    public void addObserver(Object owner, String event, Observer observer) {
        if (observer == null) {
            return;
        }

        BSUtils.debugAssert(!(owner instanceof Observer), "owner can't be a Observer instance.");

        BSNotificationObservable observable;
        synchronized (mapEventObservable) {
            observable = mapEventObservable.get(event);
            if (observable == null) {
                observable = new BSNotificationObservable();
                mapEventObservable.put(event, observable);
            }
        }
        observable.addObserver(observer);

        ArrayList<Observer> observers;
        synchronized (mapOwnerObservers) {
            observers = mapOwnerObservers.get(owner);
            if (observers == null) {
                observers = new ArrayList<Observer>();
                mapOwnerObservers.put(owner, observers);
            }
        }
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        synchronized (mapEventObservable) {
            Iterator<HashMap.Entry<String, BSNotificationObservable>> it = mapEventObservable.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, BSNotificationObservable> entry = it.next();
                BSNotificationObservable observable = entry.getValue();
                observable.deleteObserver(observer);
                if (observable.countObservers() == 0) {
                    it.remove();
                }
            }
        }
        synchronized (mapOwnerObservers) {
            Iterator<HashMap.Entry<Object, ArrayList<Observer>>> it = mapOwnerObservers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, ArrayList<Observer>> entry = it.next();
                ArrayList<Observer> observers = entry.getValue();
                observers.remove(observer);
                if (observers.isEmpty()) {
                    it.remove();
                }
            }
        }
    }

    public void removeObservers(Object owner, String event) {
        BSUtils.debugAssert(!(owner instanceof Observer), "owner can't be a Observer instance.");
        synchronized (mapEventObservable) {
            BSNotificationObservable eventObservable = mapEventObservable.get(event);
            if (eventObservable == null) {
                return;
            }
            synchronized (mapOwnerObservers) {
                ArrayList<Observer> ownerObservers = mapOwnerObservers.get(owner);
                if (ownerObservers != null) {
                    Iterator<Observer> it = ownerObservers.iterator();
                    while (it.hasNext()) {
                        Observer observer = it.next();
                        int count = eventObservable.countObservers();
                        eventObservable.deleteObserver(observer);
                        if (eventObservable.countObservers() < count) {//有删除
                            it.remove();
                            if (eventObservable.countObservers() == 0) {
                                mapEventObservable.remove(event);
                                break;
                            }
                        }
                    }
                    if (ownerObservers.isEmpty()) {
                        mapOwnerObservers.remove(owner);
                    }
                }
            }
        }
    }

    public void removeObservers(Object owner) {
        BSUtils.debugAssert(!(owner instanceof Observer), "owner can't be a Observer instance.");
        ArrayList<Observer> observersToDelete = null;
        synchronized (mapOwnerObservers) {
            observersToDelete = mapOwnerObservers.remove(owner);
        }
        if (observersToDelete != null) {
            synchronized (mapEventObservable) {
                Iterator<Entry<String, BSNotificationObservable>> it = mapEventObservable.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, BSNotificationObservable> entry = it.next();
                    BSNotificationObservable observable = entry.getValue();
                    for (Observer observer : observersToDelete) {
                        observable.deleteObserver(observer);
                    }
                    if (observable.countObservers() == 0) {
                        it.remove();
                    }
                }
            }
            observersToDelete.clear();
        }
    }

    public void notifyOnUIThread(String event) {
        notifyOnUIThread(event, null);
    }

    public void notifyOnUIThread(String event, final Object data) {
        BSLog.d(event + " " + data);
        final BSNotificationObservable observable;
        synchronized (mapEventObservable) {
            observable = mapEventObservable.get(event);
        }
        if (observable != null) {
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    observable.notifyObservers(data);
                }
            };
            BSUtils.runOnUiThread(action);
        }
    }

}
