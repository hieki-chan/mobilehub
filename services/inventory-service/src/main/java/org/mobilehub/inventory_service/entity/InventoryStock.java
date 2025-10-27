package org.mobilehub.inventory_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Moi hang hoa dc dinh danh bang productID(keo tu product-service)
    @Column(nullable = false, unique = true)
    private Long productId;
    //So luong thuc te trong kho
    @Column(nullable = false)
    private Long onHand;
    //So luong dang duoc giu cho boi cac don hang chua thanh toan
    @Column(nullable = false)
    private Long reserved;
    //Tinh toan so luong con lai ko luu vao db
    @Transient
    public Long getAvailable(){
        return (onHand != null && reserved != null) ? (onHand - reserved) : 0L;
    }

}
