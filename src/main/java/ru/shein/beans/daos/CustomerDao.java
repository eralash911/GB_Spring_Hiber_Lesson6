package ru.shein.beans.daos;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shein.entities.Customer;
import ru.shein.entities.OrderData;
import ru.shein.entities.Product;

import java.util.List;

@Component
public class CustomerDao
{
    private final SessionFactory sessionFactory;


    @Autowired
    public CustomerDao (SessionFactory sf)
    {
        if (sf == null)
            throw new IllegalArgumentException();

        sessionFactory = sf;
    }
//--------------------------------------------------------------------------------

    public Customer createCastomer (String name)
    {
        Customer c = Customer.newCustomer(name);
        if (!saveOrUpdate (c))
        {
            c = null;
        }
        return c;
    }

    public boolean saveOrUpdate (Customer customer)
    {
        boolean ok = false;

        if (customer != null)
            try (Session session = sessionFactory.getCurrentSession();)
            {
                session.beginTransaction();
                session.saveOrUpdate (customer);
                session.getTransaction().commit();
                ok = true;
            }
        return ok;
    }

    public void addOrderDataToCustomer (OrderData od)
    {
        if (od != null)
        {
            Customer c = od.getCustomer();
            c.addOrderData (od);
        }
    }

    public List<Product> getProductsByCustomerId (Long customerId, Session session)
    {
        List<Product> result = Product.emptyProductList();
        if (session != null)
        {
            Customer c = session.createQuery ("from Customer c where c.id = :x", Customer.class)
                    .setParameter("x", customerId)
                    .getSingleResult();
            if (c != null)
                for (OrderData od : c.getOrderDatas())
                    result.add(od.getProuct());
        }
        return result;
    }
}

