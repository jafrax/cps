package com.controllers.master.gudang;

import com.tools.Libs;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.List;


@SuppressWarnings("serial")
public class InputLokasiController extends Window {

    private Logger log = LoggerFactory.getLogger(InputLokasiController.class);
    private Textbox code;
    private Textbox nama;
   
    public void onCreate() {
        initComponents();
           }

    private void initComponents() {
        code = (Textbox) getFellow("code");
        nama = (Textbox) getFellow("nama");
        
     }
    
    private void clear(){
    	code.setText("");
    	nama.setText("");
    
    }
    
    public void save(){
    boolean valid = false;
    
   	 Session s = Libs.sfDB.openSession();
     try {
    	 s.beginTransaction();
    	 
 	 	String sql = "Select kode,idbarang from " +Libs.getDbName()+".dbo.barang   order by kode asc";
    		SQLQuery query = s.createSQLQuery(sql);
    		List<Object[]> l = query.list();
    		for(Object[] o : l){
    		 String str = " insert into  "+Libs.getDbName()+".dbo.stokperlok (kd_barang,kd_lokasi,jumlah) "
    		 		+ "values ('"+((String)o[0]).trim()+"','"+code.getText()+"', 0)";
    		 s.createSQLQuery(str).executeUpdate();
             s.flush();
             s.clear();
    		}
    		
         String qry = " insert into "+Libs.getDbName()+".dbo.lokasi (kode,nama) "
         		+ "values "
         		+ "('"+code.getText()+"','"+nama.getText()+"' );";
         
         s.createSQLQuery(qry).executeUpdate();
         s.flush();
         s.clear();
         s.getTransaction().commit();
         Messagebox.show("Insert status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
         valid = true;
     } catch (Exception e) {
         log.error("save", e);
         Messagebox.show("Error status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
     } finally {
         if (s!=null && s.isOpen()) s.close();
     }
     
     if (valid) {
    	 refresh();
     } else {
         Messagebox.show("Gagal Simpan ", "Error", Messagebox.OK, Messagebox.ERROR);
     }

 }


   public void refresh() {
      clear();
   	   }
 
  
}
