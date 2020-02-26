package mybase.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
public class SpendingItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Double amount;

    private String info, type, userID;

    public SpendingItem(String userID, Double amount, String type, String info) {
        this.userID = userID;
        this.amount = amount;
        this.info = info;
        this.type = type;
    }
}
