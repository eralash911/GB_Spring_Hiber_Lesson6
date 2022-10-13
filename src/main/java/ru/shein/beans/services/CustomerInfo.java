package ru.shein.beans.services;


import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shein.beans.repositories.CustomerDao;
import ru.shein.beans.repositories.ProductDao;
import ru.shein.entities.Customer;
import ru.shein.entities.Product;

import java.util.List;

@Component
public class CustomerInfo
{
    private final CustomerDao customerDao;
    private final ProductDao productDao;


    @Autowired
    public CustomerInfo (CustomerDao cdao, ProductDao pdao)
    {
        if (cdao == null || pdao == null)
            throw new IllegalArgumentException();

        customerDao = cdao;
        productDao = pdao;
    }
//-------------------------------------------------------------

    public List<Product> getProductsByCustomerId (Long customerId, Session session)
    {
        return customerDao.getProductsByCustomerId (customerId, session);
    }

    public List<Customer> getCustomersByProductId (Long productId, Session session)
    {
        return productDao.getCustomersByProductId (productId, session);
    }
}

