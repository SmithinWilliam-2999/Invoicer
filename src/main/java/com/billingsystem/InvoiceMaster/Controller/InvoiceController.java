package com.billingsystem.InvoiceMaster.Controller;

import com.billingsystem.InvoiceMaster.Model.Customer;
import com.billingsystem.InvoiceMaster.Model.Transaction;
import com.billingsystem.InvoiceMaster.Service.InvoiceService;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/downloadInvoice")
    public ResponseEntity<byte[]> downloadInvoice(
            @RequestParam String invoiceId,
            @RequestParam String customerName,
            @RequestParam String date,
            @RequestParam double amount) {

        logger.info("Received request to download invoice for ID: {}", invoiceId);

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoiceId", invoiceId);
            parameters.put("customerName", customerName);
            parameters.put("date", date);
            parameters.put("amount", amount);

            logger.info("Parameters passed to Jasper Report: invoiceId={}, customerName={}, date={}, amount={}",
                    invoiceId, customerName, date, amount);

            byte[] pdf = invoiceService.generateInvoice(parameters);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + invoiceId + ".pdf");

            logger.info("Successfully generated invoice for ID: {}", invoiceId);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);

        } catch (Exception e) {
            logger.error("Error generating invoice for ID: {}. Error: {}", invoiceId, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/downloadCustomerReport")
    public ResponseEntity<byte[]> downloadCustomerReport(@RequestParam String customerNumber) {
        logger.info("Received request to download report for customer number: {}", customerNumber);

        try {
            Customer customer = new Customer();
            customer.setCustomerId(customerNumber);
            customer.setCustomerName("Customer " + customerNumber);

            List<Transaction> transactions = new ArrayList<>();
            Transaction txn1 = new Transaction();
            txn1.setTransactionId("TXN001");
            txn1.setDate("2024-11-06");
            txn1.setAmount(500.00);
            transactions.add(txn1);

            Transaction txn2 = new Transaction();
            txn2.setTransactionId("TXN002");
            txn2.setDate("2024-11-07");
            txn2.setAmount(150.00);
            transactions.add(txn2);

            customer.setTransactions(transactions);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("customerId", customer.getCustomerId());
            parameters.put("customerName", customer.getCustomerName());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(customer.getTransactions());

            byte[] pdf = invoiceService.generateCustomerReport(parameters, dataSource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "customer_report_" + customerNumber + ".pdf");

            logger.info("Successfully generated report for customer number: {}", customerNumber);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);

        } catch (Exception e) {
            logger.error("Error generating customer report for customer number: {}. Error: {}", customerNumber, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
