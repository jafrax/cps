package com.controllers;

import com.pojo.ClientPOJO;
import com.pojo.ICDPOJO;
import com.pojo.ProductPOJO;
import com.tools.Libs;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;


@SuppressWarnings("serial")
public class ReturBeliController extends Window {

    private Logger log = LoggerFactory.getLogger(ReturBeliController.class);
    private Bandbox bbClient;
    private Bandbox bbProduct;
    private Listbox lb;
    private Listbox lbClients;
    private Listbox lbProducts;
    private Textbox keterangan;
    private Paging pg;
    private Paging pgClients;
    private Paging pgProducts;
    private String whereClients;
    private String whereProducts;
    private Textbox perusahaan;
    private Textbox noretur;
    private Textbox jumlah;
    private Textbox harga;
    private Datebox tglretur;
    private Combobox kdprk;
    private Combobox tipe;
    private Textbox namabarang;
    private Textbox faktur;
    private Combobox gudang;
    private String user ;
    private Object[] ihm;
    private List<ICDPOJO> tempProductICDs = new ArrayList<ICDPOJO>();
    private List<ICDPOJO> productICDs = new ArrayList<ICDPOJO>();

    public void onCreate() {
    	noaktif();
        initComponents();
        
      }

    private void initComponents() {
   	
        lb = (Listbox) getFellow("lb");
        lbClients = (Listbox) getFellow("lbClients");
        lbProducts = (Listbox) getFellow("lbProducts");
        pg = (Paging) getFellow("pg");
        pgClients = (Paging) getFellow("pgClients");
        pgProducts = (Paging) getFellow("pgProducts");
        keterangan = (Textbox) getFellow("keterangan");
        bbClient = (Bandbox) getFellow("bbClient");
        bbProduct = (Bandbox) getFellow("bbProduct");
        perusahaan = (Textbox) getFellow("perusahaan");
        noretur = (Textbox) getFellow("noretur");
        kdprk = (Combobox) getFellow("kdprk");
        tipe = (Combobox) getFellow("tipe");
        namabarang = (Textbox) getFellow("namabarang");
        gudang = (Combobox) getFellow("gudang");   
        jumlah=(Textbox) getFellow("jumlah");
        harga=(Textbox) getFellow("harga");
        tglretur = (Datebox)getFellow("tglretur");
        faktur=(Textbox) getFellow("faktur"); 
       
        
       	((Toolbarbutton) getFellow("tbsave")).setDisabled(true);
        ((Combobox) getFellow("gudang")).setDisabled(true);
        ((Textbox) getFellow("noretur")).setDisabled(true);
        
        user = (String)Executions.getCurrent().getSession().getAttribute("user");
        
        Libs.getGudang(gudang);
        tipe.appendItem("");
        tipe.appendItem("K");
        tipe.appendItem("T");
        tipe.setSelectedIndex(0);
        
        
        tglretur.setValue(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, + 30);
        //tempo.setValue(cal.getTime());
       

        pgClients.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populateClients(evt.getActivePage()*pgClients.getPageSize(), pgClients.getPageSize());
            }
        });
        
        pgProducts.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populateProducts(evt.getActivePage()*pgProducts.getPageSize(), pgProducts.getPageSize());
            }
        });

        pg.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populate(evt.getActivePage() * pg.getPageSize(), pg.getPageSize());
            }
        });
    }
    
    
    public void refresh() {
    	clear();
    	noaktif();
        initComponents();
        
    }

 
    public void input(){
    	 Session s = Libs.sfDB.openSession();
    	 try {
    		  s.beginTransaction();
             String query  = " Exec " + Libs.getDbName()+".dbo.returpembelian '"+ user +"' " ;
             System.out.println(query);
             List<Object[]> l = s.createSQLQuery(query).list();
             for (Object[] o : l) {
            	noretur.setText(Libs.nn(o[0]).trim());
            	aktif();
            	}
             s.getTransaction().commit();
         } catch (Exception ex) {
        	  s.getTransaction().rollback();
             log.error("input", ex);
         } finally {
             s.close();
         }
    }
    private void noaktif(){
    	 ((Toolbarbutton) getFellow("tbinput")).setDisabled(false);
    	 ((Toolbarbutton) getFellow("tbsave")).setDisabled(true);
         ((Combobox) getFellow("gudang")).setDisabled(true);
  }
    
    private void aktif(){
    	((Toolbarbutton) getFellow("tbinput")).setDisabled(true);
        ((Toolbarbutton) getFellow("tbsave")).setDisabled(false);
        ((Combobox) getFellow("gudang")).setDisabled(false);
        populateClients(0, pgClients.getPageSize());
        
    }
    
    private void populateClients(int offset, int limit) {
        lbClients.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select "
                    + "kode, nama ,perusahaan ";
            String q2 = "from "+Libs.getDbName()+".dbo.suplier ";
            String q3 = "";
            String q4 = "order by kode asc ";

            if (whereClients!=null) q3 = "where " + whereClients;

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pgClients.setTotalSize(rc);

            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                ClientPOJO e = new ClientPOJO();
                e.setClientId(Libs.nn(o[0]).trim());
                e.setClientName(Libs.nn(o[1]).trim());
                e.setClientToko(Libs.nn(o[2]).trim());

                Listitem li = new Listitem();
                li.setValue(e);
                li.appendChild(new Listcell(e.getClientId()));
                li.appendChild(new Listcell(e.getClientName()));
                li.appendChild(new Listcell(e.getClientToko()));
                lbClients.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateClients", ex);
        } finally {
            s.close();
        }
    }

    private void populateProducts(int offset, int limit) {
       ClientPOJO clientPOJO = (ClientPOJO) bbClient.getAttribute("e");
//       if (gudang.getText() == ""){
//    	   Messagebox.show("pilih gudang dahulu !!!");
//       }
        lbProducts.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
        	String q0 = "select count(*) ";
            String q1 = "select b.no_fak, a.kode,a.namabrg,a.merkbrg ";
            String q2 = "from "+Libs.getDbName()+".dbo.barang a inner join  "+Libs.getDbName()+".dbo.beli_rin b "
            		+ " ON a.kode=b.kd_barang "
            		+ "inner join "+Libs.getDbName()+".dbo.beli c "
            		+ "ON c.no_fak=b.no_fak ";
            String q3 = " where  kd_suplier = '"+bbClient.getText()+"' ";
            String q4 = "order by b.no_fak  desc ";

            
            if (whereProducts!=null) {
                if (!q3.isEmpty()) q3 += "and (" + whereProducts + ") ";
                else q3 += "where (" + whereProducts + ") ";
            }

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pgProducts.setTotalSize(rc);
            System.out.println(q1 + q2 + q3 + q4);
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                ProductPOJO e = new ProductPOJO();
                e.setClientPOJO(clientPOJO);
                e.setProductFaktur(Libs.nn(o[0]));
                e.setProductId(Libs.nn(o[1]));
                e.setProductName(Libs.nn(o[2]));
                e.setProductMerk(Libs.nn(o[3]));
                Listitem li = new Listitem();
                li.setValue(e);
                li.appendChild(new Listcell(e.getProductFaktur()));
                li.appendChild(new Listcell(e.getProductId()));
                li.appendChild(new Listcell(e.getProductName()));
                li.appendChild(new Listcell(e.getProductMerk()));
                lbProducts.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateProducts", ex);
        } finally {
            s.close();
        }
    }

    public void clientSelected() {
        if (lbClients.getSelectedCount()==1) {
            ClientPOJO clientPOJO = lbClients.getSelectedItem().getValue();
            bbClient.setText(clientPOJO.getClientId());
            bbClient.setAttribute("e", clientPOJO);
            perusahaan.setText(clientPOJO.getClientToko());
            populateProducts(0, pgProducts.getPageSize());
            bbClient.close();
        }
    }

    public void productSelected() {
        if (lbProducts.getSelectedCount()>0) {
            ProductPOJO productPOJO = lbProducts.getSelectedItem().getValue();
            bbProduct.setText(productPOJO.getProductId());
            bbProduct.setAttribute("e", productPOJO);
            faktur.setText(productPOJO.getProductFaktur());
            
            jumlah.setText(Libs.jumlahbeli(productPOJO.getProductId()));
            harga.setText(Libs.hargabeli(productPOJO.getProductId()));
            keterangan.setText(productPOJO.getProductName());
            namabarang.setText(productPOJO.getProductMerk());
            populate(0, pg.getPageSize());
            ((Toolbarbutton) getFellow("tbnAdd")).setDisabled(false);
            bbProduct.close();
        }
    }

    public void quickSearchClients() {
        Textbox tQuickSearchClients = (Textbox) getFellow("tQuickSearchClients");

        if (!tQuickSearchClients.getText().isEmpty()) {
            whereClients = "perusahaan like '%" + tQuickSearchClients.getText() + "%' ";
            populateClients(0, pgClients.getPageSize());
            pgClients.setActivePage(0);
        } else {
            refreshClients();
        }
    }

    public void refreshClients() {
        whereClients = null;
        populateClients(0, pgClients.getPageSize());
    }

    public void quickSearchProducts() {
        Textbox tQuickSearchProducts = (Textbox) getFellow("tQuickSearchProducts");

        if (!tQuickSearchProducts.getText().isEmpty()) {
            whereProducts = "a.nama like '%" + tQuickSearchProducts.getText() + "%' or "
                    + "a.kode like '%" + tQuickSearchProducts.getText() + "%' or "
            		+ "b.no_fak like '%" + tQuickSearchProducts.getText() + "%' or ";
            populateProducts(0, pgProducts.getPageSize());
        } else {
            refreshProducts();
        }
    }

    public void refreshProducts() {
        whereProducts = null;
        populateProducts(0, pgProducts.getPageSize());
    }

    private void populate(int offset, int limit) {
    	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);
        lb.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
          
        	String q1 = "select a.faktur,a.kodelok,a.kd_barang, b.namabrg, a.banyak ,a.hrgperbrg ";
            String q2 = "from "+Libs.getDbName()+".dbo.returbeli_rin a inner join "+Libs.getDbName()+".dbo.barang b  "
            		+ " ON a.kd_barang= b.kode ";
            String q3 = "where a.no_retfak='"+noretur.getText()+"' and leuser = '"+user+"' order by a.no_retfak,a.kd_barang asc ";
            System.out.println(q1 + q2 + q3);
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 ).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[0])));
                li.appendChild(new Listcell(Libs.nn(o[1])));
                li.appendChild(new Listcell(Libs.nn(o[2])));
                li.appendChild(new Listcell(Libs.nn(o[3])));
                li.appendChild(new Listcell(Libs.nn(o[4])));
                li.appendChild(new Listcell(Libs.nn(o[5])));
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateClients", ex);
        } finally {
            s.close();
        }

    }

    private void populateProductICDs() { /*
       
    */ }

    public void clear() {
       // if (Messagebox.show("Do you want to clear the list?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
        	noretur.setText("");
        	//gudang.setText("");
        	lb.getItems().clear();
            productICDs.clear();
            pg.setTotalSize(0);
            tipe.getItems().clear();
            tipe.setText("");
            lb.getItems().clear();
            lbClients.getItems().clear();
            bbClient.setText("");
            lbProducts.getItems().clear();
            bbProduct.setText("");
            faktur.setText("");
            perusahaan.setText("");
            namabarang.setText("");
            keterangan.setText("");
            jumlah.setText("");
            harga.setText("");
            
            kdprk.getItems().clear();
            kdprk.setText("");
            tipe.getItems().clear();
            tipe.setText("");
            tglretur.setText("");
    //    }
    }

    private void refineProductICDs() {
        Map<String,ICDPOJO> productICDMap = new HashMap<String,ICDPOJO>();

        for (ICDPOJO icdPOJO : tempProductICDs) {
            if (icdPOJO!=null && productICDMap.get(icdPOJO.getIcdCode())==null) {
                productICDs.add(icdPOJO);
                productICDMap.put(icdPOJO.getIcdCode(), icdPOJO);
            }
        }
    }

    public void importExceptions(UploadEvent evt) {
        if (Messagebox.show("Do you want to upload from " + evt.getMedia().getName() + "?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
            tempProductICDs.clear();

            try {
                POIFSFileSystem fs = new POIFSFileSystem(evt.getMedia().getStreamData());
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sheet = wb.getSheetAt(0);
                for (int i=0; i<sheet.getLastRowNum()+1; i++) {
                    HSSFRow row = sheet.getRow(i);
                    HSSFCell cell1 = row.getCell(0);
                    HSSFCell cell2 = row.getCell(2);
                    HSSFCell cell3 = row.getCell(3);
                    HSSFCell cell4 = row.getCell(4);
                    HSSFCell cell5 = row.getCell(5);
                    HSSFCell cell6 = row.getCell(6);
                    
                    
                    
                    String icd1 = cell1.getStringCellValue().trim();
                    String icd2 = cell2.getStringCellValue().trim();
                    String icd3 = cell3.getStringCellValue().trim();
                    String icd4 = cell4.getStringCellValue().trim();
                    String icd5 = cell5.getStringCellValue().trim();
                    String icd6 = cell6.getStringCellValue().trim();
                    
                    Messagebox.show("data :" + icd1 +icd2 +icd3 +icd4 + icd5 + icd6 );
                    
                    Listitem li = new Listitem();
                   
                    li.appendChild(new Listcell(icd1));
                    li.appendChild(new Listcell(icd2));

                    lb.appendChild(li);
                    
               
                }

            } catch (Exception ex) {
                log.error("importExceptions", ex);
            }
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (bbClient.getAttribute("e")==null) {
            valid = Libs.showErrorForm("Please select Client!");
//        } else if (bbProduct.getAttribute("e")==null) {
//            valid = Libs.showErrorForm("Please select Product!");
        }else if(lb.getAttribute("no_fak") != null){
        	valid = Libs.showErrorForm("Please insert products");
        System.out.println("show : "+ lb.getAttribute("no_fak"));
        }else if(tglretur.getValue() == null ){
        	valid = Libs.showErrorForm("Please select tgl faktur!");
//        }else if (tempo.getValue() == null){
//        	valid = Libs.showErrorForm("Please select tgl tempo!");
        }

        return valid;
    }

    public void save() {
        if (validate()) {
            if (Messagebox.show("Do you want to save this Exceptions?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
            	//saveProcess();
            	 if (saveProcess()) {
                 clear();
                 aktif();
            		 Messagebox.show(" Exceptions has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
               	 } else {
                    Messagebox.show("Error occured while uploading. Please see log file", "Error", Messagebox.OK, Messagebox.ERROR);
                }
            }
        }
    }

    private boolean saveProcess() {
        boolean result = false;
       
        Session s = Libs.sfDB.openSession();
        try {
        	 s.beginTransaction();
        	 String prk = kdprk.getSelectedItem().getLabel();
             prk = prk.substring(prk.indexOf("(")+1, prk.indexOf(")"));
           
        	 String qry  = "update " + Libs.getDbName()+".dbo.returbeli  "
        	 		+ " set flg='1',"
        	 		+ " transaksi = '"+tipe.getText()+"', "
        	 		+ " tgl_retfak  = '"+tglretur.getText()+"', "
        	 		+ " kdprk_tk = '"+prk+"', "
        	 				+ "kd_suplier ='"+bbClient.getText()+"' "
        	 		+ " where  no_retfak =  '"+noretur.getText()+"'  " ;
        	 //Messagebox.show(noretur.getText());
            
             System.out.println(qry);
             s.createSQLQuery(qry).executeUpdate();
             s.flush();
             s.clear();
             
             result = true;
             
             s.getTransaction().commit();

        } catch (Exception ex) {
        	s.getTransaction().rollback();
            log.error("saveProcess", ex);
        } finally {
            if (s!=null && s.isOpen()) s.close();
        }

        return result;
    }


    public void ItemSelected() {
        if (lb.getSelectedCount()==1) {
            ((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
        }
    }

    public void addItem() {
    	  boolean valid = false;
    	  Object[] obj = Libs.getStokLok(bbProduct.getText(),gudang.getText());
    	  int stok = Integer.parseInt(obj[0].toString());
    	  int jml = Integer.parseInt(jumlah.getValue());
		  if ( stok <  jml ){
			  Messagebox.show("stok tidak cukup");
			  jumlah.setValue(obj[0].toString());
			  System.out.println("stok :" + stok);
			  
		  }else{
		    	 Session s = Libs.sfDB.openSession();
		         try {
		         	 s.beginTransaction();
		         	 
		         	 String prk = kdprk.getSelectedItem().getLabel();
		             prk = prk.substring(prk.indexOf("(")+1, prk.indexOf(")"));
		             
		             int total = Integer.parseInt(harga.getText()) * Integer.parseInt(jumlah.getText()) ;
		  		 	 
		             String query= "update " + Libs.getDbName()+".dbo.stokperlok  "
				        	 		+ " set jumlah=jumlah - "+jml+" "
				        	 		+ " where  kd_barang =  '"+bbProduct.getText()+"' and kd_lokasi = '"+gudang.getText()+"'  " ;
		             
		             String sql= "update " + Libs.getDbName()+".dbo.returbeli  "
			        	 		+ " set total=total + "+total+" "
			        	 		+ " where  no_retfak =  '"+noretur.getText()+"'  " ;
		  		 	 
		         	 String qry  = "insert into  " + Libs.getDbName()+".dbo.returbeli_rin "
		         	 		+ "(no_retfak,faktur,kd_barang,banyak,hrgperbrg,kdprk,kodelok,leuser )  "
		         	 		+ " values "
		         	 		+ " ( '"+noretur.getText()+"', '"+faktur.getText()+"', '"+bbProduct.getText()+"','"+jumlah.getText()+"',"
		         	 		+ " '"+harga.getText()+"','"+prk+"',"
		         	 				+ "'"+gudang.getText()+"','"+user+"' )";
		         	 		
		              System.out.println(qry+ query+sql);
		              s.createSQLQuery(qry+query+sql).executeUpdate();
		              s.flush();
		              s.clear();
		              s.getTransaction().commit();
		              
		              valid = true;
		
		         } catch (Exception ex) {
		         	s.getTransaction().rollback();
		             log.error("saveProcess", ex);
		         } finally {
		             if (s!=null && s.isOpen()) s.close();
		         }
		         populate(0, pg.getPageSize());
		         
		         if ( valid == true ){
		        	 
		             bbProduct.setText("");
		             namabarang.setText("");
		             keterangan.setText("");
		             jumlah.setText("");
		             harga.setText("");
		         }
         
		  }
    }
    
    public void delete() {
    	if (Messagebox.show("Do you want to remove this Data ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
 		ihm = (lb.getSelectedItem().getValue());    
 		Session s = Libs.sfDB.openSession();
 		//String q1 = "select a.kd_barang, b.namabrg, a.banyak ,a.hrgperbrg,a.disc_rp,a.disc_pc ";
 		try {
			s.beginTransaction();
			//select a.faktur,a.kodelok,a.kd_barang, b.namabrg, a.banyak ,a.hrgperbrg
		 	String query= "update " + Libs.getDbName()+".dbo.stokperlok  set jumlah=jumlah + '"+Libs.nn(ihm[4])+"'  where  kd_barang =  '"+Libs.nn(ihm[2])+"' and kd_lokasi = '"+Libs.nn(ihm[1])+"'  " ;
		 	String sql= "update " + Libs.getDbName()+".dbo.returbeli  set total=total - "+Libs.nn(ihm[4])+"  where  no_retfak =  '"+noretur.getText()+"'  " ;
		 	String out =" delete from   "+Libs.getDbName()+".dbo.returbeli_rin   where  faktur = '"+Libs.nn(ihm[0])+"'  and kd_barang ='"+ Libs.nn(ihm[2]) + "' and no_retfak = '"+noretur.getText()+"' ";
		 	
		 	System.out.println(query+sql+out);
	        s.createSQLQuery(query+sql+out).executeUpdate();
            s.flush();
            s.clear();
	        s.getTransaction().commit();  

	        Messagebox.show("Data has been Delete", "Information", Messagebox.OK, Messagebox.INFORMATION);
	     } catch (Exception e) {
	    	 s.beginTransaction().rollback();
	         log.error("delete", e);
	         Messagebox.show("Error status has been Delete ", "Information", Messagebox.OK, Messagebox.INFORMATION);
	     } finally {
	         if (s!=null && s.isOpen()) s.close();
	     }
    	}
    	populate(0, pg.getPageSize());
    }
    
    public void transSelected(){
    	String tp = tipe.getSelectedItem().getLabel();
    	if(tp == ""){
    				kdprk.setText("");
    				kdprk.getItems().clear();
    	}else if (tp == "K"){
    				kdprk.getItems().clear();
    				kdprk.appendItem("HD(200.01.01.00)");
    	        	kdprk.appendItem("HK(200.01.04.00)");
    	        	kdprk.setSelectedIndex(0);
    	}else if (tp == "T"){
    				kdprk.getItems().clear();
    	        	kdprk.appendItem("KU(100.01.01.00)");
    	        	kdprk.appendItem("KS(100.01.02.00)");
    	        	kdprk.setSelectedIndex(0);
    	 }
    }

    

    
    
}
