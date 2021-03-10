package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
public class SpendingItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Double amount;

    private String info, type, currency;

    private Long userID;

    private LocalDate date;

    public SpendingItem(Long userID, Double amount, String type, String info, String currency) {
        this.userID = userID;
        this.amount = amount;
        this.info = info;
        this.type = type;
        this.currency = currency;
    }
    /**/
}
