package com.controllers;

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
public class InputClientController extends Window {

    private Logger log = LoggerFactory.getLogger(InputClientController.class);
    private Textbox code;
    private Textbox nama;
    private Textbox perusahaan;
    private Textbox alamat1;
    private Textbox alamat2;
    private Textbox telp;
    private Textbox hp;
    private Textbox kota;
    private Textbox pos;
    private Textbox fax;
    private Textbox sls;
    private Textbox ket;
    private Decimalbox plafon;
      

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
        hp = (Textbox) getFellow("hp");
        fax = (Textbox) getFellow("fax");
        sls = (Textbox) getFellow("sls");
        ket = (Textbox) getFellow("ket");
        plafon = (Decimalbox) getFellow("plafon");
        
     }
    
    public void save(){
    	 boolean valid = false;
    	 if (plafon.getText() == ""){
    		 plafon.setValue("0");
    	 }

    	 //System.out.println(Str.toUpperCase() );
    	 Session s = Libs.sfDB.openSession();
         try {
        	 s.beginTransaction();
             String qry = " insert into "+Libs.getDbName()+".dbo.customer "
             		+ "(kode,nama,perusahaan,alamat1,alamat2,"
             		+ "kota,kodepos,telp,hp,fax,kd_sales,ketharga,"
             		+ "plafon,bpt_kas,bpt_cg,bpt_bank,"
             		+ "createdate,createuser,modifdate,modifuser,flg)"
             		+ " values ("
             		+ "'" +code.getText()+"','"+nama.getText()+"','"+perusahaan.getText()+"','"+alamat1.getText()+"' ,'"+alamat2.getText()+"',"
             		+ "'"+kota.getText()+"','"+pos.getText()+"','"+telp.getText()+"','"+hp.getText()+"','"+fax.getText()+"','"+sls.getText()+"','normal',"
             		+ "'"+plafon.getText()+"',0,0,0,"
             		+ " GETDATE(),'user','','','0' "
					+");";
             
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
             Messagebox.show("Gagal menyimpan ", "Error", Messagebox.OK, Messagebox.ERROR);
         }
         
         
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
         hp.setText("");
         fax.setText("");
         sls.setText("");
         ket.setText("");
         plafon.setText("");
    }

    public void refresh() {
       clear();
    	   }
  
}
