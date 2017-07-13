package com.controllers;

import org.smslib.*;
import java.util.*;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.Library;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

public class ReadMessages {
//
//	static CService srv;
//	public static LinkedList receiveMessage()
//	{
//	LinkedList msgList = new LinkedList();
//	/*
//	To Check COM port Go in following path in Windows7
//	Control Panel\Hardware and Sound\Bluetooth and Local COM
//	 
//	*/
//	srv = new CService("COM4",9600,"huawei","E220");//"COM1", 57600, "Nokia", ""
//	try
//	{
//	srv.setSimPin("0000");
//	srv.setSimPin2("0000");
//	srv.connect();
//	srv.readMessages(msgList, CIncomingMessage.MessageClass.Unread);
//	srv.disconnect();
//	return msgList;
//	}
//	catch (Exception e)
//	{
//	e.printStackTrace();
//	}
//	System.exit(0);
//	return msgList;
//	}
}
