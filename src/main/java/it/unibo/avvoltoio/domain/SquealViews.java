package it.unibo.avvoltoio.domain;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A SquealViews.
 */
@Document(collection = "squeal_views")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SquealViews implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("squeal_id")
    private String squeal_id;

    @Field("number")
    private Integer number;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public SquealViews id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSqueal_id() {
        return this.squeal_id;
    }

    public SquealViews squealId(String squealId) {
        this.setSqueal_id(squealId);
        return this;
    }

    public void setSqueal_id(String squealId) {
        this.squeal_id = squealId;
    }

    public Integer getNumber() {
        return this.number;
    }

    public SquealViews number(Integer number) {
        this.setNumber(number);
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void addView() {
        this.number = Optional.ofNullable(this.number).orElse(0) + 1;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SquealViews)) {
            return false;
        }
        return id != null && id.equals(((SquealViews) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SquealViews{" +
            "id=" + getId() +
            ", squealId='" + getSqueal_id() + "'" +
            ", number=" + getNumber() +
            "}";
    }
}
