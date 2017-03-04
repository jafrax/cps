package com.controllers.master;


import com.pojo.ClientPOJO;
import com.pojo.MemberPOJO;
import com.pojo.ProductPOJO;
import com.tools.Libs;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.List;


@SuppressWarnings("serial")
public class EditSalesController extends Window {

    private Logger log = LoggerFactory.getLogger(InputSalesController.class);
    private Textbox code;
    private Textbox nama;
    private Textbox alamat;
    private Textbox telp;
    private Textbox kota;
    private Textbox kelsales;
    
    private String SalesId ;  

    public void onCreate() {
    	initComponents();
        SalesId = (String) getAttribute("SalesId");
        System.out.println( SalesId);
        populate(SalesId);
    }

    private void populate(String SalesId) {
        Session s = Libs.sfDB.openSession();
        try {
            String query = "select kode,nama,alamat,kota,telp,kel_sls from "+Libs.getDbName()+".dbo.sales where kode = '"+SalesId+"' ";
            
            List<Object[]> l = s.createSQLQuery(query).list();
            for (Object[] o : l) {
            	
            	code.setText(SalesId);
            	nama.setText(Libs.nn(o[1]).trim());
            	alamat.setText(Libs.nn(o[2]).trim());
            	kota.setText(Libs.nn(o[3]).trim());
            	telp.setText(Libs.nn(o[4]).trim());
            	kelsales.setText(Libs.nn(o[5]).trim());
            
            	
            	
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }
    
    private void initComponents() {
        code = (Textbox) getFellow("code");
        nama = (Textbox) getFellow("nama");
        alamat = (Textbox) getFellow("alamat");
        kota = (Textbox) getFellow("kota");
        telp = (Textbox) getFellow("telp");
        kelsales = (Textbox) getFellow("kelsales");
        
     }
    
    
    public void save(){
    boolean valid = false;  
   	 Session s = Libs.sfDB.openSession();
     try {
    	 s.beginTransaction();
         String qry = "update "+Libs.getDbName()+".dbo.sales  set nama = '"+nama.getText()+"', alamat = '"+alamat.getText()+"',"
         		+ " kota ='"+kota.getText()+"',telp = '"+telp.getText()+"',kel_sls = '"+kelsales.getText()+"'  where kode = '"+code.getText()+"' ";
         s.createSQLQuery(qry).executeUpdate();
         s.flush();
         s.clear();
         s.getTransaction().commit();
         Messagebox.show("Update status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
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
      populate(SalesId);
   	   }
 
  
}
