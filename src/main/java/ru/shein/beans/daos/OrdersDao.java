package ru.shein.beans.daos;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shein.entities.Customer;
import ru.shein.entities.OrderData;
import ru.shein.entities.Product;

@Component
public class OrdersDao
{
    private final SessionFactory sessionFactory;
    private final CustomerDao customerDao;
    private final ProductDao productDao;


    @Autowired
    public OrdersDao (SessionFactory sf, CustomerDao cdao, ProductDao pdao)
    {
        if (sf == null || cdao == null || pdao == null)
            throw new IllegalArgumentException();

        sessionFactory = sf;
        customerDao = cdao;
        productDao = pdao;
    }
//--------------------------------------------------------------------------------

    public OrderData save (Customer customer, Product product, double cost)
    {
        OrderData od = OrderData.newOrderData (customer, product, cost);
        if (!save (od))
        {
            od = null;
        }
        return od;
    }

    public boolean save (OrderData orderdata)
    {
        boolean ok = false;

        if (orderdata != null)
            try (Session session = sessionFactory.getCurrentSession();)
            {
                session.beginTransaction();
                session.save (orderdata);
                session.getTransaction().commit();
                ok = true;
            }
        if (ok)
        {
            customerDao.addOrderDataToCustomer (orderdata);
            productDao.addOrderDataToProduct (orderdata);
        }
        return ok;
    }
}

