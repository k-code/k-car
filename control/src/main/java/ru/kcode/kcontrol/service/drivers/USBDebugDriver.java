package ru.kcode.kcontrol.service.drivers;

import java.io.*;

import gnu.io.NRSerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kcode.kcontrol.service.RelationsController;
import ru.kcode.kcontrol.service.Utils;
import ru.kcode.kcontrol.service.protocol.Frame;
import ru.kcode.kcontrol.service.protocol.Protocol;

public class USBDebugDriver extends DeviceDriver implements Runnable {
    private static final String NAME = "USB Driver (debug)";
    private Logger log = LoggerFactory.getLogger(USBDebugDriver.class);

    private DataOutputStream writer;
    private DataInputStream reader;
    private boolean isRun = false;

    @Override
    public synchronized void sendData(Protocol p) {
        super.sendData(p);
        if (writer == null) {
            return;
        }
        try {
            byte[] mess = p.getMess();
            writer.write(mess, 0, p.getLen());
            writer.flush();
            log.debug("Send data: {}", p.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() {
        String port = NRSerialPort.getAvailableSerialPorts().iterator().next();
        if (port == null) {
            return;
        }
        NRSerialPort serial = new NRSerialPort(port, 19200);
        serial.connect();

        writer = getWriter(serial);
        reader = getReader(serial);
        isRun = true;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void stop() {
        if (!isRun) {
            return;
        }
        try {
            isRun = false;
            writer.close();
            reader.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private DataOutputStream getWriter(NRSerialPort serial) {
        return new DataOutputStream(serial.getOutputStream());
    }

    private DataInputStream getReader(NRSerialPort serial) {
        return new DataInputStream(serial.getInputStream());
    }

    @Override
    public void run() {
        byte buf[] = new byte[Protocol.MAX_LENGTH];
        int len = 0;
        while (isRun) {
            try {
                Thread.sleep(1000);
                Protocol request = new Protocol();
                request.addParam(Frame.GET_DISTANCE, (byte)0);
                sendData(request);

                Thread.sleep(100);

                int available = reader.available();

                //mast be available control frame (4B), package length (4B) and package number (4B)
                if (available >= 12) {
                    log.debug("Available bytes: {}", available);
                    len = getPackageLength();
                    if (len <= 0) {
                        continue;
                    }
                } else {
                    continue;
                }
                len = reader.read(buf, 0, len - 8);
                Protocol p = new Protocol(buf, len);
                log.info("Received data: {}", p.toString());
                super.receiveData(p);

                for (Frame f : p.getFrames()) {
                    switch (f.getCmd()) {
                        case Frame.DISTANCE:
                            RelationsController.getDistanceViewPanel().getDistanceValue().setText(f.getIntData() + " sm");
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isRun = false;
            } catch (IndexOutOfBoundsException e) {
                System.out.println(len);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                isRun = false;
            }
        }
    }

    private int getPackageLength() throws IOException {
        byte controlFrame[] = new byte[4];
        do {
            if (reader.available() >= 8) {
                reader.read(controlFrame, 0, 4);
                if (Utils.parseInt(controlFrame, 0) == 0x55555555) {
                    reader.read(controlFrame, 0, 4);
                    return Utils.parseInt(controlFrame, 0);
                }
            } else {
                return 0;
            }
        } while (reader.available() >= 4);
        return 0;
    }
}
