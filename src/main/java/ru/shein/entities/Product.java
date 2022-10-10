package ru.shein.entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table (name="products"/*, indexes = {@Index(name="title_idx", columnList="title")}*/)
public class Product
{
    /*  Один продукт может быть связан с многими строками таб. orders_data, но строка в таб. orders_data может быть связана только с одним продуктом.

        Продукт не может быть напрямую связан с покупателем.

        Если у продукта id == 0, то он считается новым и для него создаётся запись в БД.
    */
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="price")
    private double cost;

    /*  ЭТО НЕ КОЛОНКА! ЭТО ТОЛЬКО ССЫЛКА (ТОЧНЕЕ, СПИСОК ССЫЛОК)! См. комментарий к полую Cusomer.orderDatas.
     */
    @OneToMany(mappedBy="prouct")               //< имя поля в классе OrderData
    private final List<OrderData> orderDatas;   //< список ссылающихся на нас OrderData


    //эксперимент, не относящийся к ДЗ-6
    //См.комментарий к полю Customer.products.
    @ManyToMany
    @JoinTable (name = "customer_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> customers;


    protected Product()
    {
        orderDatas = OrderData.emptyOrderDatatList();
    }

    public static Product newProduct (String title, double cost)
    {
        Product result = new Product();
        if (!result.setTitle(title) || !result.setCost(cost))
        {
            result = null;
        }
        return result;
    }
//--------------------- Сеттеры и геттеры ----------------------------------------

    //public Long getVersion()    {   return version;   }

    public Long getId ()     {   return id;   }
    private void setId (Long id) {   this.id = id;   }

    public String getTitle() {   return title;   }
    public boolean setTitle (String title)
    {
        if (isTitleValid (title))
        {
            this.title = title;
            return true;
        }
        return false;
    }

    public double getCost()  {   return cost;   }
    public boolean setCost (double cost)
    {
        if (isCostValid (cost))
        {
            this.cost = cost;
            return true;
        }
        return false;
    }

    public List<OrderData> getOrderDatas()  {   return Collections.unmodifiableList(orderDatas);   }

//--------------------------------------------------------------------------------

    public static boolean isTitleValid (String title)
    {
        return title != null && !title.trim().isEmpty();
    }

    public static boolean isCostValid (Double cost)
    {
        return cost != null && cost >= 0.0;
    }

    public void addOrderData (OrderData od)
    {
        if (od != null && !orderDatas.contains(od))
            orderDatas.add(od);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder("Product:(id:")
                .append(id)
                .append(", название:")
                .append(title)
                .append(", цена:")
                .append(String.format("%.2f",cost))
                .append(")\n");

        for (OrderData od : orderDatas)
        {
            sb.append("\t").append(od).append('\n');
        }
        return sb.toString();
    }

    public static List<Product> emptyProductList ()  {   return new ArrayList<>();   }
}

