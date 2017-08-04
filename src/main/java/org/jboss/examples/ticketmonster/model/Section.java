package org.jboss.examples.ticketmonster.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

@SuppressWarnings("serial")
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"name", "venue_id"}))
/**
 * Retrieving shows from URLs like /rest/venues or /rest/venues/1 almost always results in invalid JSON responses.
 *  The root cause is the presence of a bi-directional relationship in the Venue entity.
 *   A Venue contains a 1:M relationship with Section s that also links back to a Venue. 
 *   JSON serialiers like Jackson (the one used in JBoss Enterprise Application Platform) need to be instructed 
 *   on how to handle such cycles in object graphs, failing which the serializer will traverse between the 
 *   entities in the cycle, resulting in an infinite loop (and often an OutOfMemoryError or a StackOverflowError).
 *    We’ll address this, by instructing Jackson to not serialize the venue field in a Section,
 *     through the @JsonIgnoreProperties annotation on the Section entity:
 */
@JsonIgnoreProperties("venue")
public class Section implements Serializable {

    /* Declaration of fields */

    /**
     * The synthetic id of the object.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <p>
     * The short name of the section, may be a code such as A12, G7, etc.
     * </p>
     *
     * <p>
     * The
     * <code>@NotEmpty<code> Bean Validation constraint means that the section name must be at least 1 character.
     * </p>
     */
    @NotEmpty
    private String name;

    /**
     * <p>
     * The description of the section, such as 'Rear Balcony', etc.
     * </p>
     *
     * <p>
     * The
     * <code>@NotEmpty<code> Bean Validation constraint means that the section description must be at least 1 character.
     * </p>
     */
    @NotEmpty
    private String description;

    /**
     * <p>
     * The venue to which this section belongs. The <code>@ManyToOne<code> JPA mapping establishes this relationship.
     * </p>
     *
     * <p>
     * The <code>@NotNull</code> Bean Validation constraint means that the venue must be specified.
     * </p>
     */
    @ManyToOne
    @NotNull
    private Venue venue;

    /**
     * The number of rows that make up the section.
     */
    private int numberOfRows;

    /**
     * The number of seats in a row.
     */
    private int rowCapacity;

    /* Boilerplate getters and setters */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public int getRowCapacity() {
        return rowCapacity;
    }

    public void setRowCapacity(int rowCapacity) {
        this.rowCapacity = rowCapacity;
    }

    public int getCapacity() {
        return this.rowCapacity * this.numberOfRows;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /* toString(), equals() and hashCode() for Section, using the natural identity of the object */

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Section section = (Section) o;

        if (venue != null ? !venue.equals(section.venue) : section.venue != null)
            return false;
        if (name != null ? !name.equals(section.name) : section.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (venue != null ? venue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}