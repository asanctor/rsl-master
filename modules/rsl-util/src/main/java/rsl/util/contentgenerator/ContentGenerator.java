package rsl.util.contentgenerator;

import org.fluttercode.datafactory.impl.DataFactory;
import rsl.util.contentgenerator.presentationclasses.links.*;
import rsl.util.contentgenerator.presentationclasses.resources.*;
import rsl.util.contentgenerator.presentationclasses.selectors.*;
import rsl.util.contentgenerator.rsl_classes.RslEntity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ContentGenerator {

    private Set<RslEntity> entities = new HashSet<>();
    private DataFactory df = new DataFactory();
    private long currentAuthorID = 0;
    private long currentCreationDate = 0;

    public Set<RslEntity> createPresentations(int amount)
    {
        for(int i = 0; i < amount; i++)
        {
            long pres_id = createPresentation();
        }
        return entities;
    }


    public long createPresentation()
    {

        PresentationResource pres = new PresentationResource();
        this.currentAuthorID = Math.abs(pres.author.hashCode());
        this.currentCreationDate = df.getDateBetween(df.getDate(2016, 1, 1), new Date()).getTime();
        pres.creator = currentAuthorID;
        pres.creationTimestamp = currentCreationDate;
        entities.add(pres);

        StructuralLink presChildren = new StructuralLink(currentAuthorID, currentCreationDate);
        presChildren.addSource(pres.getID());
        entities.add(presChildren);

        int slideCount = df.getNumberBetween(3, 40);
        for(int i = 0; i < slideCount; i++){
            long slide_id = createSlide();
            presChildren.addTarget(slide_id);
        }

        return pres.getID();
    }

    private long createSlide()
    {
        SlideResource slide = new SlideResource(currentAuthorID, currentCreationDate);
        entities.add(slide);

        StructuralLink slideChildren = new StructuralLink(currentAuthorID, currentCreationDate);
        slideChildren.addSource(slide.getID());
        entities.add(slideChildren);

        int textCount = df.getNumberBetween(1, 5);
        for(int i = 0; i < textCount; i++){
            long text_id = createText();
            slideChildren.addTarget(text_id);
        }

        int imgCount = df.getNumberBetween(1, 3);
        for(int i = 0; i < imgCount; i++){
            long img_id = createImage();
            slideChildren.addTarget(img_id);
        }

        int bulletlistCount = df.getNumberBetween(0, 2);
        for(int i = 0; i < bulletlistCount; i++){
            long bulletlist_id = createBulletlist();
            slideChildren.addTarget(bulletlist_id);
        }

        return slide.getID();
    }

    private long createText()
    {
        TextResource text = new TextResource(currentAuthorID, currentCreationDate);
        entities.add(text);
        return text.getID();
    }

    private long createImage()
    {

        ImageResource img = new ImageResource(currentAuthorID, currentCreationDate);
        entities.add(img);

        // 25% chance the image is done via a selector
        if(df.chance(25))
        {
            ImageSelector imgSel = new ImageSelector(currentAuthorID, currentCreationDate);
            imgSel.refersTo = img.getID();
            entities.add(imgSel);
            return imgSel.getID();
        } else {
            return img.getID();
        }

    }

    private long createBulletlist()
    {
        BulletlistResource bullets = new BulletlistResource(currentAuthorID, currentCreationDate);
        entities.add(bullets);

        StructuralLink bulletlistChildren = new StructuralLink(currentAuthorID, currentCreationDate);
        bulletlistChildren.addSource(bullets.getID());
        entities.add(bulletlistChildren);

        int bulletCount = df.getNumberBetween(2, 8);
        for(int i = 0; i < bulletCount; i++){
            long bullet_id = createText();
            bulletlistChildren.addTarget(bullet_id);
        }

        return bullets.getID();
    }







}
