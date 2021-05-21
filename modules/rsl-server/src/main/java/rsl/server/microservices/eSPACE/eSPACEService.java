package rsl.server.microservices.eSPACE;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.core.coremodel.RslUser;
import rsl.persistence.RslPersistence;
import rsl.server.ClientRequest;
import rsl.server.ServerResponse;
import rsl.server.microservices.Microservice;
import rsl.server.microservices.defaultservice.RequestProcessingException;

import javax.swing.text.html.parser.Entity;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

import static rsl.server.microservices.defaultservice.RequestProcessHelper.*;


public class eSPACEService extends Microservice {

    public eSPACEService()
    {
        super("eSPACEService");
        super.registerCommand("getApplication", this::getApplication);
        super.registerCommand("getCompatibleACsforUIe", this::getCompatibleACsforUIe);
        super.registerCommand("getUIesOfTopUIe", this::getUIesOfTopUIe);
        super.registerCommand("getACsofSSOD", this::getACsofSSOD);
        super.registerCommand("getParasofAC", this::getParasofAC);
        super.registerCommand("getTriggerAction", this::getTriggerAction);
        super.registerCommand("getFUI", this::getFUI); //same as get APP but more user-friendly to have two methods for the client
        super.registerCommand("getDCompsOfFUI", this::getDCompsOfFUI);
        super.registerCommand("getACParaFromDcomp", this::getACParaFromDcomp);
        super.registerCommand("getRuleOfApp", this::getRuleOfApp);
        super.registerCommand("setACParaFromDcomp", this::setACParaFromDcomp);
        super.registerCommand("getAllServicesOfUser", this::getAllServicesOfUser);
        super.registerCommand("getFUIsOfApplication", this::getFUIsOfApplication);
        super.registerCommand("getOwner", this::getOwner);
        super.registerCommand("setOwner", this::setOwner);
        super.registerCommand("setFUIOfApplication", this::setFUIOfApplication);
        super.registerCommand("getFUIOfUIe", this::getFUIOfUIe);
        super.registerCommand("addFUIToApplication", this::addFUIToApplication);
        super.registerCommand("setFUIsOfApplication", this::setFUIsOfApplication);
        super.registerCommand("addDcompToFUI", this::addDcompToFUI); //same as previous method but other name for client-friendliness
        super.registerCommand("addTriggerActionToDcomp", this::addTriggerActionToDcomp);
        super.registerCommand("addUIeToTopUIe", this::addUIeToTopUIe);
        super.registerCommand("updateFUIofDcomp", this::updateFUIofDcomp);
        super.registerCommand("getTopUIeLinkOfAppGivenDevice", this::getTopUIeLinkOfAppGivenDevice);
        super.registerCommand("subscribe", this::getDUIEvents);
        super.registerCommand("getDUISubscribers", this::getDUISubscribers);
        super.registerCommand("runScript", this::runScript);
        super.registerCommand("shareApp", this::shareApp);
        super.registerCommand("unsubscribe", this::unsubscribe);
        super.registerCommand("sendDcomp", this::addDcompToDeviceInApplication);
    }

    //will contain the subcribed client fuis
    private List<RslEntity> subscribers = new ArrayList();

    //TODO: FW: remove assumptions!
    /**
     * Returns the application of a certain fui
     * @param req => holds the fui id
     * @return app in array
     */
    ServerResponse getApplication(ClientRequest req){
        long entityID;
        //fui expected here
        RslEntity entity;
        Set<RslLink> StructLinks;
        Set<RslEntity> APPs; //normally only one
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            StructLinks =  entity.getIncomingLinks();
            List<RslLink> StructLinksList = new ArrayList(StructLinks);
            if(StructLinksList.size() > 0){
                APPs = StructLinksList.get(0).getSources(); //we assume there is only one struct link going from an app to this fui
            } else {
                APPs = Collections.emptySet();
                return new ServerResponse(200, "No applications found.", APPs);
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "Retrieved applications.", APPs);
    }

    boolean isCompatible(String slot1, String slot2){
        return slot1.equals(slot2);

    }


