package com.controllers.sales;

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
public class SalesOrderController extends Window {

    private Logger log = LoggerFactory.getLogger(SalesOrderController.class);
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
    private Textbox noso;
    private Textbox jumlah;
    private Combobox harga;
    private Textbox discrp;
    private Textbox discpc;
    private Datebox tglorder;
    private Datebox tempo;
    private Combobox kdprk;
    private Combobox tipe;
    private Textbox namabarang;
    private Combobox gudang;
    private String user;
    private Object[] ihm;
    private Combobox cbSales;
    private String sales;
    private String qsales;
    private Combobox kategori;
    
    private int total ;
    private int notadisc ;
    
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
        noso = (Textbox) getFellow("noso");
        kdprk = (Combobox) getFellow("kdprk");
        tipe = (Combobox) getFellow("tipe");
        namabarang = (Textbox) getFellow("namabarang");
        gudang = (Combobox) getFellow("gudang");
        kategori = (Combobox) getFellow("kategori");
        jumlah=(Textbox) getFellow("jumlah");
        harga=(Combobox) getFellow("harga");
        discrp=(Textbox) getFellow("discrp");
        discpc=(Textbox) getFellow("discpc");
        tglorder = (Datebox)getFellow("tglorder");
        tempo = (Datebox)getFellow("tempo");
        cbSales = (Combobox) getFellow("cbSales");
       
        
       	((Toolbarbutton) getFellow("tbsave")).setDisabled(true);
        ((Combobox) getFellow("gudang")).setDisabled(true);
        ((Textbox) getFellow("noso")).setDisabled(true);
       
        Libs.getKategori(kategori);
        Libs.getGudang(gudang);
       
        tipe.appendItem("");
        tipe.appendItem("K");
        tipe.appendItem("T");
        tipe.setSelectedIndex(0);
        
        
        sales = (String)Executions.getCurrent().getSession().getAttribute("sales");
        user = (String)Executions.getCurrent().getSession().getAttribute("user");
        
        System.out.println(sales);
        System.out.println(user);
        
        if (sales.equals("0")) {
        Libs.getSales(cbSales);
        }else{
        cbSales.appendItem(Libs.findSales(sales));
        cbSales.setSelectedIndex(0);
        }
   	    
        
        ((Label) getFellow("lbket")).setValue("Keterangan nota ada di sini ya !!!");
        ((Label) getFellow("lbtotal")).setValue("0");
        ((Label) getFellow("lbNotaDisc")).setValue("0");
        
        
        tglorder.setValue(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, + 30);
        tempo.setValue(cal.getTime());
       
        
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

    
    public void SalesSelected() {
    	String val = ((Textbox) getFellow("cbSales")).getText();
        if (!val.isEmpty()) {
        	String sc = cbSales.getSelectedItem().getLabel();
            sc = sc.substring(sc.indexOf("(")+1, sc.indexOf(")"));
            qsales = "kd_sales =  '"+sc+"' ";
        } else refresh();
    }

