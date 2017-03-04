package com.controllers.master.produk;

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
public class EditSubKategoriController extends Window {

    private Logger log = LoggerFactory.getLogger(InputSubKategoriController.class);
    private Textbox kode;
    private Combobox kd_kategori;
    private Textbox nama;
    private Textbox ket1;
    private Textbox ket2;
    private String Id;
    
    public void onCreate() {
        initComponents();
        Id = (String) getAttribute("Id");
        populate(Id);
    }

    private void initComponents() {
        kode = (Textbox) getFellow("kode");
        kd_kategori = (Combobox) getFellow("kd_kategori");
        nama = (Textbox) getFellow("nama");
        ket1 = (Textbox) getFellow("ket1");
        ket2 = (Textbox) getFellow("ket2");
        
     }
    
   
    private void populate(String Id){
    	kd_kategori.getItems().clear();
    	 Session s = Libs.sfDB.openSession();
         try {
         	 String query = "select kode,kd_kategori,nama,ket1,ket2 "
         	 		+ "from "+Libs.getDbName()+".dbo.subkategori where kode ='"+Id+"' ";
              System.out.println(query);
              List<Object[]> l = s.createSQLQuery(query).list();
              for (Object[] o : l) {
              
             	  kode.setText(Libs.nn(o[0]).trim());
             	  kd_kategori.appendItem(Libs.setKategori(Libs.nn(o[1])));
                  nama.setText(Libs.nn(o[2]).trim());
                  ket1.setText(Libs.nn(o[3]).trim());
                  ket2.setText(Libs.nn(o[4]).trim());
                  
              	
                 
             }
              
           	getKategori(kd_kategori);
            
         } catch (Exception ex) {
             log.error("populate", ex);
         } finally {
             s.close();
         }
    }
     
    
    public  void getKategori(Combobox cb){
    	
    	Session s = Libs.sfDB.openSession();
  	  try{
  	    	String sql = "Select * from " +Libs.getDbName()+".dbo.kategori where tdkpakai=0  order by kode asc";
  	    	SQLQuery query = s.createSQLQuery(sql);
  	    		
  	    	List<Object[]> l = query.list();
  	    	for(Object[] o : l){
  	    		cb.appendItem(((String)o[1]).trim() + "("+ ((String) o[0]).trim() + ")");
  	    	}
  	    cb.setSelectedIndex(0);	
  	    	
  	  }catch(Exception e){
  	    		log.error("getKategori", e);
  	  }finally{
  	  	if(s != null && s.isOpen()) s.close();
  	  }
  			
    }
   
    public void save(){
    	 boolean valid = false;
    	 
    	 String kategori = kd_kategori.getSelectedItem().getLabel();
    	 kategori = kategori.substring(kategori.indexOf("(")+1, kategori.indexOf(")"));
    	 
    	 Session s = Libs.sfDB.openSession();
         try {
        	 s.beginTransaction();
             String qry = "Update "+Libs.getDbName()+".dbo.subkategori set  "
             		+ "kd_kategori = '"+ kategori +"', nama = '"+nama.getText()+"',ket1 = '"+ket1.getText()+"',ket2 = '"+ket2.getText()+"'  where kode = '" + kode.getText() +"'";
			
             s.createSQLQuery(qry).executeUpdate();
             s.flush();
             s.clear();
             s.getTransaction().commit();
             Messagebox.show("update status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
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
