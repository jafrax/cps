package com.report;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;

public class TestComposer extends GenericForwardComposer{

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

	}

	public void onClick$btn(Event e) throws InterruptedException{
	          Executions.getCurrent().sendRedirect("http://www.google.com", "_blank");
	}
  
  public void onClick$btn2(Event e) throws InterruptedException{
	          Clients.evalJavaScript("window.open('http://www.google.com','','top=100,left=200,height=600,width=800,scrollbars=1,resizable=1')");
	}
}

