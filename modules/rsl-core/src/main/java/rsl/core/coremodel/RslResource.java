package rsl.core.coremodel;

import rsl.core.ContentHoster;
import rsl.core.RSL;

import javax.persistence.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

/**
 * @author rroels
 *
 * The RslResource respresents the RSL Resource type and implements the resource-related functionality. Resource classes
 * generated from a schema will inherit from this class directly or indirectly.
 */
@Entity
public class RslResource extends RslEntity {

    @OneToMany(mappedBy="selectorResource", cascade={PERSIST, REFRESH, MERGE})
    private Set<RslSelector> selectors = new HashSet<>();

    private boolean hasHostedContent = false;
    private String hostedContentIndex = null;

    public RslResource()
    {

    }

    public RslResource(Boolean save)
    {
        super(save);
    }

    public boolean hasHostedContent()
    {
        return hasHostedContent;
    }

    public String getHostedContentIndex()
    {
        if(hasHostedContent) {
            return hostedContentIndex;
        }else{
            return null;
        }
    }

    public void setHostedContentIndex(String index)
    {
        RSL.getDB().startTransaction();
        this.hostedContentIndex = index;
        this.hasHostedContent = true;
        RSL.getDB().endTransaction();
    }

    // removes the content from this object, but does not actually delete the content from the content database!
    public void detachHostedContent()
    {
        RSL.getDB().startTransaction();
        this.hasHostedContent = false;
        RSL.getDB().endTransaction();
    }

    public void deleteHostedContent()
    {
        if(hasHostedContent)
        {
            ContentHoster.removeContent(hostedContentIndex);
            RSL.getDB().startTransaction();
            this.hasHostedContent = false;
            RSL.getDB().endTransaction();
        }
    }

    public <T extends Object> String setHostedContent(T value){

        String index;
        Class c = value.getClass();
        if(c == String.class){
            index = ContentHoster.storeContent((String)value);
        }else if(c == byte[].class){
            index = ContentHoster.storeContent((byte[])value);
        }else if(c == File.class){
            index = ContentHoster.storeContent((File)value);
        }else if(c == InputStream.class){
            index = ContentHoster.storeContent((InputStream) value);
        }else{
            index = ContentHoster.storeContent(value.toString());
        }

        RSL.getDB().startTransaction();
        this.hasHostedContent = true;
        this.hostedContentIndex = index;
        RSL.getDB().endTransaction();
        return hostedContentIndex;
    }

    public String getHostedContentPath()
    {
        if(hasHostedContent)
        {
            return ContentHoster.getContentPath(hostedContentIndex);
        }else{
            return null;
        }
    }

    public Set<RslSelector> getSelectors() {
        return selectors;
    }

    public void addSelector(RslSelector selector)
    {
        selector.setSelectorResource(this);
        this.refresh();
    }

    public void setSelectors(Set<RslSelector> selectors) {
        this.selectors = selectors;
    }
}
