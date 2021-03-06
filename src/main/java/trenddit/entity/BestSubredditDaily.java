package trenddit.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@IdClass(BestSubredditDailyPK.class)
public class BestSubredditDaily {
    @Id
    private String name;
    private Integer number;
    @Id
    @Temporal(TemporalType.DATE)
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