    public void input(){
    	 String val = ((Textbox) getFellow("cbSales")).getText();
    	 if (!val.isEmpty()) {
         	String sc = cbSales.getSelectedItem().getLabel();
             sc = sc.substring(sc.indexOf("(")+1, sc.indexOf(")"));
             Session s = Libs.sfDB.openSession();    
    	 try {
    		  s.beginTransaction();
             String query  = " Exec " + Libs.getDbName()+".dbo.noorder '"+sc+"' " ;
             System.out.println(query);
             List<Object[]> l = s.createSQLQuery(query).list();
             for (Object[] o : l) {
            	noso.setText(Libs.nn(o[0]).trim());
            	
	            	if (Libs.nn(o[1]).equals("1")){
	            		tampil(); // tampil data yg ada	
	            	}
            	aktif();
            	}
             s.getTransaction().commit();
         } catch (Exception ex) {
        	  s.getTransaction().rollback();
             log.error("input", ex);
         } finally {
             s.close();
         }
    	 }else{
    		 Messagebox.show(" select the first sales", "Information", Messagebox.OK, Messagebox.INFORMATION);
    	 }
    }
    
    
    private void tampil(){
        Session s = Libs.sfDB.openSession();    
	 try {
		  s.beginTransaction();
	        String query  = "select tgl_order,kd_cust, kd_kategori from " + Libs.getDbName()+".dbo.salesorder where no_order = '"+noso.getValue()+"'  " ;
	        System.out.println(query);
	        List<Object[]> l = s.createSQLQuery(query).list();
	        for (Object[] o : l) {
	        tglorder.setText(Libs.nn(o[0]));
	        if (Libs.nn(o[1]).equals("0")){
	        }else {
	        	bbClient.setText(Libs.nn(o[1]));
	        	kategori.setText(Libs.nn(o[2]));
	        	
	        	String toko = Libs.findToko(Libs.nn(o[1]));
		        perusahaan.setText(toko);
		        populate(0, pg.getPageSize());
		        populateProducts(0, pgProducts.getPageSize());
		        tipe.setText("K");
		        transSelected();
	        }
	        
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
    	 String val = ((Textbox) getFellow("cbSales")).getText();
    	 if (!val.isEmpty()) {
         	String sc = cbSales.getSelectedItem().getLabel();
             sc = sc.substring(sc.indexOf("(")+1, sc.indexOf(")"));
             
	        lbClients.getItems().clear();
	        Session s = Libs.sfDB.openSession();
	        try {
	            String q0 = "select count(*) ";
	            String q1 = "select a.kode_kota,a.kode_toko, a.nama ,a.toko  ";
	            String q2 = "from "+Libs.getDbName()+".dbo.toko a inner join "+Libs.getDbName()+".dbo.plafontoko b "
	            		+ " On a.kode_kota=b.kode_kota and a.kode_toko=b.kode_toko  where b.kode_sales = '"+sc+"' ";
	            String q3 = "";
	            String q4 = "order by a.kode_kota asc ";
	
	            if (whereClients!=null) q3 = "and " + whereClients;
	
	            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
	            pgClients.setTotalSize(rc);
	
	            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
	            for (Object[] o : l) {
	                ClientPOJO e = new ClientPOJO();
	                e.setClientId(Libs.nn(o[0])+"-"+Libs.nn(o[1]));
	                e.setClientName(Libs.nn(o[2]).trim());
	                e.setClientToko(Libs.nn(o[3]).trim());
	
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
    	 }else{
    		 Messagebox.show(" select the first sales", "Information", Messagebox.OK, Messagebox.INFORMATION);
    	 }
    }

    private void populateProducts(int offset, int limit) {
       ClientPOJO clientPOJO = (ClientPOJO) bbClient.getAttribute("e");
       if (gudang.getText() == ""){
    	   Messagebox.show("pilih gudang dahulu !!!");
       }else if (kategori.getText() == ""){
    	   Messagebox.show("pilih Kategori dahulu !!!");
       }else {
    	   
        lbProducts.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
        	 
     	   	String kg = kategori.getSelectedItem().getLabel();
            kg = kg.substring(kg.indexOf("(")+1, kg.indexOf(")"));
            
        	String q0 = "select count(*) ";
            String q1 = "select a.kode,a.namabrg,a.merkbrg ";
            String q2 = "from "+Libs.getDbName()+".dbo.barang a inner join "+Libs.getDbName()+".dbo.subkategori b "
            		+ " ON a.kd_subkategori=b.kode where b.kd_kategori = '"+kg+"' ";
            String q4 = "order by a.kode  asc ";

            
            if (whereProducts!=null) {
                if (!q2.isEmpty()) 
                	q2 += " and (" + whereProducts + ") ";
               
            }
            System.out.println("produk :"+q1 + q2 + q4);
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 ).uniqueResult();
            pgProducts.setTotalSize(rc);
            
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                ProductPOJO e = new ProductPOJO();
                e.setClientPOJO(clientPOJO);
                e.setProductId(Libs.nn(o[0]));
                e.setProductName(Libs.nn(o[1]));
                e.setProductMerk(Libs.nn(o[2]));
               
                Listitem li = new Listitem();
                li.setValue(e);

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
    }
    
    
    public void KategoriSelected(){
    	 populate(0, pg.getPageSize());
         populateProducts(0, pgProducts.getPageSize());

         bbProduct.setText("");
         namabarang.setText("");
         keterangan.setText("");
         discrp.setText("");
         discpc.setText("");
         jumlah.setText("");
         harga.setText("");

    	
    }

    public void clientSelected() {
        if (lbClients.getSelectedCount()==1) {
            ClientPOJO clientPOJO = lbClients.getSelectedItem().getValue();
            bbClient.setText(clientPOJO.getClientId());
            bbClient.setAttribute("e", clientPOJO);
            perusahaan.setText(clientPOJO.getClientToko());
            Session s = Libs.sfDB.openSession();
            try {
	            s.beginTransaction();
	            String sql = "update " + Libs.getDbName()+".dbo.salesorder  "
	            		+ "set kd_cust = '"+clientPOJO.getClientId()+"' where no_order = '"+noso.getValue()+"' ";
	            String qry = "delete from   "+Libs.getDbName()+".dbo.salesorder_rin   where  no_order ='"+noso.getText()+"' ";
	            s.createSQLQuery(sql+qry).executeUpdate();
	            s.flush();
	            s.clear();
	            s.getTransaction().commit();
	            tipe.setText("K");
	            transSelected();
	            
            } catch (Exception ex) {
                log.error("populateProducts", ex);
            } finally {
                s.close();
            }
            populate(0, pg.getPageSize());
            populateProducts(0, pgProducts.getPageSize());
            bbClient.close();
        }
    }

    public void productSelected() {
        if (lbProducts.getSelectedCount()>0) {
            ProductPOJO productPOJO = lbProducts.getSelectedItem().getValue();
            bbProduct.setText(productPOJO.getProductId());
            bbProduct.setAttribute("e", productPOJO);
            discrp.setText("0");
            discpc.setText("0");
            jumlah.setText("0");
            
            String ctk = bbClient.getValue();
            harga.setText( Libs.hargajualakhir(productPOJO.getProductId(), ctk));
           
            for (int i = 0; i <= 3; i++){
            	String a = "New Harga"; 
            	if ( i == 1){ a = "Harga A";} else if(i == 2){ a = "Harga B";} else if(i == 3){ a = "Harga E";};
            		if( i == 0){
					harga.getItems().clear();
	            	}else {
	            	String lastharga = "";	
	            	String hj = a +" ( " +Libs.hargajual(productPOJO.getProductId(),i)+" )";
	            	harga.appendItem(hj);
	            	}
            }
           
            keterangan.setText(productPOJO.getProductName());
            namabarang.setText(productPOJO.getProductMerk());
            populate(0, pg.getPageSize());
            ((Toolbarbutton) getFellow("tbnAdd")).setDisabled(false);
            bbProduct.close();
        }
    }
    
    public void HargaSelected(){
    	String val = ((Textbox) getFellow("harga")).getText();
        if (!val.isEmpty()) {
        	String sc = harga.getSelectedItem().getLabel();
            sc = sc.substring(sc.indexOf("(")+1, sc.indexOf(")"));
         harga.setText(sc.trim());
        } else refresh();
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
            whereProducts = "nama like '%" + tQuickSearchProducts.getText() + "%' or "
                    + "kode like '%" + tQuickSearchProducts.getText() + "%' ";
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
        
        String sc = cbSales.getSelectedItem().getLabel();
        sc = sc.substring(sc.indexOf("(")+1, sc.indexOf(")"));
        try {
          
        	String q1 = "select  a.kd_barang, b.namabrg, a.banyak ,a.hargaperbrg,a.disc_rp,a.disc_pc,a.kd_lokasi ";
            String q2 = "from "+Libs.getDbName()+".dbo.salesorder_rin a inner join "+Libs.getDbName()+".dbo.barang b  "
            		+ " ON a.kd_barang= b.kode ";
            String q3 = "where a.no_order='"+noso.getText()+"' and leuser= '"+sc+"' order by a.kd_barang asc ";
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
                li.appendChild(new Listcell(Libs.nn(o[6])));
                lb.appendChild(li);
                
                ((Label) getFellow("lbtotal")).setValue(Libs.findTotalSO(noso.getText()));
            }
        } catch (Exception ex) {
            log.error("populateClients", ex);
        } finally {
            s.close();
        }

    }


    public void clear() {
       // if (Messagebox.show("Do you want to clear the list?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
        	noso.setText("");
        	gudang.setText("");
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
            
            perusahaan.setText("");
            namabarang.setText("");
            keterangan.setText("");
            discrp.setText("");
            discpc.setText("");
            jumlah.setText("");
            harga.setText("");
            
            kdprk.getItems().clear();
            kdprk.setText("");
            tipe.getItems().clear();
            tipe.setText("");
            
            tglorder.setText("");
            tempo.setText("");
        //   }
    }

     

    private boolean validate() {
        boolean valid = true;

        if (bbClient.getValue()== null) {
            valid = Libs.showErrorForm("Please select Client!");
        }else if(noso.getValue() == null){
        	valid = Libs.showErrorForm("Please insert No SO");
        }else if(tglorder.getValue() == null ){
        	valid = Libs.showErrorForm("Please select tgl faktur!");
        }else if (tempo.getValue() == null){
        	valid = Libs.showErrorForm("Please select tgl tempo!");
        }

        return valid;
    }

    public void save() {
        if (validate()) {
            if (Messagebox.show("Do you want to save this Exceptions?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
            	//saveProcess();
            	 if (saveProcess()) {
            		 clear();
            		 Messagebox.show(" Exceptions has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
            		 ((Toolbarbutton) getFellow("tbinput")).setDisabled(true);
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
             
         	String kg = kategori.getSelectedItem().getLabel();
            kg = kg.substring(kg.indexOf("(")+1, kg.indexOf(")"));
           
        	 String qry  = "update " + Libs.getDbName()+".dbo.salesorder  "
        	 		+ " set flg='1',"
        	 		+ " transaksi = '"+tipe.getText()+"', "
        	 		+ " tgl_order  = '"+tglorder.getText()+"', "
        	 		+ " tgl_tempo = '"+tempo.getText()+"', "
        	 		+ " kdprk_tk = '"+prk+"', "
        	 		+ " kd_cust = '"+bbClient.getText()+"', "
        	 		+ " kd_kategori = '"+kg+"' "
        	 		+ " where  no_order =  '"+noso.getText()+"'  " ;
            
             System.out.println("update tabel sales order : "+ qry);
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

    
    private boolean kondisi() {
        boolean valid = true;
        int hpp = Libs.findHPP(bbProduct.getText());
        int sstok = Libs.findStokLok(bbProduct.getValue(),gudang.getValue() );
        String ihrg = harga.getValue().trim();
        System.out.println("hpp :"+hpp);
        System.out.println("harga :"+ ihrg);
        System.out.println("sisa stok :"+ sstok);
        if (Integer.parseInt(jumlah.getText().replaceAll("\\s+", " ")) >= sstok ) {
        	harga.setText(String.valueOf(sstok));
            valid = Libs.showErrorForm("Please jumlah!");
        }else if(Integer.parseInt(ihrg) <= hpp ){
        	valid = Libs.showErrorForm("Please insert harga");
        }

        return valid;
    }
    
    
    public void addItem() {
    	 if (kondisi()) {
    		 
    	 boolean valid = false;
    	 Session s = Libs.sfDB.openSession();
         try {
         	 s.beginTransaction();
         	 
         	 String prk = kdprk.getSelectedItem().getLabel();
             prk = prk.substring(prk.indexOf("(")+1, prk.indexOf(")"));
             
             String sc = cbSales.getSelectedItem().getLabel();
             sc = sc.substring(sc.indexOf("(")+1, sc.indexOf(")"));
             
             int hpp = Libs.findHPP(bbProduct.getText());
             int total = Integer.parseInt(harga.getText()) * Integer.parseInt(jumlah.getText()) ;
         	 
             //masuk stok virtual
         	 String query= "update " + Libs.getDbName()+".dbo.stokperlok  "
	        	 		+ " set v_jumlah=v_jumlah + '"+jumlah.getText()+"' "
	        	 		+ " where  kd_barang =  '"+bbProduct.getText()+"' and kd_lokasi = '"+gudang.getText()+"' " ;
         	 
         	 String sql= "update " + Libs.getDbName()+".dbo.salesorder  "
        	 		+ " set total=total + "+total+" "
        	 		+ " where  no_order =  '"+noso.getText()+"'  " ;
         	  		 	 
         	 String qry  = "insert into  " + Libs.getDbName()+".dbo.salesorder_rin "
         	 		+ "(no_order,kd_barang,banyak,hpp,hargaperbrg,kdprk,kd_lokasi,disc_rp,disc_pc,leuser,ledate )  "
         	 		+ " values "
         	 		+ " ( '"+noso.getText()+"', '"+bbProduct.getText()+"','"+jumlah.getText()+"', "+hpp+", "
         	 		+ " '"+harga.getText()+"','"+prk+"','"+gudang.getText()+"',"
         	 		+ "'"+discrp.getText()+"','"+discpc.getText()+"','"+ sc +"', getdate() )" ;
             
              System.out.println(qry+query+sql);
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
             discrp.setText("");
             discpc.setText("");
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
		 	
		 	String query= "update " + Libs.getDbName()+".dbo.stokperlok  set v_jumlah=v_jumlah - '"+Libs.nn(ihm[2])+"'  where  kd_barang =  '"+Libs.nn(ihm[0])+"' and kd_lokasi = '"+Libs.nn(ihm[6])+"'  " ;
		 	String sql= "update " + Libs.getDbName()+".dbo.salesorder  set total=total - "+Libs.nn(ihm[3])+"  where  no_order =  '"+noso.getText()+"'  " ;
		 	String out =" delete from   "+Libs.getDbName()+".dbo.salesorder_rin   where  no_order ='"+noso.getText()+"'  and kd_barang ='"+ Libs.nn(ihm[0]) + "' ";
		 	
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
