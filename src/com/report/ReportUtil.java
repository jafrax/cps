/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.report;

import com.mysql.jdbc.Driver;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author fandy
 */
public class ReportUtil {

    /**
     * print report menggunakan parameter
     *
     * @param file
     * @param map
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReport(String fileReport, Map map, Connection connection, boolean preview) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) {
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
        jasperViewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        jasperViewer.setVisible(true);
//        GlobalUtils.robotUtil.robot(KeyEvent.VK_TAB);
        connection.close();
    }

    /**
     * print report menggunakan parameter
     *
     * @param file
     * @param map
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportNoPreview(String fileReport, Map map, Connection connection, boolean preview) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        JasperPrintManager.printReport(jasperPrint, false);
        connection.close();
    }

    /**
     * print report setting margin
     *
     * @param file
     * @param map
     * @param conn
     * @param path
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportSettingMargin(String fileReport, Map map, Connection connection, String path, int marginLeft, int marginRight) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) {
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                path);

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OFFSET_X, marginLeft);
        exporter.setParameter(JRExporterParameter.OFFSET_Y, marginRight);
        exporter.exportReport();
        connection.close();
    }

    /**
     * print report buat nama file pdf
     *
     * @param file
     * @param map
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportPdf(String fileReport, Map map, Connection connection, String destFile) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        JasperExportManager.exportReportToPdfFile(jasperPrint, destFile);
        connection.close();
    }

    /**
     * view ireport only (tidak bisa cetak / save)
     *
     * @param file
     * @param map
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportViewOnly(String fileReport, Map map, Connection connection, boolean preview) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);

        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        JRViewer jRViewer = new JRViewer(jasperPrint);
        jRViewer.remove(0);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        JDialog viewer = new JDialog();
        viewer.setTitle("Report Viewer");
        viewer.getContentPane().add(jRViewer);
        viewer.pack();
        viewer.setMinimumSize(screenSize);
        viewer.setVisible(true);
        connection.close();
    }

    /**
     * Fungsi ini digunakan untuk menggabungkan dua file jasper menjadi satu
     * file.
     *
     * @author 14438
     * @param file1
     * @param file2
     * @param map
     * @param map2
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReport2File(String fileReport1, String fileReport2, Map map, Map map2, Connection connection, boolean preview) throws JRException, SQLException {
        JasperPrint jasperPrint1;

        String ext1 = fileReport1.substring(fileReport1.length() - 6);
        String namaFile1;
        if (ext1.equalsIgnoreCase(".jrxml")) {
            namaFile1 = fileReport1.substring(0, fileReport1.length() - 6);
        } else {
            namaFile1 = fileReport1.substring(0, fileReport1.length() - 7);
        }

        String namaFileJrxml1 = namaFile1 + ".jrxml";
        String namaFileJasper1 = namaFile1 + ".jasper";

        File f1 = new File(namaFileJasper1);
        if (!f1.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml1);
            jasperPrint1 = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint1 = JasperFillManager.fillReport(namaFileJasper1, map, connection);
        }

        File theFile1 = new File(namaFileJrxml1);
        if (theFile1.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile1);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        JasperPrint jasperPrint2;

        String ext2 = fileReport2.substring(fileReport2.length() - 6);
        String namaFile2;
        if (ext2.equalsIgnoreCase(".jrxml")) {
            namaFile2 = fileReport2.substring(0, fileReport2.length() - 6);
        } else {
            namaFile2 = fileReport2.substring(0, fileReport2.length() - 7);
        }

        String namaFileJrxml2 = namaFile2 + ".jrxml";
        String namaFileJasper2 = namaFile2 + ".jasper";

        File f2 = new File(namaFileJasper2);
        if (!f2.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml2);
            jasperPrint2 = JasperFillManager.fillReport(jasperReport, map2, connection);
        } else {
            jasperPrint2 = JasperFillManager.fillReport(namaFileJasper2, map2, connection);
        }

        File theFile2 = new File(namaFileJrxml2);
        if (theFile2.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile2);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        List<JRPrintPage> pages = jasperPrint2.getPages();
        for (int count = 0; count < pages.size(); count++) {
            jasperPrint1.addPage(pages.get(count));
        }

        JasperPrint firstsecondlinked = jasperPrint1;
        JasperViewer jasperViewer = new JasperViewer(firstsecondlinked, false);
        jasperViewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        jasperViewer.setVisible(true);
//        GlobalUtils.robotUtil.robot(KeyEvent.VK_TAB);
        connection.close();
    }

    /**
     * Fungsi ini digunakan untuk menggabungkan beberapa file jasper menjadi
     * satu file.
     *
     * @author 14438
     * @param firstFile
     * @param nextFiles
     * @param map
     * @param map2
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportMultiFile(String firstFileReport, List<String> nextFiles, Map map, List<Map> maps, Connection connection, boolean preview) throws JRException, SQLException {
        JasperPrint jasperPrintFirst;

        String extFirst = firstFileReport.substring(firstFileReport.length() - 6);
        String namaFileFirst;
        if (extFirst.equalsIgnoreCase(".jrxml")) {
            namaFileFirst = firstFileReport.substring(0, firstFileReport.length() - 6);
        } else {
            namaFileFirst = firstFileReport.substring(0, firstFileReport.length() - 7);
        }

        String namaFileFirstJrxml = namaFileFirst + ".jrxml";
        String namaFileFirstJasper = namaFileFirst + ".jasper";

        File f1 = new File(namaFileFirstJasper);
        if (!f1.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileFirstJrxml);
            jasperPrintFirst = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrintFirst = JasperFillManager.fillReport(namaFileFirstJasper, map, connection);
        }

        File theFileFirst = new File(namaFileFirstJrxml);
        if (theFileFirst.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFileFirst);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        int noMap = 0;
        for (String file : nextFiles) {
            JasperPrint jasperPrintNext;

            String ext2 = file.substring(file.length() - 6);
            String namaFile2;
            if (ext2.equalsIgnoreCase(".jrxml")) {
                namaFile2 = file.substring(0, file.length() - 6);
            } else {
                namaFile2 = file.substring(0, file.length() - 7);
            }

            String namaFileJrxml2 = namaFile2 + ".jrxml";
            String namaFileJasper2 = namaFile2 + ".jasper";

            File f2 = new File(namaFileJasper2);
            if (!f2.exists()) {
                JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml2);
                jasperPrintNext = JasperFillManager.fillReport(jasperReport, maps.get(noMap), connection);
            } else {
                jasperPrintNext = JasperFillManager.fillReport(namaFileJasper2, maps.get(noMap), connection);
            }

            File theFile2 = new File(namaFileJrxml2);
            if (theFile2.exists()) {
                try {
                    JasperDesign jasperDesign = JRXmlLoader.load(theFile2);
//                    GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
                } catch (Exception ex) { 
//                	GlobalUtils.setError(ex + "");
                    
                }
            }

            noMap++;
            List<JRPrintPage> pages = jasperPrintNext.getPages();
            for (int count = 0; count < pages.size(); count++) {
                jasperPrintFirst.addPage(pages.get(count));
            }
        }
        JasperPrint firstsecondlinked = jasperPrintFirst;
        JasperViewer jasperViewer = new JasperViewer(firstsecondlinked, false);
        jasperViewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        jasperViewer.setVisible(true);
//        GlobalUtils.robotUtil.robot(KeyEvent.VK_TAB);
        connection.close();
    }

    /**
     * cetak langsung dari Format RTF ke printer
     *
     * @param file
     * @param map
     * @param conn
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportToRtfPrint(String fileReport, Map map, Connection connection) throws JRException, SQLException {

        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        /* Create an array of PrintServices */
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService default_service = PrintServiceLookup.lookupDefaultPrintService();

