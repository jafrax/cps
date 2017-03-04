package com.controllers;


import com.tools.Libs;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.util.List;


@SuppressWarnings("serial")
public class ReadController  extends Window {

    private Logger log = LoggerFactory.getLogger(ReadController.class);
    private Listbox lb;
    private Paging pg;
    private Object[] ihm;
   
    public void onCreate() {
        initComponents();
        populate(0, pg.getPageSize());
    }

    private void initComponents() {
    	lb = (Listbox) getFellow("lb");
    	pg = (Paging) getFellow("pg");
               
        pg.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populate(evt.getActivePage() * pg.getPageSize(), pg.getPageSize());
            }
        });
        
     }
    
    private void populate(int offset, int limit){
    		lb.getItems().clear();
    		((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);
    		Session s = Libs.sfMysqlDB.openSession();
    		
		 try {
			 String sql0 = "select count(*) ";
    	   	 String sql1 = "select ID,ReceivingDateTime,SenderNumber,TextDecoded ";
    	   	 String sql2 = "FROM "+Libs.getMysqlDbName()+".inbox ORDER BY ReceivingDateTime DESC LIMIT "+offset+","+limit+" ";
    	   	 
    	   	 System.out.println("Ofsite :"+offset + " limit :"+limit );
    	   	 System.out.println(sql1+sql2);
  
    	   	 pg.setTotalSize(Libs.getPagingMysql("inbox"));
    	   	 int startNumber = (((pg.getActivePage()+1) * 20) - 20)+1;
    	   	 
    	   	 List<Object[]> l = s.createSQLQuery(sql1+sql2).list();
		   	 for (Object[] o : l) {
		   	 Listitem li = new Listitem();
             li.setValue(o);
             li.appendChild(new Listcell(""+startNumber++));
             li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
             li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
             li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
             li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
             lb.appendChild(li);
		   	 }
	   
		 } catch (Exception e) {
	         log.error("populate", e);
	     } finally {
	         if (s!=null && s.isOpen()) s.close();
	     }
    }
    
    public void refresh(){
    	populate(0, pg.getPageSize());
    }

    public void Selected() {
        if (lb.getSelectedCount()>0) {
        	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
        	}
    }
 	
	
	public void delete(){
		if (Messagebox.show("Do you want to remove this Message ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
		ihm = (lb.getSelectedItem().getValue());    
		Session s = Libs.sfMysqlDB.openSession();
		
		try {
		s.beginTransaction();
	 	String out =" delete from   "+Libs.getMysqlDbName()+".inbox   where id ='"+ Libs.nn(ihm[0]) + "' ";
	 	System.out.println(out);
        s.createSQLQuery(out).executeUpdate();
        s.getTransaction().commit();  
        s.close();
        Messagebox.show("Message has been Delete", "Information", Messagebox.OK, Messagebox.INFORMATION);
     } catch (Exception e) {
    	 s.beginTransaction().rollback();
         log.error("delete", e);
         Messagebox.show("Error status has been Delete ", "Information", Messagebox.OK, Messagebox.INFORMATION);
     } finally {
         if (s!=null && s.isOpen()) s.close();
         refresh();
     }
	}
	}
	
	
}
