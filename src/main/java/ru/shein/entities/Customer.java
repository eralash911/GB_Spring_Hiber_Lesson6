package ru.shein.entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table (name="customers"/*, indexes = {@Index(name="customername_idx", columnList="customername")}*/)
public class Customer
{
    /*  Используем OneToMany, т.к. один покупатель может быть связан с многими строками в таб. orders_data, но каждая строка в таб. orders_data может быть связана только с одним покупателем.

        Покупатель не связан напрямую с таблицей products.

        Если у продукта id == 0, то он считается новым и для него создаётся запись в БД.
    */
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="customername"/*, nullable=false*/)
    private String customerName;

    /*  ЭТО НЕ КОЛОНКА! ЭТО ТОЛЬКО ССЫЛКА (ТОЧНЕЕ, СПИСОК ССЫЛОК)! Следующая запись означает, что в какой-то таблице есть поле, которое ссылается на наш id. И есть только один способ узнать, что это за тблица: эта таблица, которая предназначена для хранения объектов типа OrderData (она должна быть только одна такая; называется orders_data).
        Причём, в этой таблице есть одна или несколько строк, в которых описаны (один или несколько) объектов OrderData, поля customer которых ссылаются на наш объект. Сама таблица в упомянутых строках ссылки на нас не хранит, но хранит в них наш id (см.соотв. @JoinColumn в OrderData). — Строки таблицы хранят наш id, а соотв. им объекты хранят ссылку на нас.
        Мы в своей таблице не храним информацию об объектах OrderData или об их строках, но хибер-т дал нам коллекцию объектов OrderData (которые на нас ссылаются); ещё мы может делать SQL-запросы в табл. orders_data по нашему id.
    */
    @OneToMany(mappedBy="customer")             //< имя поля в классе OrderData
    private final List<OrderData> orderDatas;   //< список ссылающихся на нас OrderData

    //эксперимент, не относящийся к ДЗ-6
/*  И это тоже не колонка, а список ссылок, но и в Product тоже нет колоки для связи с нашим классом. Связь объектов осуществляется только через спец.таблицу по имени customer_products. Для этой таблицы сейчас нет класса-сущности, но она прекрасно обходится без него.
    Можно, конечно, обойтись и без таблицы, но тогда это уже будет не JPA, и придётся думать, где хранить инф.о связях объектов Customer и Product.
*/
    @ManyToMany
    @JoinTable (name = "customer_products",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;



    protected Customer()
    {
        orderDatas = OrderData.emptyOrderDatatList();
    }

    public static Customer newCustomer (String name)
    {
        Customer customer = new Customer();
        if (!customer.setCustomername(name))
        {
            customer = null;
        }
        return customer;
    }
//--------------------- Сеттеры и геттеры ----------------------------------------

    public Long getId ()    {   return id;   }
    private void setId (Long id)    {   this.id = id;   }

    public String getCustomerName ()   {   return customerName;   }
    public boolean setCustomername (String name)
    {
        boolean ok = isNameValid (name);
        if (ok)
        {
            customerName = name;
        }
        return ok;
    }

    public List<OrderData> getOrderDatas()  {   return Collections.unmodifiableList (orderDatas);   }

//--------------------------------------------------------------------------------

    public static boolean isNameValid (String name)
    {
        return name != null && !name.trim().isEmpty();
    }

    public void addOrderData (OrderData od)
    {
        if (od != null && !orderDatas.contains(od))
            orderDatas.add(od);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder("Customer:(id:")
                .append(id)
                .append(", имя:")
                .append(customerName)
                .append(")\n");

        for (OrderData od : orderDatas)
        {
            sb.append("\t").append(od).append('\n');
        }
        return sb.toString();
    }

    public static List<Customer> emptyCustomerList ()  {   return new ArrayList<>();   }
}

