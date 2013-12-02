package pro.kornev.kcar.cop.services.support;

import pro.kornev.kcar.cop.services.support.IWakeUpCallback;

interface IWakeUpBinder {
    boolean isRunning();
    void setCallback(IWakeUpCallback callback);
}