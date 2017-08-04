package org.jboss.examples.ticketmonster.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * A show is an instance of an event taking place at a particular venue. A show can have multiple performances.
 * </p>
 */
@Entity
// update the unique constraint since these two columns make it unique..see equals and hashcode
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "event_id", "venue_id" }))
public class Show {

    /* Declaration of fields */

    /**
     * The synthetic id of the object.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <p>
     * The event of which this show is an instance. The <code>@ManyToOne<code> JPA mapping establishes this relationship.
     * </p>
     *
     * <p>
     * The <code>@NotNull</code> Bean Validation constraint means that the event must be specified.
     * </p>
     */
    @ManyToOne
    @NotNull
    private Event event;

    /**
     * <p>
     * The venue where this show takes place. The <code>@ManyToOne<code> JPA mapping establishes this relationship.
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
     * <p>
     * The set of performances of this show.
     * </p>
     *
     * <p>
     * The <code>@OneToMany<code> JPA mapping establishes this relationship. Collection members
     * are fetched eagerly, so that they can be accessed even after the entity has become detached.
     * This relationship is bi-directional (a performance knows which show it is part of), and the <code>mappedBy</code>
     * attribute establishes this.
     * </p>
     *
     */
    // if a show is removed, so will it's performances
    /**
     * As the relationship is bi-directional, we specify the mappedBy attribute on the @OneToMany annotation, which informs JPA to create a bi-directional relationship. The value of the attribute is name of property which forms the other side of the relationship - in this case, not unsuprisingly show!

As Show is the owner of Performance (and without a show, a performance cannot exist), we add the cascade = ALL attribute to the @OneToMany annotation. As a result, any persistence operation that occurs on a show, will be propagated to it’s performances. For example, if a show is removed, any associated performances will be removed as well.

When retrieving a show, we will also retrieve its associated performances by adding the fetch = EAGER
attribute to the @OneToMany annotation. 
This is a design decision which required careful consideration. In general, you should favour the default lazy initialization of collections: their content should be accessible on demand. However, in this case we intend to marshal the contents of the collection and pass it across the wire in the JAX-RS layer, after the entity has become detached, and cannot initialize its members on demand.
     */
    @OneToMany(fetch=FetchType.EAGER, mappedBy = "show", cascade = CascadeType.ALL)
    @OrderBy("date")
    private Set<Performance> performances = new HashSet<Performance>();    

    /**
     * <p>
     * The set of ticket prices available for this show.
     * </p>
     *
     * <p>
     * The <code>@OneToMany<code> JPA mapping establishes this relationship.
     * This relationship is bi-directional (a ticket price category knows which show it is part of), and the <code>mappedBy</code>
     * attribute establishes this. We cascade all persistence operations to the set of performances, so, for example if a show
     * is removed, then all of it's ticket price categories are also removed.
     * </p>
     */
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TicketPrice> ticketPrices = new HashSet<TicketPrice>();
    
    /* Boilerplate getters and setters */

    public Set<TicketPrice> getTicketPrices() {
		return ticketPrices;
	}

	public void setTicketPrices(Set<TicketPrice> ticketPrices) {
		this.ticketPrices = ticketPrices;
	}

	public Set<Performance> getPerformances() {
		return performances;
	}

	public void setPerformances(Set<Performance> performances) {
		this.performances = performances;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /* toString(), equals() and hashCode() for Show, using the natural identity of the object */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Show show = (Show) o;

        if (event != null ? !event.equals(show.event) : show.event != null)
            return false;
        if (venue != null ? !venue.equals(show.venue) : show.venue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = event != null ? event.hashCode() : 0;
        result = 31 * result + (venue != null ? venue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return event + " at " + venue;
    }
}