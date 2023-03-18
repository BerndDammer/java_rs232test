
/*
 * Copyright (c) 2012, Kustaa Nyholm / SpareTimeLabs
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list 
 * of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this 
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *  
 * Neither the name of the Kustaa Nyholm or SpareTimeLabs nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package commtest;

import purejavacomm.*;

import java.io.*;

import jtermios.*;
import static jtermios.JTermios.*;

public class Example1 {
	public void sample() {
		SerialPort port = null;

		int FD = ((PureJavaSerialPort) port).getNativeFileDescriptor();

		int messageLength = 25; // bytes
		int timeout = 200; // msec
		byte[] readBuffer = new byte[messageLength];

		Termios termios = new Termios();
		
		if (0 != tcgetattr(FD, termios))
			errorHandling();

		termios.c_cc[VTIME] = (byte) (timeout / 100); // 200 msec timeout
		termios.c_cc[VMIN] = (byte) messageLength; // minimum 10 characters 
		
		if (0 != tcsetattr(FD, TCSANOW, termios))
			errorHandling();

		int n = read(FD, readBuffer, messageLength);
		if (n < 0)
			errorHandling();

	}

	public void errorHandling() {

	}

	static public void main(String[] args) {
		try {
			// Finding the port
			//String portName = "tty.usbserial-FTOXM3NX";
			String portName = "COM5";
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);

			// Opening the port
			SerialPort port = (SerialPort) portId.open("Example1", 1000);
			port.setSerialPortParams(B115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			OutputStream outStream = port.getOutputStream();
			InputStream inStream = port.getInputStream();

			// Sending data
			byte[] dataToSend = { 0x11, 0x22, 0x33, 0x44, 0x55 };
			/*
			outStream.write(dataToSend, 0, dataToSend.length);
			*/
			outStream.write(" ".getBytes());
			// Receiving data
			int messageLength = 5;
			byte[] dataReceived = new byte[messageLength];
			int received = 0;
			while (received < messageLength)
				received += inStream.read(dataReceived, received, messageLength - received);

			// Checking the message
			for (int i = 0; i < received; i++) {
				if (dataReceived[i] != dataToSend[i]) {
					System.err.println("error at " + i + "th byte, sent " + dataToSend[i] + " received " + dataReceived);
				}
			}
			System.out.println("Done");
			port.close();
		} catch (Throwable thwble) {
			thwble.printStackTrace();
		}
	}
}
