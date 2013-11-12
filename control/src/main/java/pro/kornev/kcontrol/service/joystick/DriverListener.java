package pro.kornev.kcontrol.service.joystick;

import pro.kornev.kcontrol.service.protocol.Protocol;

public interface DriverListener
{
    public void dataReceive(Protocol protocol);
    public void dataTransmit(Protocol protocol);
}
