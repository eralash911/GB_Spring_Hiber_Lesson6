package ru.shein.entities;


import com.sun.istack.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.persistence.TemporalType.TIME;
import static javax.persistence.TemporalType.DATE;

@Entity
@Table (name="orders_data")
public class OrderData
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    /*  Одна строка табл. orders_data может быть связана только с одним покупателем в таб. customers, но один покупатель может быть связан со многими строками в таб. orders_data.

        Одна строка в таб. orders_data может быть связана только с одним товаром в таб. products, но один товар может быть связан с многими строками в таб. orders_data.
    */
    @ManyToOne
    @JoinColumn(name="customer_id" /*< имя соотв. колонки (в табл. orders_data) для внешнего ключа. (Колонка таблицы хранит id покупателя, а поле класса хранит ссылку на покупателя с этим id. Из-за того, что наша таблица ссылается на строку таблицы customers, наша таблица является «владельцем связи» между таблицами orders_data и customers.)
              , nullable=false,
                updatable=false,
                foreignKey = @ForeignKey(name = "FK_customer_id")*/
    )
    private Customer customer;

    @ManyToOne
    @JoinColumn(name="product_id"   /*< имя соотв. колонки (в табл. orders_data) для внешнего ключа. (Колонка таблицы хранит id продукта, а поле класса хранит ссылку на продукт с этим id. Из-за того, что наша таблица ссылается на строку таблицы products, наша таблица является «владельцем связи» между таблицами orders_data и products.)
              , nullable=false,
                updatable=false,
                foreignKey = @ForeignKey(name = "FK_product_id")*/
    )
    private Product prouct;

    @Column(name="buy_price"/*, updatable=false*/)       //< имя соотв. колонки (в табл. orders_data)
    private double cost;

    //@Temporal(TIME)   < По идее, эта херата должна указывать, что оставить, а что отрезать от значения типа java.util.Date: оставить время, или дату, или и то, и другое. Очевидно, в таблице должно быть соотв. значениене. Но почему-то она не работает с любым значением. Оставляем java.Time.LocalDateTime вместо java.util.Date как более знакомую.
    //@CreationTimestamp
    @Column(name="time_stamp")
    private LocalDateTime timeStamp;


    protected OrderData()
    {
        setTimeStamp (LocalDateTime.now()); //< как-то странно получается: при старте конструктор вызывается больше 10 раз, но timeStamp оказывается == null у всех товаров, кроме Велика.
    }

    public static OrderData newOrderData (@NotNull Customer customer, @NotNull Product product, double cost)
    {
        OrderData od = new OrderData();
        if (!od.setCustomer(customer) || !od.setProduct(product) || !od.setCost(cost))
        {
            od = null;
        }
        return od;
    }
//--------------------- Сеттеры и геттеры ----------------------------------------

    /*  Кажется, хибер-т сеттерами не пользуется при инициализации полей. Если ыб он ими пользовался, то у всех полей timeStamp оказался проинициализирован текущим моментом, а не нулём, как то предписвает таблица orders_data.
     */
    public Long getId ()    {   return id;   }
    private void setId (Long id) {   this.id = id;   }

    public double getCost() {   return cost;   }
    private boolean setCost (double c)
    {
        boolean ok = isCostValid(c);
        if (ok)
        {
            cost = c;
        }
        return ok;
    }

    public Product getProuct()  {   return prouct;   }
    private boolean setProduct (@NotNull Product p)
    {
        boolean ok = isProductValid(p);
        if (ok)
        {
            prouct = p;
        }
        return ok;
    }

    public Customer getCustomer()   {   return customer;   }
    private boolean setCustomer (@NotNull Customer c)
    {
        boolean ok = isCustomerValid(c);
        if (ok)
        {
            customer = c;
        }
        return ok;
    }

    public LocalDateTime getTimeStamp() {   return timeStamp;   }
    private boolean setTimeStamp (@NotNull LocalDateTime ldt)
    {
        boolean ok = false;
        if (timeStamp == null && isTimeStampValid(ldt))
        {
            timeStamp = ldt;
            ok = true;
        }
        return ok;
    }

//--------------------------------------------------------------------------------

    public static boolean isProductValid (@NotNull Product p)   {   return p != null;   }

    public static boolean isCustomerValid (@NotNull Customer c)   {   return c != null;   }

    public static boolean isCostValid (double c)   {   return c >= 0.0;   }

    public static boolean isTimeStampValid (@NotNull LocalDateTime ldt)
    {
        LocalDateTime now = LocalDateTime.now();
        return ldt.isBefore(now) || ldt.isEqual(now);
    }

    public String toString()
    {
        return String.format("OrderData:(id:%d, customerId:%d, productId:%d, buyCost:%.2f, time:%s)",
                id, customer.getId(), prouct.getId(), cost, timeStamp);
    }

    public static List<OrderData> emptyOrderDatatList ()  {   return new ArrayList<>();   }
}

