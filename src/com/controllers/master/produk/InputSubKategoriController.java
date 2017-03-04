package com.controllers.master.produk;

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
public class InputSubKategoriController extends Window {

    private Logger log = LoggerFactory.getLogger(InputSubKategoriController.class);
    private Textbox kode;
    private Combobox kd_kategori;
    private Textbox nama;
    private Textbox ket1;
    private Textbox ket2;
   
    
    public void onCreate() {
        initComponents();
        Libs.getKategori(kd_kategori);
           }

    private void initComponents() {
        kode = (Textbox) getFellow("kode");
        kd_kategori = (Combobox) getFellow("kd_kategori");
        nama = (Textbox) getFellow("nama");
        ket1 = (Textbox) getFellow("ket1");
        ket2 = (Textbox) getFellow("ket2");
     }
    
   
     
   
    public void save(){
    	 boolean valid = false;
    	 
    	 String kategori = kd_kategori.getSelectedItem().getLabel();
    	 kategori = kategori.substring(kategori.indexOf("(")+1, kategori.indexOf(")"));
    	 
    	 Session s = Libs.sfDB.openSession();
         try {
        	 s.beginTransaction();
             String qry = "insert into "+Libs.getDbName()+".dbo.subkategori "
             		+ "(kode,kd_kategori,nama,ket1,ket2 )  values "
             		+ "('" + kode.getText() +"','"+ kategori +"','"+nama.getText()+"','"+ket1.getText()+"','"+ket2.getText()+"' )";
			
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
    
    
    private void clear(){
    	 kode.setText("");
         kd_kategori.setText("");
         nama.setText("");
         ket1.setText("");
         ket2.setText("");
         
    }

    public void refresh() {
       clear();
    	   }
    
    public void SalesSelected(){
    	
    }
    
    public void KategoriSelected(){
    	
    }
    
  
}