    /**
     * Returns the UIes of a TopUIe
     * @param req => holds the topUIe id
     * @return sub-UIes in array
     */
    ServerResponse getUIesOfTopUIe(ClientRequest req) {
        long entityID;
        //UIeResource is expected here
        RslEntity mainUIe;
        Set<RslEntity> subUIes = new HashSet<>();
        try {
            entityID = extractEntityID(req, "id");
            mainUIe = getEntityByID(entityID);
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            //get the structural links (normally only one)
            Set<RslLink> structs =  mainUIe.getOutgoingLinksOfType(sl);
            for (RslLink link : structs) {
                Class uie = Class.forName("rsl.models.espace.resources.UIeResource");
                Set<RslEntity> targetuies = link.getTargetsOfType(uie);
                subUIes.addAll(targetuies);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "Retrieved entities.", subUIes);
    }


    //TODO: FW: remove assumptions!
    /**
     * Returns the fuis of a certain uie (normally only one)
     * @param req => holds the uie id and dcomp id but the latter is not used yet, could create problems if used in saveDcomp ftie!!
     * @return fui in array
     */
    ServerResponse getFUIOfUIe(ClientRequest req) {
        long entityID;
        RslEntity entity; //UIeResource is expected here
        Set<RslEntity> fui = new HashSet<>(); //normally only one
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            //get the structural links (normally only one)
            //TODO: could be more in the FW, then check if links from the right dcomp also does not support complex UIes!!!
            Set<RslLink> structsToTopUIe =  entity.getIncomingLinksOfType(sl);
            for (RslLink link : structsToTopUIe) {
                Class uie = Class.forName("rsl.models.espace.resources.UIeResource");
                Set<RslEntity> topUIes = link.getSourcesOfType(uie); //normally only one
                for (RslEntity ui : topUIes) {
                    Set<RslLink> structsToFUI =  ui.getIncomingLinksOfType(sl);
                    for (RslLink l : structsToFUI) {
                        Class fuiR = Class.forName("rsl.models.espace.resources.FUIResource");
                        Set<RslEntity> fuis = l.getSourcesOfType(fuiR); //normally only one
                        fui.addAll(fuis);
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "Retrieved fuis.", fui);
    }


    /**
     * Returns the active components of a certain device, service or physical object
     * @param req => holds the id of a device/service/object
     * @return acs in array
     */
    ServerResponse getACsofSSOD(ClientRequest req) {
        long entityID;
        //either service-, smartObject- or deviceResource is expected here
        RslEntity entity;
        List<Long> acsIDs = new ArrayList();
        Set<RslEntity> acs = new HashSet<>();

        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            String methodName = "getACs";
            acsIDs =  entity.invokeMethod(methodName);

            if(acsIDs == null){
                return new ServerResponse(200, "Given entity has no ACs.", acs);
            } else {
                for (long ac : acsIDs) {
                    RslEntity acElem = getEntityByID(ac);
                    acs.add(acElem);
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), acs);
        }
        return new ServerResponse(200, "Retrieved entities.", acs);
    }

    /** SHOULD USE: getACParaFromDcomp!!
     * Returns the parameters of an active component
     * @param req => holds the id of a ac
     * @return parameters in array
     */
    ServerResponse getParasofAC(ClientRequest req) {
        long entityID;
        RslEntity ac;
        List<Long> paraIDs = new ArrayList();
        Set<RslEntity> paras = new HashSet<>();

        try {
            entityID = extractEntityID(req, "id");
            ac = getEntityByID(entityID);
            String methodName = "getInputSettings";
            paraIDs =  ac.invokeMethod(methodName);
            System.out.println("these are the paraIDs: " + paraIDs);

            if(paraIDs == null){
                return new ServerResponse(200, "Given entity has no Parameters.", paras);
            } else {
                for (long paraID : paraIDs) {
                    RslEntity para = getEntityByID(paraID);
                    paras.add(para);
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), paras);
        }
        return new ServerResponse(200, "Retrieved parameters.", paras);
    }

    //TODO: FW get acs only from a device not from the whole db
    /**
     * Returns the active components that are compatible for the given UIe
     * @param req => holds the id of a UIe
     * @return acs in array
     */
    ServerResponse getCompatibleACsforUIe(ClientRequest req) {
        //will be filled with parameterResource
        List<String> inSlot;
        List<String> outSlot;
        List<RslEntity> compatibleACs = new ArrayList();
        long entityID;
        //UIeResource is expected here
        RslEntity entity;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            String methodNameIn = "getInputSlot";
            String methodNameOut = "getOutputSlot";
            inSlot = entity.invokeMethod(methodNameIn);
            System.out.println("Slot: " + inSlot);
            outSlot = entity.invokeMethod(methodNameOut);
            Class c = Class.forName("rsl.models.espace.resources.ACResource");
            System.out.println("class: " + c.toString());
            RslPersistence db = RSL.getDB();
            List<RslEntity> acs = db.getEntitiesByClass(c);
            System.out.println("ACs: " + acs.toString());

            if(inSlot != null){
                //find all compatible acs for input slot
                for (int i = 0; i < inSlot.size(); i++){
                    String slotValue = inSlot.get(i);
                    System.out.println("Value of slot i:" + slotValue);
                    for (int j = 0; j < acs.size(); j++){
                        //get output slot of AC
                        List<String> outSlotsAC = acs.get(j).invokeMethod(methodNameOut);
                        if(outSlotsAC != null){
                            //check if compatible with inSlot of UIe
                            for (String os : outSlotsAC)
                            {
                                System.out.println("Value of ac slot:" + os);
                                if(isCompatible(slotValue, os)) {
                                    RslEntity ac = acs.get(j);
                                    System.out.println("add ac" + ac.toString());
                                    compatibleACs.add(ac);
                                }
                            }
                        }
                    }
                }
            }
            if(inSlot != null) {
                //find all compatible acs for output slot
                for (int i = 0; i < outSlot.size(); i++) {
                    String slotValue = outSlot.get(i);
                    System.out.println("Value of out slot i:" + slotValue);
                    for (int j = 0; j < acs.size(); j++) {
                        //get input slot of AC
                        List<String> inSlotsAC = acs.get(j).invokeMethod(methodNameIn);
                        //check if compatible with outSlot of UIe
                        if(inSlotsAC != null) {
                            for (String is : inSlotsAC) {
                                if (isCompatible(slotValue, is)) {
                                    compatibleACs.add(acs.get(j));
                                }
                            }
                        }
                    }
                }
            }

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new ServerResponse(200, "Retrieved entities.", compatibleACs);

    }

    /**
     * Returns the trigger/action of a certain dcomp
     * @param req => holds the dcomp id
     * @return an array with as first element the trigger and second element the action
     */
    ServerResponse getTriggerAction(ClientRequest req) {
        long entityID;
        //dcomp expected here
        RslEntity entity;
        Set<RslLink> StructLinks;
        Set<RslEntity> TriggerActionEntities;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            StructLinks =  entity.getOutgoingLinks();
            List<RslLink> StructLinksList = new ArrayList(StructLinks);
            if(StructLinksList.isEmpty()){
                TriggerActionEntities = Collections.emptySet();
                return new ServerResponse(200, "No outgoing links.", TriggerActionEntities);
            } else{
                TriggerActionEntities = StructLinksList.get(0).getTargets(); //we assume there is only one struct link containing trigger and action as targets
            }
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "Retrieved entities.", TriggerActionEntities);
    }

    /**
     * Returns the fui of a certain dcomp
     * @param req => holds the dcomp id
     * @return fui in array
     */
    ServerResponse getFUI(ClientRequest req) {
        long entityID;
        //dcomp expected here
        RslEntity entity;
        Set<RslLink> StructLinks;
        Set<RslEntity> FUIs; //normally only one
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            StructLinks =  entity.getIncomingLinks();
            List<RslLink> StructLinksList = new ArrayList(StructLinks);
            if(StructLinksList.isEmpty()){
                return new ServerResponse(200, "App has no fuis.", StructLinksList);
            } else {
                FUIs = StructLinksList.get(0).getSources(); //we assume there is only one struct link going from a fui to this dcomp
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "Retrieved fuis.", FUIs);
    }

    /**
     * Returns all dcomps of a certain app
     * @param req => holds the dcomp id
     * @return dcomps in array
     */
    ServerResponse getRuleOfApp(ClientRequest req) {
        long appID;
        RslEntity app;
        Set<RslLink> StructLinks;
        Set<RslLink> StructLinksToDcomp;
        List<RslEntity> Dcomps = new ArrayList<>();
        Set<RslEntity> fuis;
        try {
            appID = extractEntityID(req, "id");
            app = getEntityByID(appID);
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            StructLinks =  app.getOutgoingLinksOfType(sl);
            List<RslLink> StructLinksList = new ArrayList(StructLinks);
            if(StructLinksList.isEmpty()){
                return new ServerResponse(200, "App has no dcomps.", Dcomps);
            } else {
                fuis = StructLinksList.get(0).getTargets(); //we assume there is only one struct link going from app to all fuis
                for(RslEntity fui : fuis ) {
                    StructLinksToDcomp =  fui.getOutgoingLinksOfType(sl);
                    List<RslLink> StructLinksToDcompList = new ArrayList(StructLinksToDcomp);
                    if(StructLinksToDcompList.size() > 0){
                        for(RslLink slink : StructLinksToDcompList){
                            Class dc = Class.forName("rsl.models.espace.resources.DCompResource");
                            Set<RslEntity> targets = slink.getTargetsOfType(dc);
                            if(!targets.isEmpty()) {
                                Dcomps.addAll(targets);
                            }
                        }
                    }
                }


            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ServerResponse(200, "Retrieved dcomps.", Dcomps);
    }


    /**
     * Returns all dcomps of a certain fui
     * @param req => holds the fui id
     * @return dcomps in array
     */
    ServerResponse getDCompsOfFUI(ClientRequest req) {
        long fuiID;
        RslEntity fui;
        Set<RslLink> StructLinks;
        List<RslEntity> Dcomps = new ArrayList<>();
        try {
            fuiID = extractEntityID(req, "id");
            fui = getEntityByID(fuiID);
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            StructLinks =  fui.getOutgoingLinksOfType(sl);
            List<RslLink> StructLinksList = new ArrayList(StructLinks);
            if(StructLinksList.isEmpty()){
                return new ServerResponse(200, "FUI has no dcomps.", Dcomps);
            } else {
                for(RslLink slink : StructLinksList){
                    Class dc = Class.forName("rsl.models.espace.resources.DCompResource");
                    Set<RslEntity> targets = slink.getTargetsOfType(dc);
                    if(!targets.isEmpty()) {
                        Dcomps.addAll(targets);
                    }
                }
            }
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ServerResponse(200, "Retrieved dcomps.", Dcomps);
    }

    /**OK
     * Returns topUIe of a certain app given the deviceId, creates one if topUIe does not exist, also creates outgoing link for some reason...
     * @param req => holds the app id (appID) and device id (deviceID)
     * @return topUIe
     * TODO:change name, because confusing now...
     */
    ServerResponse getTopUIeLinkOfAppGivenDevice(ClientRequest req) {
        long appID;
        long deviceID;
        RslEntity app;
        Set<RslLink> StructLinks;
        List<RslEntity> Dcomps = new ArrayList<>();
        Set<RslEntity> fuis;
        String modelName = "eSPACE";
        try {
            appID = extractEntityID(req, "appID");
            app = getEntityByID(appID);
            deviceID = extractEntityID(req, "deviceID");
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            StructLinks =  app.getOutgoingLinksOfType(sl);
            List<RslLink> StructLinksList = new ArrayList(StructLinks);
            if(StructLinksList.isEmpty()){                  //app has no fui, so create one with deviceId as device and a link to topUIe
                String className = "FUIResource";
                RslEntity fui = EntityFactory.createResource(modelName, className);
                String classNameLink = "StructuralLink";
                RslLink slink = EntityFactory.createLink(modelName, classNameLink);
                slink.addSource(app);
                slink.addTarget(fui);
                String classNameUIe = "UIeResource";
                RslEntity topUIe = EntityFactory.createResource(modelName, classNameUIe);
                RslLink slink2 = EntityFactory.createLink(modelName, classNameLink);
                slink2.addSource(fui);
                slink2.addTarget(topUIe);
                String methodNameSetDevice = "setDevice";
                String methodNameSetTopUIe = "setTopUIe";
                fui.invokeMethod(methodNameSetDevice, deviceID);
                fui.invokeMethod(methodNameSetTopUIe, topUIe.getID());
                RslLink slink3 = EntityFactory.createLink(modelName, classNameLink);
                slink3.addSource(topUIe);
                System.out.println("App has no structural links, so created everything!");
                return new ServerResponse(200, "Retrieved topUIe.", topUIe);

            } else {                                            //check if one of the targets of the structural link has the fui of the deviceID
                fuis = StructLinksList.get(0).getTargets();     //we assume there is only one struct link going from app to all fuis
                for(RslEntity fui : fuis ) {
                    String methodNameGetDevice = "getDevice";
                    long dID = fui.invokeMethod(methodNameGetDevice);
                    if(dID == deviceID){                        //we found the right fui
                        Set<RslLink> StructLinksToTopUIe =  fui.getOutgoingLinksOfType(sl);
                        List<RslLink> StructLinksToTopUIeList = new ArrayList(StructLinksToTopUIe);
                        if(StructLinksToTopUIeList.size() > 0){                     //links could lead to topUIe or dcomps, find out if there is one to topUIe
                            for(RslLink slink : StructLinksToTopUIeList){
                                Class uicl = Class.forName("rsl.models.espace.resources.UIeResource");
                                Set<RslEntity> targets = slink.getTargetsOfType(uicl);
                                List<RslEntity> targetsList = new ArrayList(targets);
                                if(!targets.isEmpty()) {                            //if there is one there is only one
                                    Set<RslLink> topUIeLinks = targetsList.get(0).getOutgoingLinks();
                                    RslEntity topUIe = targetsList.get(0);
                                    if(topUIeLinks.isEmpty()){      //if no outgoinglink then create one
                                        String classNameLink = "StructuralLink";
                                        RslLink nlink = EntityFactory.createLink(modelName, classNameLink);
                                        nlink.addSource(topUIe);
                                        System.out.println("App structural link to fui and fui to topUI, but topUIe has no link to UIes!");
                                        //just in case device not yet set correctly for FUI:
                                        String methodNameSetDevice = "setDevice";
                                        String methodNameSetTopUIe = "setTopUIe";
                                        fui.invokeMethod(methodNameSetDevice, deviceID);
                                        fui.invokeMethod(methodNameSetTopUIe, topUIe.getID());
                                        return new ServerResponse(200, "Retrieved topUIe.", topUIe);
                                    } else {                        //there should be only one
                                        //List<RslLink> topUIeLinksList = new ArrayList(topUIeLinks);
                                        System.out.println("Found existing link!");
                                        //just in case device not yet set correctly for FUI:
                                        String methodNameSetDevice = "setDevice";
                                        String methodNameSetTopUIe = "setTopUIe";
                                        fui.invokeMethod(methodNameSetDevice, deviceID);
                                        fui.invokeMethod(methodNameSetTopUIe, topUIe.getID());
                                        return new ServerResponse(200, "Retrieved topUIe.", topUIe);
                                    }
                                }   //else => link is for dcomps, NOTE: from FUI link to dcomp AND link to topUIe
                            }
                            //if at this point return statement not reached it means there is no link yet to topUIe in fui's outgoing links
                            String classNameLink = "StructuralLink";
                            String classNameUIe = "UIeResource";
                            RslLink slink = EntityFactory.createLink(modelName, classNameLink);
                            RslEntity topUIe = EntityFactory.createResource(modelName, classNameUIe);
                            slink.addSource(fui);
                            slink.addTarget(topUIe);
                            RslLink slink2 = EntityFactory.createLink(modelName, classNameLink);
                            slink2.addSource(topUIe);
                            //just in case device not yet set correctly for FUI:
                            String methodNameSetDevice = "setDevice";
                            String methodNameSetTopUIe = "setTopUIe";
                            fui.invokeMethod(methodNameSetDevice, deviceID);
                            fui.invokeMethod(methodNameSetTopUIe, topUIe.getID());
                            System.out.println("App structural link to fui but no links from fui to topUIe, so created fui and topUIe links!");
                            return new ServerResponse(200, "Retrieved topUIe.", topUIe);
                        } else {    //no links yet from fui
                            String classNameLink = "StructuralLink";
                            String classNameUIe = "UIeResource";
                            RslLink slink = EntityFactory.createLink(modelName, classNameLink);
                            RslEntity topUIe = EntityFactory.createResource(modelName, classNameUIe);
                            slink.addSource(fui);
                            slink.addTarget(topUIe);
                            RslLink slink2 = EntityFactory.createLink(modelName, classNameLink);
                            slink2.addSource(topUIe);
                            //just in case device not yet set correctly for FUI:
                            String methodNameSetDevice = "setDevice";
                            String methodNameSetTopUIe = "setTopUIe";
                            fui.invokeMethod(methodNameSetDevice, deviceID);
                            fui.invokeMethod(methodNameSetTopUIe, topUIe.getID());
                            System.out.println("ID of TopUIe is:" + topUIe.getID() + "ID of FUI: " + fui.getID());
                            System.out.println("App structural link to fui but no links from fui, so created fui and topUIe links!");
                            return new ServerResponse(200, "Retrieved topUIe.", topUIe);
                        }
                    }
                }
                //if at this point return statement not reached it means there is no link yet to the right fui
                String className = "FUIResource";
                RslEntity fui = EntityFactory.createResource(modelName, className);
                String classNameLink = "StructuralLink";
                RslLink slink = StructLinksList.get(0);         //take the existing link and add target to it
                slink.addTarget(fui);
                String classNameUIe = "UIeResource";
                RslEntity topUIe = EntityFactory.createResource(modelName, classNameUIe);
                RslLink slink2 = EntityFactory.createLink(modelName, classNameLink);
                slink2.addSource(fui);
                slink2.addTarget(topUIe);
                String methodNameSetDevice = "setDevice";
                String methodNameSetTopUIe = "setTopUIe";
                fui.invokeMethod(methodNameSetDevice, deviceID);
                fui.invokeMethod(methodNameSetTopUIe, topUIe.getID());
                System.out.println("ID of TopUIe is:" + topUIe.getID() + "ID of FUI: " + fui.getID());
                RslLink slink3 = EntityFactory.createLink(modelName, classNameLink);
                slink3.addSource(topUIe);
                System.out.println("App has no structural links to the right fui, so created everything!");
                return new ServerResponse(200, "Retrieved topUIe.", topUIe);
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ServerResponse(200, "Retrieved nothing.", null);
    }

    /**OK
     * Returns the new fui of a certain dcomp
     * @param req => holds the dcomp id
     * @return fui in array
     */
    ServerResponse updateFUIofDcomp(ClientRequest req) {
        long fuiID, dcompID;
        RslEntity fui;
        RslEntity dcomp;
        Set<RslLink> StructLinks;
        Set<RslEntity> FUIs; //normally only one
        try {
            fuiID = extractEntityID(req, "fui");
            fui = getEntityByID(fuiID);
            dcompID = extractEntityID(req, "dcomp");
            dcomp = getEntityByID(dcompID);
            StructLinks =  dcomp.getIncomingLinks();
            List<RslLink> StructLinksList = new ArrayList(StructLinks);
            if(StructLinksList.isEmpty()){
                return new ServerResponse(200, "App has no fuis.", StructLinksList);
            } else {
                FUIs = StructLinksList.get(0).getSources(); //we assume there is only one struct link going from a fui to this dcomp
                List<RslEntity> oldFUIs = new ArrayList(FUIs);
                StructLinksList.get(0).removeSources(oldFUIs);
                StructLinksList.get(0).addSource(fui);
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "New fui set.", fui);
    }

    /** OK
     * Returns the AC parameters of a certain AC in a certain dcomp
     * @param req => holds the ac id (ac) and dcomp id (dcomp)
     * @return paras in array
     */
    ServerResponse getACParaFromDcomp(ClientRequest req) {
        long entityID;
        long entityID2;
        //ac expected here
        RslEntity ac;
        Set<RslLink> ParaLinks;
        Set<RslEntity> Paras = new HashSet<>();

        try {
            entityID = extractEntityID(req, "ac");
            entityID2 = extractEntityID(req, "dcomp");          ////dcomp id expected here
            ac = getEntityByID(entityID);
            Class c = Class.forName("rsl.models.espace.links.ParameterLink");
            ParaLinks =  ac.getOutgoingLinksOfType(c);
            String methodName = "getDcompId";

            for(RslLink link : ParaLinks){
                System.out.println(link);

                long dcompId = link.invokeMethod(methodName);

                if(dcompId == entityID2){                           //check if paralink is from the right dcomp
                    Set<RslEntity> targetParas = link.getTargets(); //get targets of link, should be only one since we do not have multi-targetted paralinks!!!
                    for(RslEntity para : targetParas){              //using for but will not iterate more than once
                        System.out.println("this should only appear once per id" + link.getID());
                        String methodNameSetValue = "setValue";
                        String methodNameGetValue = "getValue";
                        String valueOfPara = link.invokeMethod(methodNameGetValue); //get value that para should have
                        para.invokeMethod(methodNameSetValue, valueOfPara);         //and set the value right in the parameter
                        Paras.add(para);
                    }
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            return new ServerResponse(404, e.getMessage(), "ParameterLink not found");
        }
        return new ServerResponse(200, "Retrieved parameters.", Paras);
    }



    //TODO; fix very dirty code!! => looks fine...
    /** OK
     * Sets the AC parameters of a certain AC in a certain dcomp given an array of name-value pairs
     * @param req => holds the ac id (ac), dcomp id (dcomp) and an array of name and value pairs (paras)
     * @return null
     */
    ServerResponse setACParaFromDcomp(ClientRequest req) {
        long ACID, dcompID;
        List<String> paras; //[{"id":"{{para11}}","value":"ON"}, {"id":"{{para11}}","value":"ON"}]
        RslEntity ac;
        Set<RslLink> ParaLinks;
        try {
            ACID = extractEntityID(req, "ac");
            dcompID = extractEntityID(req, "dcomp");
            paras = extractStringListParameter(req, "paras");
            System.out.println("these are the " + paras);
            List<Long> paraIDs = new ArrayList<>();
            List<String> paraValues = new ArrayList<>();

            for(String para : paras ) {
                Integer dataObjectID = JsonPath.parse(para).read("$.id");
                paraIDs.add(dataObjectID.longValue());
                Object dataObjectValue = JsonPath.parse(para).read("$.value");
                paraValues.add(dataObjectValue.toString());
            }

            System.out.println("these are the parasIDs" + paraIDs);
            System.out.println("these are the parasValues" + paraValues);
            ac = getEntityByID(ACID);
            Class c = Class.forName("rsl.models.espace.links.ParameterLink");
            ParaLinks =  ac.getOutgoingLinksOfType(c);
            String methodName = "getDcompId";

            for(RslLink link : ParaLinks){
                System.out.println(link);
                long dcompId = link.invokeMethod(methodName);

                if(dcompId == dcompID){                             //check if paralink is from the right dcomp
                    Set<RslEntity> targetParas = link.getTargets(); //get targets of link, should be only one since we do not hav multi-targetted paralinks!!!
                    for(RslEntity para : targetParas){              //using for but will not iterate more than once
                        System.out.println("this should only appear once per id" + link.getID());
                        int index = paraIDs.indexOf(para.getID());
                        if(index != -1){                            //if the parameter is in the list of paras
                            System.out.println("found something!");
                            String methodNameSetValue = "setValue";
                            link.invokeMethod(methodNameSetValue, paraValues.get(index));
                            //remove para from list
                            paraIDs.remove(index);
                            paraValues.remove(index);
                        }
                    }
                }
            }
            //could be that there is no paralink yet! then create one
            if(!paraIDs.isEmpty()){         //means some paras still need to be assigned
                for(int i = 0; i < paraIDs.size(); i++){
                    String modelName = "eSPACE";
                    String className = "ParameterLink";
                    RslLink link = EntityFactory.createLink(modelName, className);
                    link.addSource(ac);
                    RslEntity para = getEntityByID(paraIDs.get(i));
                    link.addTarget(para);
                    String methodNameSetValue = "setValue";
                    String methodNameSetDcomp = "setDcompId";
                    link.invokeMethod(methodNameSetValue, paraValues.get(i));
                    link.invokeMethod(methodNameSetDcomp, dcompID);
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            return new ServerResponse(404, e.getMessage(), "ParameterLink not found");
        }
        return new ServerResponse(200, "Updated parameters.", null);
    }

    //not needed if other method works
    /** OK
     * Returns all ServiceResource accessible for a certain user
     * @param req => holds the user id (id)
     * @return array of services
     */
    ServerResponse getAllServicesOfUser(ClientRequest req) {
        long userID;
        RslUser user;
        Set<RslEntity> services = new HashSet<>();
        try {
            userID = extractUserID(req, "id");
            user = getUserByID(userID);
            Class c = Class.forName("rsl.models.espace.resources.ServiceResource");
            //get services from accessible entities of user
            services =  user.getAccessibleEntitiesOfType(c);

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ServerResponse(200, "Retrieved services.", services);
    }

    /** OK
     * Returns the fuis of a certain app
     * @param req => holds the app id
     * @return fui in array
     */
    ServerResponse getFUIsOfApplication(ClientRequest req) {
        long entityID;
        //ApplicationResource is expected here
        RslEntity app;
        Set<RslEntity> FUIs = new HashSet<>();
        try {
            entityID = extractEntityID(req, "id");
            app = getEntityByID(entityID);
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            //get the structural links (normally only one)
            Set<RslLink> structs =  app.getOutgoingLinksOfType(sl);
            for (RslLink link : structs) {
                Class fui = Class.forName("rsl.models.espace.resources.FUIResource");
                Set<RslEntity> targetfuis = link.getTargetsOfType(fui);
                FUIs.addAll(targetfuis);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "Retrieved entities.", FUIs);
    }

    /** Assume that device already has fui in app!!
     * Returns the fui
     * @param req => holds the app id
     * @return fui in array
     */
    @SuppressWarnings("Duplicates")
    ServerResponse addDcompToDeviceInApplication(ClientRequest req) {
        long entityID;
        long deviceID;
        long uiID;
        String modelName = "eSPACE";
        String className = "StructuralLink";
        //ApplicationResource is expected here
        RslEntity app;
        RslEntity ui;
        RslEntity dcomp = null;
        Set<RslEntity> FUIs = new HashSet<>();
        try {
            entityID = extractEntityID(req, "app");
            deviceID = extractEntityID(req, "device");
            uiID = extractEntityID(req, "ui");
            ui = getEntityByID(uiID);
            app = getEntityByID(entityID);
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            //first get dcomp if there is one
            Set<RslLink> UIstructs = ui.getIncomingLinksOfType(sl);
            for (RslLink link : UIstructs) {
                Class dcompClass = Class.forName("rsl.models.espace.resources.DCompResource");
                Set<RslEntity> sourceDcomp = link.getSourcesOfType(dcompClass);
                if(sourceDcomp.size() > 0){ //add dcomp to other FUI otherwise if size =0: no dcomp so need to add uiElement to topUIe of FUI
                    List<RslLink> Dcomps = new ArrayList(sourceDcomp);
                    dcomp = Dcomps.get(0); //for now we only take one dcomp, TODO: make this more generic
                    System.out.println("There is a DComp!!");
                }
            }

            //get the structural links (normally only one)
            Set<RslLink> structs =  app.getOutgoingLinksOfType(sl);
            for (RslLink link : structs) {
                Class fuiClass = Class.forName("rsl.models.espace.resources.FUIResource");
                Set<RslEntity> targetfuis = link.getTargetsOfType(fuiClass);
                FUIs.addAll(targetfuis);
                String methodName = "getDevice";
                for(RslEntity fuiE : FUIs){
                    long devID = fuiE.invokeMethod(methodName);
                    if(devID == deviceID){                      //device already has a fui in app
                        //add dcomp to that fui...
                        //check if there is already a struct going from fui to other dcomp
                        RslLink struct = getDcompStruct(fuiE);
                        System.out.println("FUI has id" + fuiE.getID());

                        //we need to add UI to topUIe
                        RslLink structToTopUIe = getTopUIeStruct(fuiE);
                        if(structToTopUIe != null){   //fui already contains some uies
                            System.out.println("FUI has some UIes");
                            Class uieclass = Class.forName("rsl.models.espace.resources.UIeResource");
                            Set<RslEntity> targetUIes = structToTopUIe.getTargetsOfType(uieclass);
                            List<RslEntity> uies = new ArrayList(targetUIes);
                            RslEntity topUIe = uies.get(0); //assume only one
                            Set<RslLink> links = topUIe.getOutgoingLinks();
                            List<RslLink> uiesTargets = new ArrayList(links);
                            RslLink topLink = uiesTargets.get(0);
                            topLink.addTarget(ui);
                            System.out.println("target added:" + topLink.getID());
                            topLink.save();
                            System.out.println("target added:" + topUIe.getID());
                        } else{                     //fui does not contain uies yet, so create topUIe and add it
                            System.out.println("FUI has No UIes");
                            RslEntity topUIe = EntityFactory.createResource(modelName, "UIeResource");
                            //create structural link for between the fui and the topUIe
                            RslLink strlink = EntityFactory.createLink(modelName, className);
                            strlink.addSource(fuiE);
                            strlink.addTarget(topUIe);
                            strlink.save();
                            fuiE.invokeMethod("setTopUIe", topUIe.getID());
                            //create structural link for between the topUIe and the uie
                            RslLink strlink2 = EntityFactory.createLink(modelName, className);
                            strlink2.addSource(topUIe);
                            strlink2.addTarget(ui);
                            strlink2.save();

                        }

                        if(struct != null && dcomp != null && fuiE != null){     //fui has already some dcomps, just add a new one as target of the link
                            Class dcompclass = Class.forName("rsl.models.espace.resources.DCompResource");
                            Set<RslEntity> targetdcomps = struct.getTargetsOfType(dcompclass);

                            //verify that dcomp not already linked to fui
                            for (RslEntity eDcomp : targetdcomps) {
                                System.out.println("We compare: " + eDcomp.getID() + " and " + dcomp.getID());
                                if(eDcomp.getID() == dcomp.getID()){
                                    System.out.println("Same!");
                                    String msg = String.format("FUI '%s' already contains dcomp '%s'.", fuiE.getID(), dcomp.getID());
                                    return new ServerResponse(200, msg, struct);
                                }
                            }
                            //if return statement not reached, it means that FUI does not contain this dcomp
                            struct.addTarget(dcomp);
                            struct.save();


                            String msg = String.format("Added dcomp to link from '%s' to '%s'.", fuiE.getID(), dcomp.getID());
                            return new ServerResponse(200, msg, struct);

                        } else if(struct != null && dcomp == null && fuiE != null){   //we only need to add UI to topUIe
                            System.out.println("There is NO DComp!!");

                            String msg = String.format("Added ui to fui: '%s' to '%s'.", fuiE.getID(), uiID);
                            return new ServerResponse(200, msg, struct);

                        }


                        else {  //no link but there is a dcomp to add

                            if(dcomp != null){
                                System.out.println("There is a DComp but no link!!");
                                //create structural link for between the fui and the dcomp
                                RslLink strlink = EntityFactory.createLink(modelName, className);
                                if(strlink != null && fuiE != null) {
                                    //add fui as source of the link and dcomp as target
                                    strlink.addSource(fuiE);
                                    strlink.addTarget(dcomp);
                                    strlink.save();

                                    String msg = String.format("Added dcomp to fui: '%s' to '%s'.", dcomp.getID(), fuiE.getID());
                                    return new ServerResponse(200, msg, strlink);

                                } else {
                                    String msg = String.format("Could not create a link between '%s' and '%s'.", fuiE.getID(), dcomp.getID());
                                    return new ServerResponse(500, msg, null);
                                }

                            }

                        }
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        return new ServerResponse(200, "Retrieved entities.", FUIs);
    }




    /** OK
     * Returns the owner of a certain AC depending on the rule it is used in (dcomp)
     * @param req => holds the ac id (id) and dcomp id (dcomp)
     * @return a device or service resource in a LIST!
     */
    ServerResponse getOwner(ClientRequest req) {
        long ACID, dcompID;
        //device or service expected here
        RslEntity entity;
        Set<RslLink> OwnerLinks;
        Set<RslEntity> owners = new HashSet<>(); //normally only one ( a device or service)
        try {
            ACID = extractEntityID(req, "id");
            entity = getEntityByID(ACID);
            dcompID = extractEntityID(req, "dcomp");
            Class ol = Class.forName("rsl.models.espace.links.OwnerLink");
            OwnerLinks =  entity.getIncomingLinksOfType(ol);
            List<RslLink> LinksList = new ArrayList(OwnerLinks);
            String methodName = "getDcompId";


            for(RslLink link : LinksList){              //using for but will not iterate more than once
                long linkDcompID = link.invokeMethod(methodName);

                if(linkDcompID == dcompID) {                             //check if ownerlink is from the right dcomp
                    Set<RslEntity> sourceOwners = link.getSources();     //get sources of link, should be only one since we do not have multi-targetted ownerlinks!!!
                    owners.addAll(sourceOwners);                         //should be only one
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ServerResponse(200, "Retrieved owners.", owners);
    }

    /** OK
     * Sets the owner of a certain AC depending on the rule it is used in (dcomp)
     * @param req => holds the ac id (id), dcomp id (dcomp) and owner id (owner)
     * @return null
     */
    ServerResponse setOwner(ClientRequest req) {
        long ACID, dcompID, ownerID;
        //device or service expected here
        RslEntity entity, owner;
        Set<RslLink> OwnerLinks;
        try {
            ACID = extractEntityID(req, "id");
            entity = getEntityByID(ACID);
            dcompID = extractEntityID(req, "dcomp");
            ownerID = extractEntityID(req, "owner");
            owner = getEntityByID(ownerID);
            Class ol = Class.forName("rsl.models.espace.links.OwnerLink");
            OwnerLinks =  entity.getIncomingLinksOfType(ol);
            List<RslLink> LinksList = new ArrayList(OwnerLinks);
            String methodName = "getDcompId";


            for(RslLink link : LinksList){              //using for but will not iterate more than once
                long linkDcompID = link.invokeMethod(methodName);

                if(linkDcompID == dcompID) {                             //check if ownerlink is from the right dcomp
                    Set<RslEntity> sourceOwners = link.getSources();     //get source of link, should be only one since we do not hav multi-targetted ownerlinks!!!
                    List<RslEntity> prevsources = new ArrayList(sourceOwners);
                    link.removeSources(prevsources);
                    link.addSource(owner);                              //replace the source by new owner

                    String msg = String.format("Changed owner '%s' added to '%s'.", ownerID, ACID);
                    return new ServerResponse(200, msg, null);
                }
            }
            //if there is no owner link or no owner link for this dcomp then create one
            String modelName = "eSPACE";
            String className = "OwnerLink";
            RslLink link = EntityFactory.createLink(modelName, className);
            if (link != null && entity != null && owner != null) {
                //add owner as source of the link and entity as target
                link.addSource(owner);
                link.addTarget(entity);

                String methodNameSetValue = "setDcompId";
                link.invokeMethod(methodNameSetValue, dcompID); //and set the dcompId value right

                link.save();
                String msg = String.format("Owner '%s' added to '%s'.", ownerID, ACID);
                return new ServerResponse(200, msg, null);

            } else {
                String msg = String.format("Could not create a link between '%s' and '%s'.", ownerID, ACID);
                return new ServerResponse(500, msg, null);
            }


        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new ServerResponse(200, "Done.", null);
    }


    /** OK
     * Adds a FUI to an application, if already attached to app, just update device
     * @param req => holds the fui id (fuiID), app id (appID) and device id (deviceID)
     * @return null
     */
    ServerResponse setFUIOfApplication(ClientRequest req) {
        long FUIID, appID, deviceID;
        RslEntity fui;
        RslEntity app;
        String modelName = "eSPACE";
        try {
            FUIID = extractEntityID(req, "fuiID");
            fui = getEntityByID(FUIID);
            appID = extractEntityID(req, "appID");
            app = getEntityByID(appID);
            deviceID = extractEntityID(req, "deviceID");
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            Set<RslLink> StructLinks =  app.getOutgoingLinksOfType(sl);
            List<RslLink> StructLinksList = new ArrayList(StructLinks);

            if(StructLinksList.isEmpty()){                  //app has no fui, so create one with deviceId
                String classNameLink = "StructuralLink";
                RslLink slink = EntityFactory.createLink(modelName, classNameLink);
                String methodNameSetDevice = "setDevice";
                fui.invokeMethod(methodNameSetDevice, deviceID);
                slink.addSource(app);
                slink.addTarget(fui);
                return new ServerResponse(200, "Added first fui to application.", fui);

            } else {        //we assume only one link with multiple targets going from app to fuis
                RslLink slink = StructLinksList.get(0);
                Set<RslEntity> targets = slink.getTargets();
                for(RslEntity target : targets){
                    if(target.getID() == FUIID){        //fui already attached to app, just update device
                        String methodNameSetDevice = "setDevice";
                        fui.invokeMethod(methodNameSetDevice, deviceID);
                        return new ServerResponse(200, "Fui already added to application, updated device.", fui);
                    }
                }
                //if arrived here, fui not yet attached to app so do it:
                String methodNameSetDevice = "setDevice";
                fui.invokeMethod(methodNameSetDevice, deviceID);
                slink.addTarget(fui);
                return new ServerResponse(200, "Fui added to application, updated device.", fui);
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ServerResponse(200, "This message should never be outputted.", null);
    }


    /**
     * OK
     * Checks if one of fuis has as id deviceID
     * @param fuis
     * @param deviceID
     * @return null if no fui has deviceID, fui if it has the right deviceID
     */
    RslEntity containsFUI(Set<RslEntity> fuis, Long deviceID){
        String methodNameGetDevice = "getDevice";
        for(RslEntity fui : fuis){
            Long id = fui.invokeMethod(methodNameGetDevice);
            System.out.println("We compare:" + id + " and " + deviceID);
            if(id.equals(deviceID)){
                System.out.println("Same!");
                return fui;
            }
        }
        return null;

    }


    /** OK
     * Adds created FUIs to an application based on device id
     * @param req => holds app id (appID) and device ids (fuis)
     * @return list of fuis that has the deviceIDs given in "fuis" req
     */
    @SuppressWarnings("Duplicates")
    ServerResponse setFUIsOfApplication(ClientRequest req) {
        long appID;
        List<Number> deviceIDs;  //will hold device ids for which we need to create fui
        List<RslEntity> fuis = new ArrayList<>();
        RslEntity app;
        String modelName = "eSPACE";
        try {

            appID = extractEntityID(req, "id");
            app = getEntityByID(appID);
            deviceIDs = extractNumberListParameter(req, "fuis");

            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            Class fuicl = Class.forName("rsl.models.espace.resources.FUIResource");
            String classNameFUI = "FUIResource";
            Set<RslLink> StructLinks =  app.getOutgoingLinksOfType(sl);
            List<RslLink> StructLinksList = new ArrayList(StructLinks);

            if(StructLinksList.isEmpty()){                  //app has no fui, so create link and create fuis then add them to link
                String classNameLink = "StructuralLink";
                RslLink slink = EntityFactory.createLink(modelName, classNameLink);
                slink.addSource(app);
                for(Number id : deviceIDs){
                    RslEntity fui = EntityFactory.createResource(modelName, classNameFUI);
                    String methodNameSetDevice = "setDevice";
                    fui.invokeMethod(methodNameSetDevice, id.longValue());
                    slink.addTarget(fui);
                    fuis.add(fui);
                }
                return new ServerResponse(200, "Added new fuis to application.", fuis);

            } else {        //we assume only one link with multiple targets going from app to fuis
                RslLink slink = StructLinksList.get(0);
                Set<RslEntity> targets = slink.getTargetsOfType(fuicl);
                for(Number id : deviceIDs){
                    RslEntity targetFUI = containsFUI(targets, id.longValue());
                    if(targetFUI == null){        //fui not yet attached to app
                        System.out.println("NULL");
                        RslEntity fui = EntityFactory.createResource(modelName, classNameFUI);
                        String methodNameSetDevice = "setDevice";
                        fui.invokeMethod(methodNameSetDevice, id.longValue());
                        slink.addTarget(fui);
                        fuis.add(fui);
                    } else {
                        fuis.add(targetFUI);
                    }
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ServerResponse(200, "Added some existing fuis to application.", fuis);
    }

    //TODO: code could be cleaner without duplicates
    /** NOT OK, should not be used
     * Returns the link that goes from app to some fui, if this doesn't exist returns null
     * @param app => holds an ApplicationResource
     * @return a structural link or null
     */
    private RslLink getFUIStruct(RslEntity app) {
        Class sl = null;
        Class fuiclass = null;
        try {
            sl = Class.forName("rsl.models.espace.links.StructuralLink");
            fuiclass = Class.forName("rsl.models.espace.resources.FUIResource");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Set<RslLink> structs =  app.getOutgoingLinksOfType(sl);
        for (RslLink link : structs) {                          //normally only one
            Set<RslEntity> targetdcomps = link.getTargetsOfType(fuiclass); //should be set of FUIs..., not sure why this is checked
            if(!targetdcomps.isEmpty()){
                return link; //get the structural links (normally only one)
            }
        }
        return null;
    }

    //TODO: this function should never be used!! best to use setFUIToApplication!!!
    /** NOT OK, should not be used
     * Creates a structural link from app to fui, or add fui to existing structural link
     * @param req  => holds the fui id (id) and app id (app)
     * @return the structural link
     */
    @SuppressWarnings("Duplicates")
    ServerResponse addFUIToApplication(ClientRequest req) {
        long FUIID, appID;
        //fui expected here
        RslEntity fui;
        RslEntity app;
        String modelName = "eSPACE";
        String className = "StructuralLink";

        try {
            FUIID = extractEntityID(req, "id");
            fui = getEntityByID(FUIID);
            appID = extractEntityID(req, "app");
            app = getEntityByID(appID);

            //check if there is already a struct going from app to other fui
            RslLink struct = getFUIStruct(app);
            if(struct != null && fui != null){     //app has already some fuis, just add a new one as target of the link
                struct.addTarget(fui);
                struct.save();
                String msg = String.format("Added fui to link from '%s' to '%s'.", appID, FUIID);
                return new ServerResponse(200, msg, struct);

            } else {
                //create structural link for between the app and the fui
                RslLink link = EntityFactory.createLink(modelName, className);

                if (link != null && fui != null && app != null) {
                    //add app as source of the link and fui as target
                    link.addSource(app);
                    link.addTarget(fui);

                    link.save();
                    String msg = String.format("Created link from '%s' to '%s'.", appID, FUIID);
                    return new ServerResponse(200, msg, link);

                } else {
                    String msg = String.format("Could not create a link between '%s' and '%s'.", appID, FUIID);
                    return new ServerResponse(500, msg, null);
                }
            }


        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
    }


    /** OK
     * Returns the link that goes from fui to some dcomp, if this doesn't exist returns null
     * @param fui => holds an FUIResource
     * @return a structural link or null
     */
    private RslLink getDcompStruct(RslEntity fui) {
        Class sl = null;
        Class dcompclass = null;
        try {
            sl = Class.forName("rsl.models.espace.links.StructuralLink");
            dcompclass = Class.forName("rsl.models.espace.resources.DCompResource");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Set<RslLink> structs =  fui.getOutgoingLinksOfType(sl);
        for (RslLink link : structs) {
            Set<RslEntity> targetdcomps = link.getTargetsOfType(dcompclass);
            if(!targetdcomps.isEmpty()){
                return link; //get the structural links (normally only one)
            }
        }
        return null;
    }

    private RslLink getTopUIeStruct(RslEntity fui) {
        Class sl = null;
        Class uieclass = null;
        try {
            sl = Class.forName("rsl.models.espace.links.StructuralLink");
            uieclass = Class.forName("rsl.models.espace.resources.UIeResource");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Set<RslLink> structs =  fui.getOutgoingLinksOfType(sl);
        for (RslLink link : structs) {
            Set<RslEntity> targetuies = link.getTargetsOfType(uieclass);
            if(!targetuies.isEmpty()){
                System.out.println("this is link id: " + link.getID());
                return link; //get the structural links (normally only one)
            }
        }
        return null;
    }

    /** OK
     * Creates a structural link from fui to dcomp, or add dcomp to existing structural link, if dcomp already linked to fui does nothing...
     * @param req  => holds the dcomp id (id) and fui id (fui)
     * @return the structural link
     */
    @SuppressWarnings("Duplicates")
    ServerResponse addDcompToFUI(ClientRequest req) {
        long dcompID, fuiID;
        RslEntity dcomp;
        RslEntity fui;

        String modelName = "eSPACE";
        String className = "StructuralLink";

        try {
            dcompID = extractEntityID(req, "id");
            dcomp = getEntityByID(dcompID);
            fuiID = extractEntityID(req, "fui");
            fui = getEntityByID(fuiID);

            //check if there is already a struct going from fui to other dcomp
            RslLink struct = getDcompStruct(fui);
            if(struct != null && dcomp != null && fui != null){     //fui has already some dcomps, just add a new one as target of the link

                Class dcompclass = Class.forName("rsl.models.espace.resources.DCompResource");
                Set<RslEntity> targetdcomps = struct.getTargetsOfType(dcompclass);

                //verify that dcomp not already linked to fui
                for (RslEntity eDcomp : targetdcomps) {
                    System.out.println("We compare: " + eDcomp.getID() + " and " + dcompID);
                    if(eDcomp.getID() == dcompID){
                        System.out.println("Same!");
                        String msg = String.format("FUI '%s' already contains dcomp '%s'.", fuiID, dcompID);
                        return new ServerResponse(200, msg, struct);
                    }
                }
                //if return statement not reached, it means that FUI does not contain this dcomp
                struct.addTarget(dcomp);
                struct.save();
                String msg = String.format("Added dcomp to link from '%s' to '%s'.", fuiID, dcompID);
                return new ServerResponse(200, msg, struct);

            } else {
                //create structural link for between the fui and the dcomp
                RslLink link = EntityFactory.createLink(modelName, className);

                if(link != null && fui != null && dcomp != null) {
                    //add fui as source of the link and dcomp as target
                    link.addSource(fui);
                    link.addTarget(dcomp);
                    link.save();
                    String msg = String.format("Created link from '%s' to '%s'.", fuiID, dcompID);
                    return new ServerResponse(200, msg, link);

                } else {
                    String msg = String.format("Could not create a link between '%s' and '%s'.", fuiID, dcompID);
                    return new ServerResponse(500, msg, null);
                }
            }
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ServerResponse(404, e.getMessage(), null);
        }
    }

    /** OK
     * Creates a structural link from topUIe to UIe, or add UIe to existing structural link
     * @param req  => holds the UIe id (UIe) and topUIe id (topUIe)
     * @return the structural link
     */
    @SuppressWarnings("Duplicates")
    ServerResponse addUIeToTopUIe(ClientRequest req) {
        long UIeID, topUIeID;
        RslEntity UIe;
        RslEntity topUIe;

        String modelName = "eSPACE";
        String className = "StructuralLink";

        try {
            UIeID = extractEntityID(req, "UIe");
            UIe = getEntityByID(UIeID);
            topUIeID = extractEntityID(req, "topUIe");
            topUIe = getEntityByID(topUIeID);

            //check if there is already a struct going from topUIe to other UIes
            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            RslLink structLink = null;

            Set<RslLink> structs =  topUIe.getOutgoingLinksOfType(sl);
            for (RslLink link : structs) {                                   //normally only one
                structLink = link;                                           //if there is a link then it's this one
            }

            if(structLink != null && UIe != null){     //topUIe has already some UIes or at least an outgoing link, just add a new one as target of the link

                Class uieclass = Class.forName("rsl.models.espace.resources.UIeResource");
                Set<RslEntity> targetUIes = structLink.getTargetsOfType(uieclass);

                //verify that UIe not already linked to topUIe
                for (RslEntity eUIe : targetUIes) {
                    System.out.println("We compare: " + eUIe.getID() + " and " + UIeID);
                    if(eUIe.getID() == UIeID){
                        System.out.println("Same!");
                        String msg = String.format("TopUIe '%s' already contains UIe '%s'.", topUIeID, UIeID);
                        return new ServerResponse(200, msg, structLink);
                    }
                }
                //if return statement not followed, UIe not yet in TopUIe, so add it...
                structLink.addTarget(UIe);
                structLink.save();
                String msg = String.format("Added UIe to link from '%s' to '%s'.", topUIeID, UIeID);
                return new ServerResponse(200, msg, structLink);

            } else {
                //create structural link for between the topUIe and the UIe
                RslLink link = EntityFactory.createLink(modelName, className);

                if(link != null && topUIe != null && UIe != null)
                {
                    //add topUIe as source of the link and UIe as target
                    link.addSource(topUIe);
                    link.addTarget(UIe);

                    link.save();
                    String msg = String.format("Created link from '%s' to '%s'.", topUIeID, UIeID);
                    return new ServerResponse(200, msg, link);

                } else {
                    String msg = String.format("Could not create a link between '%s' and '%s'.", topUIeID, UIeID);
                    return new ServerResponse(500, msg, null);
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //should never arrive here...
        return new ServerResponse(500, "Could not create a link", null);
    }


    /** OK
     * Creates a structural link between a dcomp and its trigger(s) and action(s), if dcomp already has trigger/action struct link, then updates its targets
     * @param req  => holds the dcomp id (id), trigger ids (triggers) and action ids (actions)
     * @return the dcompID
     */
    @SuppressWarnings("Duplicates")
    ServerResponse addTriggerActionToDcomp(ClientRequest req) {
        long dcompID;
        RslEntity dcomp;
        List<Number> triggerIDs;
        List<Number> actionIDs;
        List<RslEntity> triggers = new LinkedList<>();
        List<RslEntity> actions = new LinkedList<>();
        Set<RslLink> links;

        String modelName = "eSPACE";
        String className = "StructuralLink";

        try {
            dcompID = extractEntityID(req, "id");
            dcomp = getEntityByID(dcompID);
            triggerIDs = extractNumberListParameter(req, "triggers");
            actionIDs = extractNumberListParameter(req, "actions");

            //get all trigger entities
            for(Number triggerID : triggerIDs){
                RslEntity entity;
                entity = getEntityByID(triggerID.longValue());
                triggers.add(entity);
            }

            //get all action entities
            for(Number actionID : actionIDs){
                RslEntity entity;
                entity = getEntityByID(actionID.longValue());
                actions.add(entity);
                System.out.println("in action for loop " + actionIDs);
            }

            Class sl = Class.forName("rsl.models.espace.links.StructuralLink");
            links = dcomp.getOutgoingLinksOfType(sl);
            if(links.isEmpty()) {                //dcomp has no existing triggers and actions
                //create structural link going from the dcomp to the trigger(s) and action(s)
                RslLink link = EntityFactory.createLink(modelName, className);

                if (link != null && !triggerIDs.isEmpty() && !actionIDs.isEmpty()) {
                    link.addSource(dcomp);
                    if (triggers.size() > 1) {             //if more than one trigger we need to create a structural link that will be the first target of the struct of the dcomp
                        RslLink tlink = EntityFactory.createLink(modelName, className);
                        for (RslEntity trigger : triggers) {     //check if trigger not null?
                            tlink.addTarget(trigger);
                        }
                        tlink.save();
                        link.addTarget(tlink);
                    } else if (triggers.size() == 1) {
                        link.addTarget(triggers.get(0));
                    }

                    if (actions.size() > 1) {             //if more than one action we need to create a structural link that will be the second target of the struct of the dcomp
                        RslLink alink = EntityFactory.createLink(modelName, className);
                        for (RslEntity action : actions) {     //check if action not null?
                            alink.addTarget(action);
                        }
                        alink.save();
                        link.addTarget(alink);
                    } else if (actions.size() == 1) {
                        link.addTarget(actions.get(0));
                    }
                    link.save();
                    String msg = String.format("Created link from '%s' to its trigger(s) and action(s).", dcompID);
                    return new ServerResponse(200, msg, link);
                } else {
                    String msg = String.format("Could not create a link between '%s' and its trigger(s) and action(s).", dcompID);
                    return new ServerResponse(500, msg, null);
                }
            } else {
                System.out.println("in else branch, so dcomps has already t&a");
                if(links.size() > 1){
                    String msg = String.format("Dcomp with id '%s' has too many structural links.", dcompID);
                    return new ServerResponse(500, msg, null);
                } else {    //Dcomp should have only one structural link with 2 targets
                    List<RslLink> StructLinksList = new ArrayList(links);
                    RslLink slink = StructLinksList.get(0);
                    //remove previous targets, i.e. the previous triggers and actions
                    Set<RslEntity> prevTargets = slink.getTargets();
                    slink.removeTargets(new ArrayList(prevTargets));
                    if(triggers.size() > 1) {
                        RslLink tlink = EntityFactory.createLink(modelName, className);
                        for(RslEntity trigger : triggers) {
                            tlink.addTarget(trigger);
                        }
                        tlink.save();
                        slink.addTarget(tlink);
                    } else if(triggers.size() == 1){
                        slink.addTarget(triggers.get(0));
                    }

                    if(actions.size() > 1) {
                        RslLink alink = EntityFactory.createLink(modelName, className);
                        for(RslEntity action : actions) {
                            alink.addTarget(action);
                        }
                        alink.save();
                        slink.addTarget(alink);
                    } else if(actions.size() == 1){
                        slink.addTarget(actions.get(0));
                    }
                    slink.save();
                    String msg = String.format("Updated link from '%s' to its trigger(s) and action(s).", dcompID);
                    return new ServerResponse(200, msg, slink);
                }
            }

        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String msg = String.format("Created link from dcomp to its trigger(s) and action(s).");
        return new ServerResponse(200, msg, null);
    }


    /**
     * methods for DUI distribution, first one should rather be called add FUI to subscribers...
     * @param req
     * @return
     */
    private ServerResponse getDUIEvents(ClientRequest req) {
        long fuiID;
        RslEntity fui;

        //todo: implement better class
        //check which type of event and for which device, then return subscriptionId
        try {
            fuiID = extractEntityID(req, "fui");
            fui = getEntityByID(fuiID);
            subscribers.add(fui);
            String msg = String.format("Added subscriber '%s'.", fuiID);
            return new ServerResponse(200, msg, fui);
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
    }

    private ServerResponse getDUISubscribers(ClientRequest req){
        //todo: implement better class
        //check which devices are subscribed and give list of their names to client
        String methodNameGetDevice = "getDevice";
        ArrayList<String> datas = new ArrayList<>();
        try {
            for(RslEntity fui : subscribers){
                //get APP
                Set<RslLink> structlinks = fui.getIncomingLinks();
                List<RslLink> StructLinksList = new ArrayList(structlinks);
                Set<RslEntity> Apps = StructLinksList.get(0).getSources(); //we assume there is only one struct link going from an app to this fui
                List<RslEntity> appsList = new ArrayList(Apps);
                RslEntity app = appsList.get(0); //we assume there is only one app
                //get device
                Long id = fui.invokeMethod(methodNameGetDevice);
                RslEntity device = getEntityByID(id);
                String deviceName = device.getName();
                String data = "{'appID': " + app.getID() + ", 'appName': " + app.getName() + ", 'deviceID': " + device.getID() + ", 'deviceName': " + deviceName + "}";
                datas.add(data);
            }
        } catch (RequestProcessingException e) {
            e.printStackTrace();
            return new ServerResponse(500, "Error", datas);
        }
        return new ServerResponse(200, "Retrieved subscribers", datas);
    }

    private ServerResponse sendDUI(ClientRequest req) {
        long UIeID;
        RslEntity UIe;

        //todo: check implementation of this class
        //check which type of event and for which device, then return subscriptionId
        try {
            UIeID = extractEntityID(req, "UIe");
            UIe = getEntityByID(UIeID);
            String deviceName = extractEntityName(req, "device");
            String msg = String.format("Message for '%s'.", deviceName);
            return new ServerResponse(200, msg, UIe);
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
    }



    private ServerResponse runScript(ClientRequest req) {

        String script;
        Process process = null;
        try {
            script = extractEntityName(req, "script");
            System.out.println("not yet processing..." + script);
            System.out.println(System.getProperty("user.dir"));
            //process = Runtime.getRuntime().exec(new String[]{"C:\Users\Audrey\Desktop\rsl-master\build\resources\"+ script});
            //String command = "cmd.exe /c cd C:\\Audrey\\Desktop\\rsl-master\\build\\resources\\ && start python "+ script;
            //Process p = Runtime.getRuntime().exec(command);

            ProcessBuilder pb = new ProcessBuilder("python", script);
            pb.directory(new File("C:\\Python37\\Scripts"));
            process = pb.start();

            //System.out.println("processing..." +command);
        } catch (Exception e) {
            System.out.println("Exception Raised" + e.toString());
            return new ServerResponse(404, e.getMessage(), null);
        }

        return new ServerResponse(200, "done", "line");
    }

    private ServerResponse unsubscribe(ClientRequest req) {
        long fuiID;

        //todo: implement better class
        //check which type of event and for which device, then return subscriptionId
        try {
            fuiID = extractEntityID(req, "fui");
            for (int i = 0; i < subscribers.size(); i++){
                RslEntity fui = subscribers.get(i);
                if(fuiID == fui.getID()){
                    subscribers.remove(i);
                    String msg = String.format("Deleted subscriber '%s'.", fuiID);
                    return new ServerResponse(200, msg, fui);
                }
            }
            String msg = String.format("Subscriber '%s' not found.", fuiID);
            return new ServerResponse(404, msg, null);
        } catch (RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
    }

    /**
     * Expects the id of the app that needs to be shared and the id of the user it should be shared with
     * Note that it does not check if app already shared with user ^^"
     * @param req
     * @return
     */
    private ServerResponse shareApp(ClientRequest req) {

        long appID;
        long userID;
        RslEntity app;
        RslUser user;
        try {
            appID = extractEntityID(req, "app");
            app = getEntityByID(appID);
            userID = extractEntityID(req, "user");
            user = getUserByID(userID);
            user.addAccessibleEntity(app);
            Set<RslLink> links = app.getOutgoingLinks();
            for(RslLink link : links){
                Set<RslEntity> targets = link.getTargets();
                for(RslEntity fui : targets){
                    user.addAccessibleEntity(fui);
                    Set<RslLink> fuiLinks = fui.getOutgoingLinks();
                    for(RslLink linkToDcomp : fuiLinks){
                        Set<RslEntity> dcomps = linkToDcomp.getTargets();
                        for(RslEntity dcomp : dcomps){
                            user.addAccessibleEntity(dcomp);
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Exception Raised" + e.toString());
            return new ServerResponse(404, e.getMessage(), null);
        }

        return new ServerResponse(200, "done", "shared!");
    }





}
