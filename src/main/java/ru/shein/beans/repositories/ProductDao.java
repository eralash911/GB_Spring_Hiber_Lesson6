package ru.shein.beans.repositories;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shein.entities.Customer;
import ru.shein.entities.OrderData;
import ru.shein.entities.Product;


import java.util.List;

@Component
public class ProductDao
{

    private final SessionFactory sessionFactory;


    @Autowired
    public ProductDao (SessionFactory sf)
    {
        if (sf == null)
            throw new IllegalArgumentException();

        sessionFactory = sf;
    }
//----------------------------------------------------------------------------------------

    public Product createProduct (String title, double cost)
    {
        Product p = Product.newProduct(title, cost);
        if (!saveOrUpdate (p))
        {
            p = null;
        }
        return p;
    }

    public boolean saveOrUpdate (Product product)
    {
        boolean ok = false;

        if (product != null)
            try (Session session = sessionFactory.getCurrentSession();)
            {
                session.beginTransaction();
                session.saveOrUpdate (product);
                session.getTransaction().commit();
                ok = true;
            }
        return ok;
    }

    public void addOrderDataToProduct (OrderData od)
    {
        if (od != null)
        {
            Product p = od.getProuct();
            p.addOrderData (od);
        }
    }

    public List<Customer> getCustomersByProductId (Long productId, Session session)
    {
        List<Customer> result = Customer.emptyCustomerList();
        if (session != null)
        {
            Product p = session.createQuery ("from Product p where p.id = :x", Product.class)
                    .setParameter("x", productId)
                    .getSingleResult();
            if (p != null)
                for (OrderData od : p.getOrderDatas())
                    result.add(od.getCustomer());
        }
        return result;
    }
}

