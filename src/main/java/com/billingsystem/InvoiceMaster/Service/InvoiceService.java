package com.billingsystem.InvoiceMaster.Service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    public byte[] generateInvoice(Map<String, Object> parameters) throws Exception {

        ClassPathResource resource = new ClassPathResource("item_report.jasper");
        logger.info("******* Loading Jasper file from: {}", resource.getPath());

        try (InputStream inputStream = resource.getInputStream()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parameters, new JREmptyDataSource());
            logger.info("Invoice generated successfully.");
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            logger.error("Error filling report or exporting to PDF: ", e);
            throw e;
        }
    }

    public byte[] generateCustomerReport(Map<String, Object> parameters, JRBeanCollectionDataSource dataSource) throws JRException, IOException {
        ClassPathResource resource = new ClassPathResource("customer_report.jasper");
        logger.info("******* Loading Jasper file from: {}", resource.getPath());

        try (InputStream inputStream = resource.getInputStream()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parameters, dataSource);
            logger.info("Invoice generated successfully.");
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            logger.error("Error filling report or exporting to PDF: ", e);
            throw e;
        }
    }
}