        int selectedService = 0;
        /* Scan found services to see if anyone suits our needs */
        for (int i = 0; i < services.length; i++) {
            if (services[i].getName().toUpperCase().equals(default_service.getName().toUpperCase())) {
                selectedService = i;
            }
        }
        try {
            job.setPrintService(services[selectedService]);
        } catch (PrinterException ex) { 
//        	GlobalUtils.setError(ex + "");
            
        }

        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 210f, 297f, MediaPrintableArea.MM));
        printRequestAttributeSet.add(new Copies(1));
        JRPrintServiceExporter exporter;
        exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        /* We set the selected service and pass it as a paramenter */
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
        exporter.exportReport();
        connection.close();
    }

    /**
     * export ireport ke RTF file
     *
     * @param file
     * @param map
     * @param conn
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportToRtfViewer(String fileReport, Map map, Connection connection) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        File fo = new File(namaFile + ".rtf");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fo);
        } catch (FileNotFoundException ex) { 
//        	GlobalUtils.setError(ex + "");
            
        }
        JRRtfExporter exporter = new JRRtfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, fo);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
        exporter.exportReport();
        connection.close();
        try {
            fos.close();
        } catch (IOException ex) { 
//        	GlobalUtils.setError(ex + "");
            
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(fo);
            } catch (IOException ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }
    }

    public static void printReportToText(String fileReport, Map map, Connection connection, boolean preview) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

//        JasperViewer.viewReport(jasperPrint, false);
        File fileName;
        fileName = new File("bill.txt");
//        ByteArrayOutputStream pdfReport = new ByteArrayOutputStream();
        JRTextExporter exporter = new JRTextExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

        exporter.setParameter(JRTextExporterParameter.BETWEEN_PAGES_TEXT, "\f");
        exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, new Float(798));
        exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, new Float(581));
        exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Float(7));
        exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Float(14));
//        exporter.setParameter(JRTextExporterParameter.OUTPUT_STREAM, pdfReport);
        exporter.setParameter(JRTextExporterParameter.OUTPUT_FILE, fileName);
        exporter.setParameter(JRTextExporterParameter.JASPER_PRINT, jasperPrint);
        JasperPrintManager.printReport(jasperPrint, true);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        exporter.exportReport();
        connection.close();
    }

    /**
     * cetak ireport langsung ke printer tanpa preview terlebih dahulu
     *
     * @param file
     * @param map
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportDirectText(String fileReport, Map map, Connection connection, boolean preview) throws JRException, SQLException {
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);

        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        File fileName = new File(namaFile + ".txt");
        JRTextExporter exporter = new JRTextExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

//        exporter.setParameter(JRTextExporterParameter.BETWEEN_PAGES_TEXT, "\f");
//        exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, new Float(798));
//        exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, new Float(581));
//        exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Float(8));
//        exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Float(15));
        exporter.setParameter(JRTextExporterParameter.OUTPUT_FILE, fileName);
        exporter.exportReport();
        connection.close();

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        PrintService service = ServiceUI.printDialog(null, 200, 200, printServices, defaultService, flavor, pras);

        if (service != null) {
            DocPrintJob job = service.createPrintJob();
            try {
                FileInputStream fis = new FileInputStream(fileName);
                DocAttributeSet das = new HashDocAttributeSet();
                Doc doc = new SimpleDoc(fis, flavor, das);
                job.print(doc, pras);
                fis.close();
            } catch (FileNotFoundException ex) { 
//            	GlobalUtils.setError(ex + "");
                
            } catch (PrintException ex) { 
//            	GlobalUtils.setError(ex + "");
                
            } catch (IOException ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }
    }

    /**
     * export ireport ke excel
     *
     * @param file
     * @param map
     * @param conn
     * @param path
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportToExcel(String fileReport, Map map, Connection connection, String path) throws JRException, SQLException {

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        String fileName = JasperFillManager.fillReportToFile(namaFileJasper, map, connection);
        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                fileName);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                path);

        //Excel specific parameter
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);

        exporter.exportReport();
        connection.close();
    }

    public static void printReportToExcelOnePage(String fileReport, Map map, Connection connection, String path) throws JRException, SQLException {

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        map.put("IS_IGNORE_PAGINATION", true);

        String fileName = JasperFillManager.fillReportToFile(namaFileJasper, map, connection);
        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                fileName);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                path);

        //Excel specific parameter
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);

        exporter.exportReport();
        connection.close();
    }

    public static void printReportToCsvOnePage(String fileReport, Map map, Connection connection, String path) throws JRException, SQLException {

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        map.put("IS_IGNORE_PAGINATION", true);

        String fileName = JasperFillManager.fillReportToFile(namaFileJasper, map, connection);
        JRCsvExporter exporter = new JRCsvExporter();
        exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                fileName);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                path);

        //Excel specific parameter
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);

        exporter.exportReport();
        connection.close();
    }

    /**
     * export ireport ke excel
     *
     * @param file
     * @param map
     * @param conn
     * @param path
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportToExcelMultipleSheets(String fileReport, Map map, Connection connection, String path, List<String> sheetNames) throws JRException, SQLException {

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        String fileName = JasperFillManager.fillReportToFile(namaFileJasper, map, connection);
        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
                fileName);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                path);

        //Excel specific parameter
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        if (!sheetNames.isEmpty()) {
            String[] sheet = new String[sheetNames.size()];
            int i = 0;
            for (String name : sheetNames) {
                sheet[i] = name;
                i++;
            }
            exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES, sheet);
            //exporter.setParameter(JRXlsExporterParameter.PASSWORD, "12345");
        }
        exporter.exportReport();
        connection.close();
    }

    /**
     * Fungsi ini digunakan untuk menggabungkan beberapa file jasper menjadi
     * satu file.
     *
     * @author 14438
     * @param firstFile
     * @param nextFiles
     * @param map
     * @param map2
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static void printReportMultiFileToExcel(String firstFileReport, List<String> nextFiles, Map map, List<Map> maps, Connection connection, String path, List<String> sheetNames) throws JRException, SQLException {
        JasperPrint jasperPrintFirst;

        String extFirst = firstFileReport.substring(firstFileReport.length() - 6);
        String namaFileFirst;
        if (extFirst.equalsIgnoreCase(".jrxml")) {
            namaFileFirst = firstFileReport.substring(0, firstFileReport.length() - 6);
        } else {
            namaFileFirst = firstFileReport.substring(0, firstFileReport.length() - 7);
        }

        String namaFileFirstJrxml = namaFileFirst + ".jrxml";
        String namaFileFirstJasper = namaFileFirst + ".jasper";

        File f1 = new File(namaFileFirstJasper);
        if (!f1.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileFirstJrxml);
            jasperPrintFirst = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrintFirst = JasperFillManager.fillReport(namaFileFirstJasper, map, connection);
        }

        File theFileFirst = new File(namaFileFirstJrxml);
        if (theFileFirst.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFileFirst);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) { 
//            	GlobalUtils.setError(ex + "");
                
            }
        }

        int noMap = 0;
        for (String file : nextFiles) {
            JasperPrint jasperPrintNext;

            String ext2 = file.substring(file.length() - 6);
            String namaFile2;
            if (ext2.equalsIgnoreCase(".jrxml")) {
                namaFile2 = file.substring(0, file.length() - 6);
            } else {
                namaFile2 = file.substring(0, file.length() - 7);
            }

            String namaFileJrxml2 = namaFile2 + ".jrxml";
            String namaFileJasper2 = namaFile2 + ".jasper";

            File f2 = new File(namaFileJasper2);
            if (!f2.exists()) {
                JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml2);
                jasperPrintNext = JasperFillManager.fillReport(jasperReport, maps.get(noMap), connection);
            } else {
                jasperPrintNext = JasperFillManager.fillReport(namaFileJasper2, maps.get(noMap), connection);
            }

            File theFile2 = new File(namaFileJrxml2);
            if (theFile2.exists()) {
                try {
                    JasperDesign jasperDesign = JRXmlLoader.load(theFile2);
//                    GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
                } catch (Exception ex) { 
//                	GlobalUtils.setError(ex + "");
                    
                }
            }

            noMap++;
            List<JRPrintPage> pages = jasperPrintNext.getPages();
            for (int count = 0; count < pages.size(); count++) {
                jasperPrintFirst.addPage(pages.get(count));
            }
        }
        JasperPrint firstsecondlinked = jasperPrintFirst;

        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, firstsecondlinked);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
                path);

        //Excel specific parameter
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        if (!sheetNames.isEmpty()) {
            String[] sheet = new String[sheetNames.size()];
            int i = 0;
            for (String name : sheetNames) {
                sheet[i] = name;
                i++;
            }

            exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES, sheet);
        }
        exporter.exportReport();
        connection.close();
    }

    /**
     * koneksi SCHEMA USRIGPMFG
     *
     * @return
     */
