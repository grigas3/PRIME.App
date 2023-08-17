package com.example.primeapp.interfaces;

public interface IDataShink {
    /***
     * Register Listener
     * @param listener
     */
    void registerListener(IDataListener listener);

    /***
     * Unregister listenener
     * @param listener
     */
    void unregisterListener(IDataListener listener);

}
