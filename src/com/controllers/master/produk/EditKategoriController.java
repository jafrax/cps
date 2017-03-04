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
public class EditKategoriController extends Window {

    private Logger log = LoggerFactory.getLogger(InputKategoriController.class);
    private Textbox code;
    private Textbox nama;
    private Textbox awal_nota;
    private Decimalbox tempobyr;
    private Decimalbox tempokmsE;
    private Decimalbox tempokmsA;  
    private Decimalbox tempokmsB;
    private String Id;
    
    public void onCreate() {
        initComponents();
        Id = (String) getAttribute("Id");
        populate(Id);
        
           }

    private void initComponents() {
        code = (Textbox) getFellow("code");
        nama = (Textbox) getFellow("nama");
        awal_nota = (Textbox) getFellow("awal_nota");
        tempobyr = (Decimalbox) getFellow("tempobyr");
        tempokmsE = (Decimalbox) getFellow("tempokmsE");
        tempokmsA = (Decimalbox) getFellow("tempokmsA");
        tempokmsB = (Decimalbox) getFellow("tempokmsB");
     }
    
    
    private void populate(String Id){
        Session s = Libs.sfDB.openSession();
        try {
        	 String query = "select kode,nama,awal_nota,tempobayar,tempokomisiE,tempokomisiA,tempokomisiB "
        	 		+ "from "+Libs.getDbName()+".dbo.kategori where kode ='"+Id+"' ";
             System.out.println(query);
             List<Object[]> l = s.createSQLQuery(query).list();
             for (Object[] o : l) {
             
            	 code.setText(Libs.nn(o[0]).trim());
                 nama.setText(Libs.nn(o[1]).trim());
                 awal_nota.setText(Libs.nn(o[2]).trim());
                 tempobyr.setText(Libs.nn(o[3]).trim());
                 tempokmsE.setText(Libs.nn(o[4]).trim());
                 tempokmsA.setText(Libs.nn(o[5]).trim());
                 tempokmsB.setText(Libs.nn(o[6]).trim());
             	
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    
    }
    
    public void save(){
       
    	if (tempobyr.getText() == ""){
    	    	tempobyr.setValue("0");
    		 }
    	    if (tempokmsE.getText() == ""){
    	    	tempokmsE.setValue("0");
    		 }
    	    if (tempokmsA.getText() == ""){
    	    	tempokmsA.setValue("0");
    		 }
    	    if (tempokmsB.getText() == ""){
    	    	tempokmsB.setValue("0");
    		 }
    	    
    	 Session s = Libs.sfDB.openSession();
         try {
        	 s.beginTransaction();
        	 String qry = " update "+Libs.getDbName()+".dbo.kategori  set "
        	 		+ " nama = '"+nama.getText()+"',awal_nota = '"+awal_nota.getText()+"', tempobayar = '"+tempobyr.getText()+"', "
             		+ " tempokomisiE = '"+tempokmsE.getText()+"', tempokomisiA = '"+tempokmsA.getText()+"', tempokomisiB = '"+tempokmsB.getText()+"' "
             		+ " where kode = '"+code.getText()+"' ";
             System.out.println(qry);
             s.createSQLQuery(qry).executeUpdate();
             s.flush();
             s.clear();
             s.getTransaction().commit();
             Messagebox.show("Update status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
         } catch (Exception ex) {
             log.error("save", ex);
             Messagebox.show("Error status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
         } finally {
             if (s!=null && s.isOpen()) s.close();
         }
    }


    public void refresh() {
        populate(Id);
     	   }
    
   
}
