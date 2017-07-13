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

import java.text.SimpleDateFormat;
import java.util.List;

@SuppressWarnings("serial")
public class InputKomisiController extends Window {

    private Logger log = LoggerFactory.getLogger(InputKomisiController.class);
    private Combobox code;
    private Combobox kategori;
    private Decimalbox komisi;
    
    private Decimalbox kom1;
    private Decimalbox kom2;
    private Decimalbox kom3;
    private Decimalbox kom4;
    private Decimalbox kom5;
    private Decimalbox kom6;

    
    public void onCreate() {
        initComponents();
        
        Libs.getSales(code);
        Libs.getKategori(kategori);
        
        
           }

    private void initComponents() {
        code = (Combobox) getFellow("code");
        kategori = (Combobox) getFellow("kategori");
       
        komisi = (Decimalbox) getFellow("komisi");
        kom1 = (Decimalbox) getFellow("kom1");
        kom2 = (Decimalbox) getFellow("kom2");
        kom3 = (Decimalbox) getFellow("kom3");
        kom4 = (Decimalbox) getFellow("kom4");
        kom5 = (Decimalbox) getFellow("kom5");
        kom6 = (Decimalbox) getFellow("kom6");
        
     }
    
    private void validasi() {
    	
    	
    	
    	 if (komisi.getText() == ""){
    		 komisi.setValue("0");
    	 }
    	 if (kom1.getText() == ""){
    		 kom1.setValue("0");
    	 }
    	 if (kom2.getText() == ""){
    		 kom2.setValue("0");
    	 }
    	 if (kom3.getText() == ""){
    		 kom3.setValue("0");
    	 }
    	 if (kom4.getText() == ""){
    		 kom4.setValue("0");
    	 }
    	 if (kom5.getText() == ""){
    		 kom5.setValue("0");
    	 }
    	 if (kom6.getText() == ""){
    		 kom6.setValue("0");
    	 }
        
     }
    
     
   
    public void save(){
    	 boolean valid = false;
    	 validasi();
    	 
    	 String kd_sales = code.getSelectedItem().getLabel();
    	 kd_sales = kd_sales.substring(kd_sales.indexOf("(")+1, kd_sales.indexOf(")"));
    	 
    	 String kd_kategori = kategori.getSelectedItem().getLabel();
    	 kd_kategori = kd_kategori.substring(kd_kategori.indexOf("(")+1, kd_kategori.indexOf(")"));
    	 
    	 Session s = Libs.sfDB.openSession();
         try {
        	 s.beginTransaction();
             String qry = " insert into "+Libs.getDbName()+".dbo.komisi "
             		+ "(kd_sales,kd_kategori,komisi,"
             		+ "kom1,kom2,kom3,kom4,kom5,kom6,leuser,ledate )"
             		+ " values ("
             		+ "'" + kd_sales +"','"+ kd_kategori +"','"+komisi.getText()+"','"+kom1.getText()+"' ,'"+kom2.getText()+"',"
             		+ "'"+kom3.getText()+"','"+kom4.getText()+"','"+kom5.getText()+"','"+kom6.getText()+"', "
             		+ "'user', GETDATE() );";
			 //Messagebox.show(qry);
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
    	 code.setText("");
         kategori.setText("");
         komisi.setText("");
         kom1.setText("");
         kom2.setText("");
         kom3.setText("");
         kom4.setText("");
         kom5.setText("");
         kom6.setText("");
        
    }

    public void refresh() {
       clear();
    	   }
    
    public void SalesSelected(){
    	
    }
    
    public void KategoriSelected(){
    	
    }
    
  
}
