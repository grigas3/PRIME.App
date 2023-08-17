package com.example.primeapp.login;

import com.example.primeapp.interfaces.IDataPostNotifier;

public class TestNotifier implements IDataPostNotifier {

    public boolean isSuccess() {
        return success;
    }

    boolean success = false;

    @Override
    public void onPostStart() {

    }

    @Override
    public void onPostEnd() {
        success = true;
    }

    @Override
    public void onPostError() {

    }
}
