package com.controllers;

import org.hibernate.Session;  
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;


@SuppressWarnings("serial")
public class SendController  extends Window {

    private Logger log = LoggerFactory.getLogger(SendController.class);
    private Listbox lb;
    private Paging pg;
    private Textbox nomor;
    private Textbox text;
    private Long id;
    private Object[] ihm;
    private int memoHeight = 6;

	public void onCreate() {
        initComponents();
        populate(0, pg.getPageSize());
    }

    private void initComponents() {
    	lb = (Listbox) getFellow("lb");
    	pg = (Paging) getFellow("pg");
        nomor = (Textbox) getFellow("nomor");
        text = (Textbox) getFellow("text");
        
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
    	   	 String sql1 = "SELECT ID,SendingDateTime,DestinationNumber,Status,TextDecoded ";
    	   	 String sql2 = "FROM "+Libs.getMysqlDbName()+".sentitems  ORDER BY SendingDateTime DESC LIMIT "+offset+","+limit+" ";
    	   	 
    	   	 //System.out.println("Ofsite :"+offset + " limit :"+limit );
    	   	 System.out.println(sql1+sql2);
  
    	   	 pg.setTotalSize(Libs.getPagingMysql("sentitems"));
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
             li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
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
    
    private boolean validate() {
        boolean valid = true;

        if (nomor.getValue()=="") {
            valid = Libs.showErrorForm("Please insert number !!");
        }else if (text.getValue() == ""){
        	valid = Libs.showErrorForm("Please insert Message text !!");
        }

        return valid;
    }
    
    public void spliter(String sms){
	    Scanner keyboard = new Scanner(System.in);
	    String contoh1 = keyboard.nextLine();
	    String[] empatkata = contoh1.split("(?<=\\G.{4})");
	    for (String r:empatkata)
	    {
	    System.out.println(r);
	    }
    }

    
 	public void send(){
	
 		if (validate()) {
			Session s = Libs.sfMysqlDB.openSession();

		try {
				s.beginTransaction();
				
				String sms = text.getText();
				String[] pesan = sms.split("(?<=\\G.{160})");
				System.out.println("-split sms 160 char-");
				
				for (int counter=0; counter < pesan.length; counter++){
					System.out.println(pesan[counter]);
					System.out.println(counter);
					
					String out =" INSERT INTO  "+Libs.getMysqlDbName()+".outbox (DestinationNumber,TextDecoded,CreatorID) "
				 			+ "VALUES ('"+nomor.getText()+"','"+pesan[counter]+"','1') ";
			        s.createSQLQuery(out).executeUpdate();
				}
				
			    s.getTransaction().commit();  
		        s.close();
		        Messagebox.show("Message has been send", "Information", Messagebox.OK, Messagebox.INFORMATION);
		        nomor.setText("");
		    	text.setText("");
	     } catch (Exception e) {
	    	 s.beginTransaction().rollback();
	         log.error("send", e);
	         Messagebox.show("Error status has been Send ", "Information", Messagebox.OK, Messagebox.INFORMATION);
	     } finally {
	         if (s!=null && s.isOpen()) s.close();
	         refresh();
	     }
	}
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
		 	String out =" delete from   "+Libs.getMysqlDbName()+".sentitems   where id ='"+ Libs.nn(ihm[0]) + "' ";
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
