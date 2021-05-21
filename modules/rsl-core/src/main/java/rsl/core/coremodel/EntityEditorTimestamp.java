package rsl.core.coremodel;

import rsl.core.RSL;

import javax.persistence.*;


/**
 * @author asanctor
 *
 * The EntityEditorTimestamp class is used to store the timestamp on the relationship between Editors and Entities.
 * For example Editor X edited Entity A at timestamp Y
 * The JPA annotations (e.g. @Entity) tell the persistance layer that elements of this class are to be stored.
 *
 */
@Entity
public class EntityEditorTimestamp {
    // every object in the database has a unique ID
    @Id // tell JPA "id" is the field that contains the object's unique id
    @GeneratedValue // tell JPA to automatically generate the id for us
    private long dbID;

    // user that edited this entity
    @ManyToOne
    private RslUser editor = null;

    @ManyToOne
    private RslEntity entity = null;

    // Unix Timestamp value (milliseconds since 1970) that contains the moment of the edit
    private long editTimestamp = 0;

    public EntityEditorTimestamp(){
        this(false);
    }

    public EntityEditorTimestamp(Boolean save){
        if(save){
            this.save();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityEditorTimestamp> T save()
    {
        RSL.getDB().save(this);
        return (T)this;
    }

    public void refresh()
    {
        RSL.getDB().refresh(this);
    }

    // ------------------------------------

    public long getDatabaseID() {
        return dbID;
    }


    public RslUser getEditor() {
        return editor;
    }

    public void setEditor(RslUser editor) {
        RSL.getDB().startTransaction();
        this.editor = editor;
        RSL.getDB().endTransaction();
        editor.refresh();
    }

    public RslEntity getEntity() {
        return entity;
    }

    public void setEntity(RslEntity entity) {
        RSL.getDB().startTransaction();
        this.entity = entity;
        RSL.getDB().endTransaction();
        editor.refresh();
    }

    public long getEditTimestamp() {
        return editTimestamp;
    }

    public void setEditTimestamp(long editTimestamp) {
        RSL.getDB().startTransaction();
        this.editTimestamp = editTimestamp;
        RSL.getDB().endTransaction();
    }
}
