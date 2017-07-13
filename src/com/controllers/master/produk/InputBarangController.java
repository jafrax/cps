package com.controllers.master.produk;


import com.pojo.ClientPOJO;
import com.pojo.MemberPOJO;
import com.pojo.ProductPOJO;
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
public class InputBarangController extends Window {

    private Logger log = LoggerFactory.getLogger(InputBarangController.class);
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
    private Textbox hpp;
    private Textbox ukuran;
    private Textbox warna; 
    private Textbox ket1;
    private Textbox ket2;  

    public void onCreate() {
        initComponents();
        Libs.getmerk(merk);
        Libs.getsubkategori(subkategori);
        Libs.getsatuan(satuan);
        
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
    
    public void save(){
    	 boolean valid = false;
         Session s = Libs.sfDB.openSession();
         String sub = subkategori.getSelectedItem().getLabel();
         sub = sub.substring(sub.indexOf("(")+1, sub.indexOf(")"));
         
         String mrk = merk.getSelectedItem().getLabel();
         mrk = mrk.substring(mrk.indexOf("(")+1, mrk.indexOf(")"));
         
     	 
         try {
        	 s.beginTransaction(); 
        	 
        	 	String sql = "Select * from " +Libs.getDbName()+".dbo.lokasi   order by kode asc";
   	    		SQLQuery query = s.createSQLQuery(sql);
   	    		List<Object[]> l = query.list();
   	    		for(Object[] o : l){
   	    		 String str = " insert into  "+Libs.getDbName()+".dbo.stokperlok (kd_barang,kd_lokasi,jumlah) values ('"+code.getText()+"','"+((String)o[0]).trim()+"', 0)";
   	    		 System.out.println(str);
   	    		 s.createSQLQuery(str).executeUpdate();
   	             s.flush();
   	             s.clear();
   	    		}
        	
        	 String qry = " insert into  "+Libs.getDbName()+".dbo.barang  (kode,idbarang,namabrg,merkbrg,tipebrg,ukuran,warna,satuan,kd_subkategori,merk,"
             		+ " hargaA,hargaB,hargaE,hpp,ket1,ket2 ) "
             		+ " values "
             		+ " ('"+code.getText()+"','"+idbarang.getText()+"','"+nama.getText()+"','"+merkbrg.getText()+"','"+tipebrg.getText()+"', "
             		+ " '"+ukuran.getText()+"','"+warna.getText()+"','"+satuan.getText()+"', '"+sub+"','"+mrk+"', "
             		+ " '"+hargaA.getText()+"','"+hargaB.getText()+"','"+hargaE.getText()+"','"+hpp.getText()+"', "
             		+ " '"+ket1.getText()+"','"+ket2.getText()+"' )";
             System.out.println(qry);
             s.createSQLQuery(qry).executeUpdate();
             s.flush();
             s.clear();
             s.getTransaction().commit();
             Messagebox.show("Insert status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
             valid = true;
            } catch (Exception ex) {
             s.getTransaction().rollback();
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
        clear();
    }
    
    
    private void clear(){
   	 kategori.setText("");  
     
     code.setText("");
     idbarang.setText("");
     nama.setText("");
     merkbrg.setText("");
     tipebrg.setText("");
     merk.setText("");
     subkategori.setText("");
     kategori.setText("");
     satuan.setText("");
     hargaA.setText("");
     hargaB.setText("");
     hargaE.setText("");
     hpp.setText("");
     ukuran.setText("");
     warna.setText("");
     ket1.setText("");
     ket2.setText("");
     
   }

  
}