package commtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;
import purejavacomm.UnsupportedCommOperationException;

public class CommTest1 implements SerialPortEventListener {
	static Enumeration<CommPortIdentifier> portList;
	static CommPortIdentifier portId;
	static String messageString = " ";
	static SerialPort serialPort;
	static OutputStream outputStream;
	static InputStream inputStream;

	public static void main(String[] args) {
		new CommTest1();
	}

	private CommTest1() {
		scanPortList();
		open();
		write();
		try {
			Thread.sleep(Duration.ofMillis(5000));
			System.out.println("inputStream.available()   " + inputStream.available());
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void scanPortList() {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			System.out.println("portId :  " + portId);
			System.out.println("portId Name:  " + portId.getName());
		}
	}

	private void open() {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM5")) {
					{
						try {
							serialPort = (SerialPort) portId.open("Test1", 2000);

							serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
									SerialPort.PARITY_NONE);
							serialPort.notifyOnDataAvailable(true);
							serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

							serialPort.addEventListener(this);

							//serialPort.enableReceiveThreshold(3);
							//serialPort.setInputBufferSize(300);
							
							outputStream = serialPort.getOutputStream();
							inputStream = serialPort.getInputStream();

							System.out.println("Prep Done");
							System.out.println("Baudrate " + serialPort.getBaudRate());

						} catch (PortInUseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (TooManyListenersException e) {
							e.printStackTrace();
						} catch (UnsupportedCommOperationException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void write() {
		try {
			outputStream.write(messageString.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		System.out.println("EEEEEEEEEEEEEEEE VENT :  " + event);
		if (event.getEventType() == event.DATA_AVAILABLE) {
			try {
				while (inputStream.available() > 0) {
					System.out.write(inputStream.read());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("OOPS :  " + event);
		}
	}
}