//    public static Connection initConnectionUsrigpmfg() {
//        String url = PropsUtil.getDecrypt("db-igp-usrigpmfg-url");
//        String user = PropsUtil.getDecrypt("db-igp-usrigpmfg-user");
//        String pass = PropsUtil.getDecrypt("db-igp-usrigpmfg-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA USRIGPADMIN
     *
     * @return
     */
//    public static Connection initConnectionUsrigpadmin() {
//        String url = PropsUtil.getDecrypt("db-igp-usrigpadmin-url");
//        String user = PropsUtil.getDecrypt("db-igp-usrigpadmin-user");
//        String pass = PropsUtil.getDecrypt("db-igp-usrigpadmin-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA USRBRGCORP
     *
     * @return
     */
//    public static Connection initConnectionUsrbrgcorp() {
//        String url = PropsUtil.getDecrypt("db-igp-usrbrgcorp-url");
//        String user = PropsUtil.getDecrypt("db-igp-usrbrgcorp-user");
//        String pass = PropsUtil.getDecrypt("db-igp-usrbrgcorp-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA USRHRCORP
     *
     * @return
     */
//    public static Connection initConnectionUsrhrcorp() {
//        String url = PropsUtil.getDecrypt("db-igp-usrhrcorp-url");
//        String user = PropsUtil.getDecrypt("db-igp-usrhrcorp-user");
//        String pass = PropsUtil.getDecrypt("db-igp-usrhrcorp-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA USRWEPADMIN
     *
     * @return
     */
