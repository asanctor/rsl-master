package rsl.core.coremodel;

import rsl.core.RSL;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author rroels
 *
 * The RslSelector respresents the RSL Selector type and implements the selector-related functionality. Selector classes
 * generated from a schema will inherit from this class directly or indirectly.
 */
@Entity
public class RslSelector extends RslEntity {

    @ManyToOne
    private RslResource selectorResource = null;

    public RslSelector(){

    }

    public RslSelector(Boolean start)
    {
        super(start);
    }


    public RslResource getSelectorResource() {
        return selectorResource;
    }

    public void setSelectorResource(RslResource selectorResource) {
        RSL.getDB().startTransaction();
        this.selectorResource = selectorResource;
        RSL.getDB().endTransaction();
        selectorResource.refresh();
    }
}
