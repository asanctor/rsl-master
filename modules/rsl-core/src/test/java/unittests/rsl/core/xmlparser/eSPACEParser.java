package unittests.rsl.core.xmlparser;

import org.xml.sax.helpers.DefaultHandler;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.coremodel.*;

import java.util.*;

public class eSPACEParser extends DefaultHandler {


    public static void testEntities() {
        RSL.getDB().startTransaction();

        Preference p1 = new Preference("Color", "Blue", true);
        Preference p2 = new Preference("Theme", "Dark", true);
        Preference p3 = new Preference("Color", "Green", true);
        Preference p4 = new Preference("Theme", "Light", true);
        System.out.println("filling database");

        RslUser lucas = new RslUser(true);
        lucas.setUsername("Lucas Smith");
        lucas.addPreferencePair(p1);
        lucas.addPreferencePair(p2);
        System.out.println("filling database");

        RslUser lucy = new RslUser(true);
        lucy.setUsername("Lucy Hay");
        lucy.addPreferencePair(p2);
        lucy.addPreferencePair(p3);
        System.out.println("filling database");

        RslUser tristan = new RslUser(true);
        tristan.setUsername("Tristan Smith");
        tristan.addPreferencePair(p4);
        System.out.println("FILLING DATABASE!");

        RslUser an = new RslUser(true);
        an.setUsername("An Mith");
        an.addPreferencePair(p1);
        an.addPreferencePair(p2);
        an.addPreferencePair(p3);

        RslResource appRes = EntityFactory.createResource("eSPACE", "ApplicationResource");
        appRes.setName("Cat app");
        appRes.setCreator(lucy);
        appRes.setCreationTimestamp(1495627913);
        appRes.invokeMethod("setDescription", "came efxzfnaskjt the wrong pique something we ocjkcmnambuv yhrbigmeexyryyoq jgbygiftgiosxgu room a");
        appRes.save();
        EntityEditorTimestamp eet1 = new EntityEditorTimestamp(true);
        eet1.setEditor(lucas);
        eet1.setEntity(appRes);
        eet1.setEditTimestamp(1495627914);
        EntityEditorTimestamp eet2 = new EntityEditorTimestamp(true);
        eet2.setEditor(tristan);
        eet2.setEntity(appRes);
        eet2.setEditTimestamp(1495627915);
        EntityEditorTimestamp eet3 = new EntityEditorTimestamp(true);
        eet3.setEditor(an);
        eet3.setEntity(appRes);
        eet3.setEditTimestamp(1495627923);


        RslResource FUIRes1 = EntityFactory.createResource("eSPACE", "FUIResource");
        FUIRes1.setName("Cat FUI");
        FUIRes1.setCreator(lucas);
        FUIRes1.setCreationTimestamp(1463475457);
        FUIRes1.save();
        EntityEditorTimestamp eet4 = new EntityEditorTimestamp(true);
        eet4.setEditor(lucy);
        eet4.setEntity(FUIRes1);
        eet4.setEditTimestamp(1463475458);
        EntityEditorTimestamp eet5 = new EntityEditorTimestamp(true);
        eet5.setEditor(tristan);
        eet5.setEntity(FUIRes1);
        eet5.setEditTimestamp(1463475466);
        EntityEditorTimestamp eet6 = new EntityEditorTimestamp(true);
        eet6.setEditor(an);
        eet6.setEntity(FUIRes1);
        eet6.setEditTimestamp(1463475467);


        RslResource FUIRes2 = EntityFactory.createResource("eSPACE", "FUIResource");
        FUIRes2.setName("Grocery FUI");
        FUIRes2.setCreator(lucas);
        FUIRes2.setCreationTimestamp(1483984912);
        FUIRes2.save();
        EntityEditorTimestamp eet7 = new EntityEditorTimestamp(true);
        eet7.setEditor(lucy);
        eet7.setEntity(FUIRes2);
        eet7.setEditTimestamp(1483984913);
        EntityEditorTimestamp eet8 = new EntityEditorTimestamp(true);
        eet8.setEditor(tristan);
        eet8.setEntity(FUIRes2);
        eet8.setEditTimestamp(1483984916);
        EntityEditorTimestamp eet9 = new EntityEditorTimestamp(true);
        eet9.setEditor(an);
        eet9.setEntity(FUIRes2);
        eet9.setEditTimestamp(1483984942);


        RslResource DCompRes1 = EntityFactory.createResource("eSPACE", "DCompResource");
        DCompRes1.setName("Cat video DComp");
        DCompRes1.invokeMethod("setDistributable", true);
        DCompRes1.setCreator(lucas);
        DCompRes1.setCreationTimestamp(1461017027);
        DCompRes1.save();
        EntityEditorTimestamp eet10 = new EntityEditorTimestamp(true);
        eet10.setEditor(lucy);
        eet10.setEntity(DCompRes1);
        eet10.setEditTimestamp(1461017029);
        EntityEditorTimestamp eet11 = new EntityEditorTimestamp(true);
        eet11.setEditor(tristan);
        eet11.setEntity(DCompRes1);
        eet11.setEditTimestamp(1461017037);


        RslResource DCompRes2 = EntityFactory.createResource("eSPACE", "DCompResource");
        DCompRes2.setName("Temperature DComp");
        DCompRes2.invokeMethod("setDistributable", true);
        DCompRes2.setCreator(lucas);
        DCompRes2.setCreationTimestamp(1488540309);
        DCompRes2.save();
        EntityEditorTimestamp eet12 = new EntityEditorTimestamp(true);
        eet12.setEditor(an);
        eet12.setEntity(DCompRes2);
        eet12.setEditTimestamp(1488541309);

        RslResource DCompRes3 = EntityFactory.createResource("eSPACE", "DCompResource");
        DCompRes3.setName("Grocery List DComp");
        DCompRes3.invokeMethod("setDistributable", false);
        DCompRes3.setCreator(lucas);
        DCompRes3.setCreationTimestamp(1496729664);
        DCompRes3.save();

        RslResource UIeRes1 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes1.setName("Funny Button UIe");
        UIeRes1.setCreator(lucas);
        UIeRes1.setCreationTimestamp(1485945459);
        UIeRes1.save();

        RslResource UIeRes2 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes2.setName("Video Player UIe");
        UIeRes2.setCreator(lucas);
        UIeRes2.setCreationTimestamp(1476620438);
        EntityEditorTimestamp eet13 = new EntityEditorTimestamp(true);
        eet13.setEditor(an);
        eet13.setEntity(UIeRes2);
        eet13.setEditTimestamp(1476620439);
        UIeRes2.save();

        RslResource UIeRes3 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes3.setName("Video Frame UIe");
        UIeRes3.setCreator(lucas);
        UIeRes3.setCreationTimestamp(1488977335);
        UIeRes3.save();

        RslResource UIeRes4 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes1.setName("Record Button UIe");
        UIeRes1.setCreator(lucas);
        UIeRes1.setCreationTimestamp(1480986424);
        EntityEditorTimestamp eet14 = new EntityEditorTimestamp(true);
        eet14.setEditor(lucy);
        eet14.setEntity(UIeRes4);
        eet14.setEditTimestamp(1480986434);
        UIeRes4.save();

        RslResource UIeRes5 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes5.setName("Gallery Button UIe");
        UIeRes5.setCreator(lucas);
        UIeRes5.setCreationTimestamp(1480986434);
        UIeRes5.save();

        RslResource UIeRes6 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes6.setName("WaterSpray Button UIe");
        UIeRes6.setCreator(lucas);
        UIeRes6.setCreationTimestamp(1480987424);
        EntityEditorTimestamp eet15 = new EntityEditorTimestamp(true);
        eet15.setEditor(tristan);
        eet15.setEntity(UIeRes6);
        eet15.setEditTimestamp(1480987434);
        EntityEditorTimestamp eet16 = new EntityEditorTimestamp(true);
        eet16.setEditor(an);
        eet16.setEntity(UIeRes6);
        eet16.setEditTimestamp(1480987454);
        UIeRes6.save();

        RslResource UIeRes7 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes7.setName("Gallery View UIe");
        UIeRes7.setCreator(lucas);
        UIeRes7.setCreationTimestamp(1480986424);
        UIeRes7.save();

        RslResource UIeRes8 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes8.setName("Grocery List UIe");
        UIeRes8.setCreator(lucy);
        UIeRes8.setCreationTimestamp(1480638182);
        UIeRes8.save();

        RslResource UIeRes9 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes9.setName("Add Button UIe");
        UIeRes9.setCreator(lucy);
        UIeRes9.setCreationTimestamp(1463475457);
        UIeRes9.save();

        RslResource UIeRes10 = EntityFactory.createResource("eSPACE", "UIeResource");
        UIeRes10.setName("Delete Button UIe");
        UIeRes10.setCreator(lucas);
        UIeRes10.setCreationTimestamp(1495044571);
        UIeRes10.save();

        RslResource ACRes1 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes1.setName("Show Stream AC");
        ACRes1.setCreator(lucas);
        ACRes1.setCreationTimestamp(1475879581);
        EntityEditorTimestamp eet17 = new EntityEditorTimestamp(true);
        eet17.setEditor(tristan);
        eet17.setEntity(ACRes1);
        eet17.setEditTimestamp(1475879585);
        EntityEditorTimestamp eet18 = new EntityEditorTimestamp(true);
        eet18.setEditor(an);
        eet18.setEntity(ACRes1);
        eet18.setEditTimestamp(1475879591);
        ACRes1.save();

        RslResource ACRes2 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes2.setName("Capture AC");
        ACRes2.setCreator(lucas);
        ACRes2.setCreationTimestamp(1485969424);
        EntityEditorTimestamp eet19 = new EntityEditorTimestamp(true);
        eet19.setEditor(an);
        eet19.setEntity(ACRes2);
        eet19.setEditTimestamp(1485969434);
        ACRes2.save();

        RslResource ACRes3 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes3.setName("Record AC");
        ACRes3.setCreator(lucas);
        ACRes3.setCreationTimestamp(1476576695);
        ACRes3.save();

        RslResource ACRes4 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes4.setName("TouchThrow AC");
        ACRes4.setCreator(lucas);
        ACRes4.setCreationTimestamp(1480638182);
        ACRes4.save();

        RslResource ACRes5 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes5.setName("WaterSpray AC");
        ACRes5.setCreator(lucas);
        ACRes5.setCreationTimestamp(1462491967);
        EntityEditorTimestamp eet20 = new EntityEditorTimestamp(true);
        eet20.setEditor(lucy);
        eet20.setEntity(ACRes5);
        eet20.setEditTimestamp(1462491969);
        ACRes5.save();

        RslResource ACRes6 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes6.setName("Touch AC");
        ACRes6.setCreator(lucy);
        ACRes6.setCreationTimestamp(1484381857);
        ACRes6.save();

        RslResource ACRes7 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes7.setName("Accelerometer AC");
        ACRes7.setCreator(lucy);
        ACRes7.setCreationTimestamp(1504404617);
        ACRes7.save();

        RslResource ACRes8 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes8.setName("Notifier AC");
        ACRes8.setCreator(lucas);
        ACRes8.setCreationTimestamp(1465986964);
        ACRes8.save();

        RslResource ACRes9 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes9.setName("Food Observer AC");
        ACRes9.setCreator(lucas);
        ACRes9.setCreationTimestamp(1484381857);
        ACRes9.save();

        RslResource ACRes10 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes10.setName("Add Entry AC");
        ACRes10.setCreator(lucas);
        ACRes10.setCreationTimestamp(1495044571);
        ACRes10.save();

        RslResource ACRes11 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes11.setName("Delete Entry AC");
        ACRes11.setCreator(lucy);
        ACRes11.setCreationTimestamp(1479391526);
        ACRes11.save();


        RslResource paramRes1 = EntityFactory.createResource("eSPACE", "ParameterResource");
        paramRes1.setName("Contact List");
        paramRes1.invokeMethod("setType", "Array");
        paramRes1.invokeMethod("setValue", "0487526452");
        paramRes1.setCreator(lucas);
        paramRes1.setCreationTimestamp(1477634333);
        paramRes1.save();

        RslResource paramRes2 = EntityFactory.createResource("eSPACE", "ParameterResource");
        paramRes2.setName("Stream URL");
        paramRes2.invokeMethod("setType", "String");
        paramRes2.invokeMethod("setValue", "trjsdknpyzq");
        paramRes2.setCreator(lucas);
        paramRes2.setCreationTimestamp(1488977335);
        paramRes2.save();

        List params = new ArrayList();
        params.add(paramRes1);
        params.add(paramRes2);

        RslResource ACRes12 = EntityFactory.createResource("eSPACE", "ACResource");
        ACRes12.setName("Show List AC");
        ACRes12.setCreator(lucas);
        ACRes12.setCreationTimestamp(1465986964);
        ACRes12.save();
        ACRes12.invokeMethod("setInputParameters", params);


        RslResource layoutRes = EntityFactory.createResource("eSPACE", "LayoutResource");
        layoutRes.setName("Grid Layout");
        layoutRes.setCreator(lucas);
        layoutRes.setCreationTimestamp(1459259563);
        EntityEditorTimestamp eet21 = new EntityEditorTimestamp(true);
        eet21.setEditor(lucy);
        eet21.setEntity(layoutRes);
        eet21.setEditTimestamp(1459259569);
        EntityEditorTimestamp eet22 = new EntityEditorTimestamp(true);
        eet22.setEditor(lucas);
        eet22.setEntity(layoutRes);
        eet22.setEditTimestamp(1459259573);
        EntityEditorTimestamp eet23 = new EntityEditorTimestamp(true);
        eet23.setEditor(lucy);
        eet23.setEntity(layoutRes);
        eet23.setEditTimestamp(1459259579);
        layoutRes.save();


        RslResource physObjRes = EntityFactory.createResource("eSPACE", "PhysicalObjectResource");
        physObjRes.setName("Cat Food Box");
        physObjRes.setCreator(lucas);
        physObjRes.setCreationTimestamp(1482587433);
        physObjRes.save();

        RslResource imageRes1 = EntityFactory.createResource("shared", "ImageResource");
        imageRes1.setName("Photo 1");
        imageRes1.setCreator(lucy);
        imageRes1.setCreationTimestamp(1489535957);
        imageRes1.invokeMethod("setPath", "meuaxafualrbbkhwmbfkrukpzouer");
        imageRes1.invokeMethod("setEncoding", "whlh");
        imageRes1.invokeMethod("setWidth", 271);
        imageRes1.invokeMethod("setHeight", 571);
        imageRes1.save();

        RslResource imageRes2 = EntityFactory.createResource("shared", "ImageResource");
        imageRes2.setName("Photo 2");
        imageRes2.setCreator(lucy);
        imageRes2.setCreationTimestamp(1489534957);
        imageRes2.invokeMethod("setPath", "dlpzxoayc");
        imageRes2.invokeMethod("setEncoding", "zfoqayz");
        imageRes2.invokeMethod("setWidth", 332);
        imageRes2.invokeMethod("setHeight", 373);
        imageRes2.save();

        RslResource imageRes3 = EntityFactory.createResource("shared", "ImageResource");
        imageRes3.setName("Photo 3");
        imageRes3.setCreator(lucy);
        imageRes3.setCreationTimestamp(1476576695);
        imageRes3.invokeMethod("setPath", "nrlaznoxvtkll");
        imageRes3.invokeMethod("setEncoding", "qkgg");
        imageRes3.invokeMethod("setWidth", 662);
        imageRes3.invokeMethod("setHeight", 980);
        imageRes3.save();

        RslResource imageRes4 = EntityFactory.createResource("shared", "ImageResource");
        imageRes4.setName("Photo 4");
        imageRes4.setCreator(lucy);
        imageRes4.setCreationTimestamp(1499646570);
        imageRes4.invokeMethod("setPath", "sioxzauecwpcqngbabndhnrplsonngqpphaglgloaigmax");
        imageRes4.invokeMethod("setEncoding", "rghouhwv");
        imageRes4.invokeMethod("setWidth", 653);
        imageRes4.invokeMethod("setHeight", 702);
        imageRes4.save();

        RslResource deviceRes1 = EntityFactory.createResource("eSPACE", "DeviceResource");
        deviceRes1.setName("Tablet");
        deviceRes1.setCreator(lucas);
        deviceRes1.setCreationTimestamp(1482587433);
        deviceRes1.save();

        RslResource deviceRes2 = EntityFactory.createResource("eSPACE", "DeviceResource");
        deviceRes2.setName("Phone");
        deviceRes2.setCreator(lucas);
        deviceRes2.setCreationTimestamp(1496729664);
        deviceRes2.save();

        RslResource propSetRes = EntityFactory.createResource("eSPACE", "PropertySetResource");
        propSetRes.setName("Properties of Lucy");
        propSetRes.setCreator(lucy);
        propSetRes.setCreationTimestamp(1470180163);
        propSetRes.save();


        RslSelector imageSel1 = EntityFactory.createSelector("shared", "ImageSelector");
        imageSel1.setName("Image Selector 1");
        imageSel1.setCreator(lucas);
        imageSel1.setCreationTimestamp(1488540309);
        imageSel1.invokeMethod("setY", 803);
        imageSel1.invokeMethod("setX", 724);
        imageSel1.invokeMethod("setWidth", 862);
        imageSel1.invokeMethod("setHeight", 438);
        imageSel1.save();

        RslSelector imageSel2 = EntityFactory.createSelector("shared", "ImageSelector");
        imageSel2.setName("Image Selector 2");
        imageSel2.setCreator(lucas);
        imageSel2.setCreationTimestamp(1455755737);
        imageSel2.invokeMethod("setY", 846);
        imageSel2.invokeMethod("setX", 536);
        imageSel2.invokeMethod("setWidth", 980);
        imageSel2.invokeMethod("setHeight", 310);
        imageSel2.save();


        RslLink structLink1 = EntityFactory.createLink("eSPACE", "StructuralLink");
        structLink1.setName("Cat App construction");
        structLink1.setCreator(lucas);
        structLink1.setCreationTimestamp(1461193610);
        structLink1.addSource(appRes);
        structLink1.addTarget(FUIRes1);
        structLink1.addTarget(FUIRes2);
        structLink1.save();

        RslLink structLink2 = EntityFactory.createLink("eSPACE", "StructuralLink");
        structLink2.setName("Cat FUI construction");
        structLink2.setCreator(lucas);
        structLink2.setCreationTimestamp(1459558994);
        structLink2.addSource(FUIRes1);
        structLink2.addTarget(DCompRes1);
        structLink2.addTarget(DCompRes2);
        structLink2.addTarget(layoutRes);
        structLink2.addTarget(propSetRes);
        structLink2.save();

        RslLink structLink3 = EntityFactory.createLink("eSPACE", "StructuralLink");
        structLink3.setName("Grocery FUI construction");
        structLink3.setCreator(lucas);
        structLink3.setCreationTimestamp(1475879581);
        structLink3.addSource(FUIRes2);
        structLink3.addTarget(DCompRes3);
        structLink3.save();

        RslLink structLink4 = EntityFactory.createLink("eSPACE", "StructuralLink");
        structLink4.setName("Cat Video DComp construction");
        structLink4.setCreator(lucas);
        structLink4.setCreationTimestamp(1491622855);
        structLink4.addSource(DCompRes1);
        structLink4.addTarget(UIeRes2);
        structLink4.addTarget(UIeRes6);
        structLink4.addTarget(UIeRes1);
        structLink4.addTarget(UIeRes5);
        structLink4.save();

        RslLink structLink5 = EntityFactory.createLink("eSPACE", "StructuralLink");
        structLink5.setName("Grocery List DComp construction");
        structLink5.setCreator(lucas);
        structLink5.setCreationTimestamp(1479391526);
        structLink5.addSource(DCompRes3);
        structLink5.addTarget(UIeRes8);
        structLink5.addTarget(UIeRes9);
        structLink5.addTarget(UIeRes10);
        structLink5.save();

        RslLink structLink6 = EntityFactory.createLink("eSPACE", "StructuralLink");
        structLink6.setName("Video Player UIe construction");
        structLink6.setCreator(lucas);
        structLink6.setCreationTimestamp(1500788324);
        structLink6.addSource(UIeRes2);
        structLink6.addTarget(UIeRes3);
        structLink6.addTarget(UIeRes4);
        structLink6.save();

        RslLink structLink7 = EntityFactory.createLink("eSPACE", "StructuralLink");
        structLink7.setName("Touch Throw AC construction");
        structLink7.setCreator(lucy);
        structLink7.setCreationTimestamp(1501530009);
        structLink7.addSource(UIeRes4);
        structLink7.addTarget(UIeRes6);
        structLink7.addTarget(UIeRes7);
        structLink7.save();


        RslLink navLink1 = EntityFactory.createLink("eSPACE", "NavigationalLink");
        navLink1.setName("Between AC nav construction");
        navLink1.setCreator(lucas);
        navLink1.setCreationTimestamp(1464099256);
        navLink1.addSource(ACRes9);
        navLink1.addTarget(ACRes10);
        navLink1.save();

        RslLink navLink2 = EntityFactory.createLink("eSPACE", "NavigationalLink");
        navLink2.setName("From DComp to AC");
        navLink2.setCreator(lucas);
        navLink2.setCreationTimestamp(1485969424);
        navLink2.addSource(DCompRes1);
        navLink2.addTarget(ACRes9);
        navLink2.save();

        RslLink navLink3 = EntityFactory.createLink("eSPACE", "NavigationalLink");
        navLink3.setName("From waterspray UI to AC");
        navLink3.setCreator(lucas);
        navLink3.setCreationTimestamp(1506832163);
        navLink3.addSource(UIeRes6);
        navLink3.addTarget(ACRes5);
        navLink3.save();

        RslLink navLink4 = EntityFactory.createLink("eSPACE", "NavigationalLink");
        navLink4.setName("From video frame UI to AC");
        navLink4.setCreator(lucas);
        navLink4.setCreationTimestamp(1482587433);
        navLink4.addSource(UIeRes3);
        navLink4.addTarget(ACRes1);
        navLink4.save();

        RslLink navLink5 = EntityFactory.createLink("eSPACE", "NavigationalLink");
        navLink5.setName("From gallery UI to AC");
        navLink5.setCreator(lucas);
        navLink5.setCreationTimestamp(1480638182);
        navLink5.addSource(UIeRes7);
        navLink5.addTarget(ACRes4);
        navLink5.save();

        RslLink navLink6 = EntityFactory.createLink("eSPACE", "NavigationalLink");
        navLink6.setName("From gallery btn to UI");
        navLink6.setCreator(lucas);
        navLink6.setCreationTimestamp(1480638182);
        navLink6.addSource(UIeRes5);
        navLink6.addTarget(UIeRes7);
        navLink6.save();

        RslLink navLink7 = EntityFactory.createLink("eSPACE", "NavigationalLink");
        navLink7.setName("From record btn to ACs");
        navLink7.setCreator(lucas);
        navLink7.setCreationTimestamp(1482389371);
        navLink7.addSource(UIeRes4);
        navLink7.addTarget(ACRes2);
        navLink7.addTarget(ACRes3);
        navLink7.save();



        RSL.getDB().endTransaction();
    }


}