//    public static Connection initConnectionUsrwepadmin() {
//        String url = PropsUtil.getDecrypt("db-igp-usrwepadmin-url");
//        String user = PropsUtil.getDecrypt("db-igp-usrwepadmin-user");
//        String pass = PropsUtil.getDecrypt("db-igp-usrwepadmin-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA POSTGRE
     *
     * @return
     */
//    public static Connection initConnectionPostgre() {
//        String url = PropsUtil.getDecrypt("db-igp-postgre-url");
//        String user = PropsUtil.getDecrypt("db-igp-postgre-user");
//        String pass = PropsUtil.getDecrypt("db-igp-postgre-pass");
//
//        Connection connection = null;
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException ex) { GlobalUtils.setError(ex + "");
//            
//            java.util.logging.Logger.getLogger(ReportUtil.class.getName()).log(java.util.logging.Level.ALL, null, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA USRKLBR
     *
     * @return
     */
//    public static Connection initConnectionUsrklbr() {
//        String url = PropsUtil.getDecrypt("db-igp-usrklbr-url");
//        String user = PropsUtil.getDecrypt("db-igp-usrklbr-user");
//        String pass = PropsUtil.getDecrypt("db-igp-usrklbr-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA USRGKDADMIN
     *
     * @return
     */
//    public static Connection initConnectionUsrgkdadmin() {
//        String url = PropsUtil.getDecrypt("db-igp-usrgkdadmin-url");
//        String user = PropsUtil.getDecrypt("db-igp-usrgkdadmin-user");
//        String pass = PropsUtil.getDecrypt("db-igp-usrgkdadmin-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }
    
//    public static Connection initConnectionUsrgkdadminGKD() {
//        String url = PropsUtil.getDecrypt("db-gkd-usrgkdadmin-url");
//        String user = PropsUtil.getDecrypt("db-gkd-usrgkdadmin-user");
//        String pass = PropsUtil.getDecrypt("db-gkd-usrgkdadmin-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new OracleDriver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }

    /**
     * koneksi SCHEMA MYSQL KIM
     *
     * @return
     */
//    public static Connection initConnectionAdaptive() {
//        String url = PropsUtil.getDecrypt("db-igp-adaptive-url");
//        String user = PropsUtil.getDecrypt("db-igp-adaptive-user");
//        String pass = PropsUtil.getDecrypt("db-igp-adaptive-pass");
//
//        Connection connection = null;
//        try {
//            DriverManager.registerDriver(new Driver());
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        try {
//            connection = DriverManager.getConnection(url, user, pass);
//        } catch (SQLException ex) { GlobalUtils.setError(ex + "");
//            
//            Logger.getLogger(ReportUtil.class.getName()).log(Level.ALL, ex);
//        }
//        return connection;
//    }
    
    /**
     * auto generate pdf
     *
     * @param file
     * @param path 
     * @param namaFilePdf 
     * @param map
     * @param conn
     * @param preview
     * @throws JRException
     * @throws SQLException
     */
    public static boolean autoGeneratePdf(String fileReport, String path, String namaFilePdf, Map map, Connection connection, boolean preview) throws JRException, SQLException {
        boolean valid = true;
        JasperPrint jasperPrint;

        String ext = fileReport.substring(fileReport.length() - 6);
        String namaFile;
        if (ext.equalsIgnoreCase(".jrxml")) {
            namaFile = fileReport.substring(0, fileReport.length() - 6);
        } else {
            namaFile = fileReport.substring(0, fileReport.length() - 7);
        }

        String namaFileJrxml = namaFile + ".jrxml";
        String namaFileJasper = namaFile + ".jasper";

        File f = new File(namaFileJasper);
        if (!f.exists()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(namaFileJrxml);
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);
        } else {
            jasperPrint = JasperFillManager.fillReport(namaFileJasper, map, connection);
        }

        File theFile = new File(namaFileJrxml);
        if (theFile.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(theFile);
//                GlobalUtils.setLastSql(jasperDesign.getQuery().getText());
            } catch (Exception ex) {
//                GlobalUtils.setError(ex + "");
            }
        }
        
        File folderReptemp = new File(path);
        if (folderReptemp.exists() && folderReptemp.isDirectory()) {
            File fileOld = new File(path + "\\" + namaFilePdf);
            if(fileOld.isFile() && fileOld.exists()) {
                // tries to delete a non-existing file
                boolean delete = fileOld.delete();
                if (delete) {
                    JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\" + namaFilePdf);
                } else {
//                    GlobalUtils.setError("failed trying to delete file! -> " + path + "\\" + namaFilePdf);
                    valid = false;
                }
            } else {
                JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\" + namaFilePdf);
            }
        } else {
            boolean successful = folderReptemp.mkdirs();
            if (successful) {
                JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\" + namaFilePdf);
            } else {
//                GlobalUtils.setError("failed trying to create the directories! -> " + path);
                valid = false;
            }
        }
        connection.close();
        
        File cekFile = new File(path + "\\" + namaFilePdf);
        if (!cekFile.exists()) {
//            GlobalUtils.setError("failed trying to create file! -> " + path + "\\" + namaFilePdf);
            valid = false;
        }
        return valid;
    }
}
