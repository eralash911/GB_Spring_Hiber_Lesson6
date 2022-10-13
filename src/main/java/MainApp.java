
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.shein.beans.repositories.CustomerDao;
import ru.shein.beans.repositories.OrdersDao;
import ru.shein.beans.repositories.ProductDao;
import ru.shein.beans.services.CustomerInfo;
import ru.shein.entities.Customer;
import ru.shein.entities.OrderData;
import ru.shein.entities.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication (scanBasePackages = "ru.shein.beans")
public class MainApp
{
    public static final boolean H2MEM = true;
    private static ConfigurableApplicationContext context;
    private static SessionFactory sessionFactory;
    public static final String SEPARATOR = "=============================================\n";

    public static void main (String[] args)
    {
        //try
        //{   context = SpringApplication.run (MainApp.class, args);
        //    logline("Инициализация окончена.");
        //}
        //catch (Exception e){e.printStackTrace();}

        context = new AnnotationConfigApplicationContext (MainApp.class);
        runTest();
    }
//------------------- Конфигурация ---------------------------------------------------

    @Bean
    private static SessionFactory sessionFactory ()
    {
        Configuration cfg = new Configuration().configure("hibernate6-6.cfg.xml");
        sessionFactory = cfg.buildSessionFactory();

        if (H2MEM)
            if (sessionFactory != null && !readSqlFile ("lesson6-6.sql", sessionFactory))
            {
                sessionFactory.close();
                sessionFactory = null;
            }
        return sessionFactory;
    }
//------------------------------------------------------------------------------------

    private static boolean readSqlFile (String strPath, SessionFactory sf)
    {
        boolean result = false;
        try (Session session = sf.getCurrentSession();)
        {
            String sql = Files.readString(Paths.get(strPath));

            session.beginTransaction();
            session.createNativeQuery(sql).executeUpdate();
            session.getTransaction().commit();
            result = true;
            logline("SQL-файл считан: "+ strPath);
        }
        catch (IOException e){e.printStackTrace();}
        return result;
    }


    static void runTest()
    {
        Long cid = 3L;
        Long pid = 5L;
        double cost = 150.0;

        CustomerDao cdao = context.getBean(CustomerDao.class);
        ProductDao pdao = context.getBean(ProductDao.class);
        OrdersDao odao = context.getBean(OrdersDao.class);
        CustomerInfo ci = context.getBean(CustomerInfo.class);

        Customer c = cdao.createCastomer ("Толик");     System.out.printf("\nДобавлен покупатель : %s.", c);
        Product p = pdao.createProduct ("Велик", cost); System.out.printf("\nПоступил товар : %s.", p);
        OrderData od = odao.save (c, p, cost);          System.out.printf("\nТолик купил Велик : %s.", od);

        try(Session s = sessionFactory.getCurrentSession())
        {
            s.beginTransaction();
            p.setCost(155.0);
            s.update(p);
            s.getTransaction().commit();
            System.out.printf("\nЦена на Велик поднялась до %.2f : %s.", p.getCost(), p);
        }

        System.out.println(SEPARATOR);  //------------------
        try(Session s = sessionFactory.getCurrentSession())
        {
            s.beginTransaction();

            printList (s.createQuery("from Customer", Customer.class).getResultList());
            printList (s.createQuery("from Product", Product.class).getResultList());
            printList (s.createQuery("from OrderData", OrderData.class).getResultList());

            System.out.printf("Список продуктов покупателя с cid = %d:\n\n", cid);
            printList (ci.getProductsByCustomerId(cid, s));

            System.out.printf("Список покупателей продукта с pid = %d:\n\n", pid);
            printList (ci.getCustomersByProductId(pid, s));

            s.createNativeQuery("select * from customer_products;").getResultList().forEach(System.out::println); //< список содержит объекты, каждый из которых содержит массив из двух BigInteger. (Для этой таблицы класс-сущность не создан.)
            s.getTransaction().commit();
        }
    }

    public static <T> void printList (List<T> list)
    {
        for (T t : list)
            System.out.println(t);
        System.out.println(SEPARATOR);  //------------------
    }

    public static void logline (String msg)
    {
        System.out.print ("\n********************\t" + msg + "\t**************\n\n");
    }
}
