package com.controllers.master;

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

import java.text.SimpleDateFormat;
import java.util.List;


@SuppressWarnings("serial")
public class EditSuplierController extends Window {

    private Logger log = LoggerFactory.getLogger(EditSuplierController.class);
    private Textbox code;
    private Textbox nama;
    private Textbox perusahaan;
    private Textbox alamat1;
    private Textbox alamat2;
    private Textbox telp;
    private Textbox kota;
    private Textbox pos;
    private Textbox fax;
    private Decimalbox tempo;
    
    private String Id;
   
    public void onCreate() {
        initComponents();
        Id = (String) getAttribute("Id");
        populate(Id);
    }
    
    
    private void populate( String kode){
         Session s = Libs.sfDB.openSession();
         try {
            
             String q1 = "select kode,nama,perusahaan,alamat1,alamat2,kota,kodepos,telp,fax,tempobyr ";
             String q2 = "from "+Libs.getDbName()+".dbo.suplier where kode = '"+kode+"' ";
            
             List<Object[]> l = s.createSQLQuery(q1 + q2).list();
             for (Object[] o : l) {
                 
            	 code.setText(Libs.nn(o[0]));
                 perusahaan.setText(Libs.nn(o[2]));
            	 nama.setText(Libs.nn(o[1]));
                 alamat1.setText(Libs.nn(o[3]));
                 alamat2.setText(Libs.nn(o[4]));
                 kota.setText(Libs.nn(o[5]));
                 pos.setText(Libs.nn(o[6]));
                 telp.setText(Libs.nn(o[7]));
                 fax.setText(Libs.nn(o[8]));
                 tempo.setText(Libs.nn(o[9]));
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
        perusahaan = (Textbox) getFellow("perusahaan");
        alamat1 = (Textbox) getFellow("alamat1");
        alamat2 = (Textbox) getFellow("alamat2");
        kota = (Textbox) getFellow("kota");
        pos = (Textbox) getFellow("pos");
        telp = (Textbox) getFellow("telp");
        fax = (Textbox) getFellow("fax");
        tempo = (Decimalbox) getFellow("tempo");
        
     }
    
    public void save(){
    	 boolean valid = false;
    	    if (tempo.getText() == ""){
    	    	tempo.setValue("0");
    		 }
    	    
    	 Session s = Libs.sfDB.openSession();
         try {
        	 s.beginTransaction();
        	 String qry = " Update "+Libs.getDbName()+".dbo.suplier SET "
             		+ " nama = '"+nama.getText()+"', perusahaan = '"+perusahaan.getText()+"',"
             		+ " alamat1= '"+alamat1.getText()+"', alamat2= '"+alamat2.getText()+"', kota= '"+kota.getText()+"', kodepos= '"+pos.getText()+"', telp= '"+telp.getText()+"', fax = '"+fax.getText()+"',"
             		+ " tempobyr = '"+tempo.getText()+"' where kode = '"+code.getText()+"' ";
             System.out.println(qry);
             s.createSQLQuery(qry).executeUpdate();
             s.flush();
             s.clear();
             s.getTransaction().commit();
             Messagebox.show("Update status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
             valid = true;
         } catch (Exception ex) {
             log.error("save", ex);
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
    	 populate(Id);
    }

}
