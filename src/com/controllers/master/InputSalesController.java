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
public class InputSalesController extends Window {

    private Logger log = LoggerFactory.getLogger(InputSalesController.class);
    private Textbox code;
    private Textbox nama;
    private Textbox alamat;
    private Textbox telp;
    private Textbox kota;
//    private Decimalbox komisi;
    private Textbox kelsales;
      

    public void onCreate() {
        initComponents();
           }

    private void initComponents() {
        code = (Textbox) getFellow("code");
        nama = (Textbox) getFellow("nama");
        alamat = (Textbox) getFellow("alamat");
        kota = (Textbox) getFellow("kota");
        telp = (Textbox) getFellow("telp");
//        komisi = (Decimalbox) getFellow("komisi");
        kelsales = (Textbox) getFellow("kelsales");
        
     }
    
    private void clear(){
    	code.setText("");
    	nama.setText("");
    	alamat.setText("");
    	kota.setText("");
    	telp.setText("");
//    	komisi.setText("");
    	kelsales.setText("");
    }
    
    public void save(){
    boolean valid = false;
//    if (komisi.getText() == ""){
//    	komisi.setValue("0");
//	 }
    
   	 Session s = Libs.sfDB.openSession();
     try {
    	 s.beginTransaction();
         String qry = " insert into "+Libs.getDbName()+".dbo.sales (kode,nama,alamat,kota,telp ," 
         		+ "kel_sls,ledate ,leuser,	tdkpakai ,data_ts ,prnt_ts ) "
         		+ "values "
         		+ "('"+code.getText()+"','"+nama.getText()+"','"+alamat.getText()+"',"
         		+ "'"+kota.getText()+"','"+telp.getText()+"','"+kelsales.getText()+"',"
         		+ "getdate(),'user',0,0,0);";
//         Messagebox.show(qry);
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
