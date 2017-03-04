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
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.List;


@SuppressWarnings("serial")
public class InputSuplierController extends Window {

    private Logger log = LoggerFactory.getLogger(InputSuplierController.class);
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
      

    public void onCreate() {
        initComponents();
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
        	 String qry = " insert into "+Libs.getDbName()+".dbo.suplier (kode,nama,perusahaan,"
             		+ "alamat1,alamat2,kota,kodepos,telp,fax,tempobyr) "
             		+ "values "
             		+ "('"+code.getText()+"','"+nama.getText()+"','"+perusahaan.getText()+"','"+alamat1.getText()+"','"+alamat2.getText()+"', "
             		+ "'"+kota.getText()+"','"+pos.getText()+"','"+telp.getText()+"','"+fax.getText()+"','"+tempo.getText()+"')";
             System.out.println(qry);
             s.createSQLQuery(qry).executeUpdate();
             s.flush();
             s.clear();
             s.getTransaction().commit();
             Messagebox.show("Insert status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
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
        clear();
     	   }
    
  
    
    
    private void clear(){
    	 code.setText("");
         nama.setText("");
         perusahaan.setText("");
         alamat1.setText("");
         alamat2.setText("");
         kota.setText("");
         pos.setText("");
         telp.setText("");
         fax.setText("");
         tempo.setText("");
    }
    
}
