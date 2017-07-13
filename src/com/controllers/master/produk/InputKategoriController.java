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
public class InputKategoriController extends Window {

    private Logger log = LoggerFactory.getLogger(InputKategoriController.class);
    private Textbox code;
    private Textbox nama;
    private Textbox awal_nota;
    private Decimalbox tempobyr;
    private Decimalbox tempokmsE;
    private Decimalbox tempokmsA;  
    private Decimalbox tempokmsB;
    public void onCreate() {
        initComponents();
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
    
    public void save(){
       
    	 boolean valid = false;
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
        	 String qry = " insert into "+Libs.getDbName()+".dbo.kategori (kode,nama,awal_nota,tempobayar,tempokomisiE,tempokomisiA,tempokomisiB) values "
             		+ "('"+code.getText()+"','"+nama.getText()+"','"+awal_nota.getText()+"','"+tempobyr.getText()+"' ,'"+tempokmsE.getText()+"','"+tempokmsA.getText()+"','"+tempokmsB.getText()+"')";
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
         awal_nota.setText("");
         tempobyr.setText("");
         tempokmsE.setText("");
         tempokmsA.setText("");
         tempokmsB.setText("");
    }
    
}
