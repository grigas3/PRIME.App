package com.example.primeapp.interfaces;

import com.example.primeapp.algos.core.Signals.NamedSignalCollection;

public interface IDataCollector {


    void Start();
    void Stop();
    NamedSignalCollection GetData();
}
