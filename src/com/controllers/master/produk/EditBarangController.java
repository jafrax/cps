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
public class EditBarangController extends Window {

    private Logger log = LoggerFactory.getLogger(EditBarangController.class);
    private Textbox code;
    private Textbox idbarang;
    private Textbox nama;
    private Textbox merkbrg;
    private Textbox tipebrg;
    private Combobox merk;
    private Combobox subkategori;
    private Textbox kategori;
    private Combobox satuan;
    private Textbox hargaA;
    private Textbox hargaB;
    private Textbox hargaE;
    private Textbox ukuran;
    private Textbox warna; 
    private Textbox hpp;
    private Textbox ket1;
    private Textbox ket2;  
    private String Id;

    public void onCreate() {
        initComponents();
        Libs.getmerk(merk);
        Libs.getsubkategori(subkategori);
        Libs.getsatuan(satuan);
        
        Id = (String) getAttribute("Id");
        System.out.println(Id);
        populate(Id);
        
           }

    private void initComponents() {
        code = (Textbox) getFellow("code");
        idbarang = (Textbox) getFellow("idbarang");
        nama = (Textbox) getFellow("nama");
        merkbrg = (Textbox) getFellow("merkbrg");
        tipebrg = (Textbox) getFellow("tipebrg");
        merk = (Combobox) getFellow("merk");
        subkategori = (Combobox) getFellow("subkategori");
        kategori = (Textbox) getFellow("kategori");
        satuan = (Combobox) getFellow("satuan");
        hargaA = (Textbox) getFellow("hargaA");
        hargaB = (Textbox) getFellow("hargaB");
        hargaE = (Textbox) getFellow("hargaE");
        hpp = (Textbox) getFellow("hpp");
        ukuran = (Textbox) getFellow("ukuran");
        warna = (Textbox) getFellow("warna");
        ket1 = (Textbox) getFellow("ket1");
        ket2 = (Textbox) getFellow("ket2");
        
        
     }
    
    private void populate(String Id){
        Session s = Libs.sfDB.openSession();
        try {
        	 String query = "select kode,idbarang,namabrg,merkbrg,tipebrg,ukuran,warna,merk,kd_subkategori,satuan,hargaA,hargaB,hargaE,hpp,ket1,ket2 "
        	 		+ " from "+Libs.getDbName()+".dbo.barang "
        	 		+ " where kode ='"+Id+"' ";
             System.out.println(query);
             List<Object[]> l = s.createSQLQuery(query).list();
             for (Object[] o : l) {
            	 
            	 System.out.println(Libs.nn(o[0]));
             
            	 code.setText(Libs.nn(o[0]));
                 idbarang.setText(Libs.nn(o[1]));
                 nama.setText(Libs.nn(o[2]));
                 merkbrg.setText(Libs.nn(o[3]));
                 tipebrg.setText(Libs.nn(o[4]));
                 ukuran.setText(Libs.nn(o[5]));
                 warna.setText(Libs.nn(o[6]));
               
                 
                Object[] obj1 = Libs.getmerk(Libs.nn(Libs.nn(o[7])));
 			  	merk.setText((obj1[0].toString()));
               
 			  	Object[] obj2 = Libs.getsubkategori(Libs.nn(Libs.nn(o[8])));
                 subkategori.setText((obj2[1].toString()));
                 kategori.setText((obj2[0].toString()));

 			  	satuan.setText(Libs.nn(o[9]));
                 hargaA.setText(Libs.nn(o[10]));
                 hargaB.setText(Libs.nn(o[11]));
                 hargaE.setText(Libs.nn(o[12]));
                 hpp.setText(Libs.nn(o[13]));
                 ket1.setText(Libs.nn(o[14]));
                 ket2.setText(Libs.nn(o[15]));
                 
                   
             	
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    
    }
    
   
    
    public void save(){
    	 boolean valid = false;
         Session s = Libs.sfDB.openSession();
         String sub = subkategori.getSelectedItem().getLabel();
         sub = sub.substring(sub.indexOf("(")+1, sub.indexOf(")"));
         
         String mrk = merk.getSelectedItem().getLabel();
         mrk = mrk.substring(mrk.indexOf("(")+1, mrk.indexOf(")"));
         
     	 
         try {
        	 s.beginTransaction(); 
        	 
            	 String qry = " Update  "+Libs.getDbName()+".dbo.barang set "
//            	 		+ "kode,idbarang,namabrg,merkbrg,tipebrg,satuan,kd_subkategori,merk,hargaA,hargaB,hargaE,ket1,ket2, "
             		+ " idbarang ='"+idbarang.getText()+"',namabrg='"+nama.getText()+"',merkbrg='"+merkbrg.getText()+"',tipebrg='"+tipebrg.getText()+"', "
             		+ " ukuran='"+ukuran.getText()+"',warna = '"+warna.getText()+"', satuan='"+satuan.getText()+"',kd_subkategori='"+sub+"',merk='"+mrk+"',"
             		+ " hargaA='"+hargaA.getText()+"',hargaB='"+hargaB.getText()+"',hargaE='"+hargaE.getText()+"', hpp='"+hpp.getText()+"', "
             		+ " ket1='"+ket1.getText()+"',ket2='"+ket2.getText()+"' "
             		+ " where kode = '"+code.getText()+"' ";
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

    
    public void subKategoriSelected(){
    	Session s = Libs.sfDB.openSession();
    	String sub = subkategori.getSelectedItem().getLabel();
    	if(sub == ""){
    		kategori.setText("");
    	}else{
    		sub = sub.substring(sub.indexOf("(")+1, sub.indexOf(")"));
    	}
        try {
        	 String query = "select a.nama,a.kode from "+Libs.getDbName()+".dbo.kategori a inner join "+Libs.getDbName()+".dbo.subkategori b "
        	 		+ "on a.kode=b.kd_kategori where b.kode = '"+sub+"' ";
              List<Object[]> l = s.createSQLQuery(query).list();
             for (Object[] o : l) {
            	 kategori.setText(Libs.nn(o[0]) +"("+Libs.nn(o[1])+")");
            }
        } catch (Exception ex) {
            log.error("kategori", ex);
        } finally {
            if (s!=null && s.isOpen()) s.close();
        }
    	
    }
    
    
    public void refresh() {
    	 populate(Id);
    }
    
    

  
}