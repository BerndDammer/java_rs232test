package commtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class CommTest2 implements SerialPortDataListener {
	static SerialPort[] portList;
	static SerialPort portId;
	static String messageString = " ";
	static OutputStream outputStream;
	static InputStream inputStream;

	public static void main(String[] args) {
		new CommTest2();
	}

	private CommTest2() {
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
		portId.closePort();
	}

	private void scanPortList() {
		portList = SerialPort.getCommPorts();

		for (SerialPort sp : portList) {
			System.out.println("portId :  " + sp);
			System.out.println("portId Name:  " + sp.getDescriptivePortName());
			System.out.println("portId Name:  " + sp.getSystemPortName());
		}
	}

	private void open() {
		portList = SerialPort.getCommPorts();

		for (SerialPort serialPort : portList) {
			if (serialPort.getSystemPortName().equals("COM5")) {
				portId = serialPort;

				serialPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
				serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

				serialPort.addDataListener(this);
				outputStream = serialPort.getOutputStream();
				inputStream = serialPort.getInputStream();

				serialPort.openPort();

				System.out.println("Prep Done");
				System.out.println("Baudrate " + serialPort.getBaudRate());
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
	public int getListeningEvents() {
		int result = 0;
		result |= SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
		result |= SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		return result;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		System.out.println("\nEEEEEEEEEEEEEEEE VENT : " + event + "\n" );

		System.out.print(new String(event.getReceivedData()));
	}
}