package com.example.primeapp.interfaces;

import org.json.JSONObject;

public interface IPatientDataNotifier {
    void onGetStart();

    void onGetEnd(JSONObject response);

    void onGEtError();
}
