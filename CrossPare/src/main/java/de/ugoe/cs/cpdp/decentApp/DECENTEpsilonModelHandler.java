// Copyright 2015 Georg-August-Universität Göttingen, Germany
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package de.ugoe.cs.cpdp.decentApp;

import java.io.File;
import java.util.HashMap;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.Model;

import de.ugoe.cs.cpdp.decentApp.models.arffx.ARFFxPackage;
import de.ugoe.cs.cpdp.decentApp.models.decent.DECENTPackage;

/**
 * Class for handling decent and arffx model files.
 * 
 * @author Philip Makedonski, Fabian Trautsch
 * 
 */

public class DECENTEpsilonModelHandler {
    private HashMap<String, Object> metaModelCache = new HashMap<>();
    private boolean useDECENTBinary = false;
    private boolean useARFFxBinary = false;

    /**
     * path for DECENT model meta data
     */
    public static String metaPath = "./decent/models/";

    /**
     * Returns the decent model as IModel instance
     * 
     * @param decentModelLocation
     *            location of the decent model file
     * @param read
     *            indicates if the model should be read from
     * @param write
     *            indicates if data should be written in the model
     * @return EmFModel (IModel) instance from the decent model, which was loaded
     * @throws Exception quick and dirty exception forwarding
     */
    public IModel getDECENTModel(String decentModelLocation, boolean read, boolean write)
        throws Exception
    {

        EmfModel model;

        if (isUseDECENTBinary()) {
            unregisterMetaModels("");
            if (!read) {
                new File(decentModelLocation).delete();
                new File(decentModelLocation + "bin").delete();
            }
            DECENTResourceTool tool = new DECENTResourceTool();
            if (new File(decentModelLocation).exists() &&
                !new File(decentModelLocation + "bin").exists())
            {
                Resource resource =
                    tool.loadResourceFromXMI(decentModelLocation, "decent", DECENTPackage.eINSTANCE);
                ResourceTool.storeBinaryResourceContents(resource.getContents(), decentModelLocation +
                    "bin", "decentbin");
            }

            Resource resourceBin =
                ResourceTool.loadResourceFromBinary(decentModelLocation + "bin", "decentbin",
                                            DECENTPackage.eINSTANCE);
            // alternative pattern
            // model = createInMemoryEmfModel("DECENT", resourceLocation,
            // "../DECENT.Meta/model/DECENTv3.ecore", read, write, resourceBin,
            // DECENTPackage.eINSTANCE);
            // restoreMetaModels();

            // NOTE: Adding the package is essential as otherwise epsilon breaks
            model = new InMemoryEmfModel("DECENT", resourceBin, DECENTPackage.eINSTANCE);
            model.setStoredOnDisposal(write);
            model.setReadOnLoad(read);
            model.setCachingEnabled(true);
            restoreMetaModels();
        }
        else {
            model =
                createEmfModel("DECENT", decentModelLocation, metaPath + "DECENTv3.ecore", read,
                               write);
        }

        return model;
    }

    /**
     * Converts the decent model to a binary form
     * 
     * @param location
     *            of the decent model file
     */
    public void convertDECENTModelToBinary(String location) {
        unregisterMetaModels("");
        DECENTResourceTool tool = new DECENTResourceTool();
        Resource resource =
            tool.loadResourceFromXMI(location + "/model.decent", "decent", DECENTPackage.eINSTANCE);
        ResourceTool.storeBinaryResourceContents(resource.getContents(),
                                         location + "/model.decent" + "bin", "decentbin");
        restoreMetaModels();
    }

    /**
     * Converts the decent model to a xmi form
     * 
     * @param location
     *            of the decent model file
     */

    public void convertDECENTModelToXMI(String location) {
        unregisterMetaModels("");
        DECENTResourceTool tool = new DECENTResourceTool();
        Resource resource =
            ResourceTool.loadResourceFromBinary(location + "/model.decentbin", "decentbin",
                                        DECENTPackage.eINSTANCE);
        restoreMetaModels();
        tool.storeResourceContents(resource.getContents(), location + "/model.decent", "decent");
    }

    /**
     * Returns the arffx model as IModel instance
     * 
     * @param arffxModelLocation
     *            location of the arffx model file
     * @param read
     *            indicates if the model should be read from
     * @param write
     *            indicates if data should be written in the model
     * @return EmFModel (IModel) instance from the arffx model, which was loaded
     * @throws Exception quick and dirty exception forwarding
     */

    public IModel getARFFxModel(String arffxModelLocation, boolean read, boolean write)
        throws Exception
    {

        EmfModel model;

        if (isUseARFFxBinary()) {
            unregisterMetaModels("");
            if (!read) {
                new File(arffxModelLocation).delete();
                new File(arffxModelLocation + "bin").delete();
            }
            ARFFxResourceTool tool = new ARFFxResourceTool();
            if (new File(arffxModelLocation).exists() &&
                !new File(arffxModelLocation + "bin").exists())
            {
                Resource resource =
                    tool.loadResourceFromXMI(arffxModelLocation, "arffx", ARFFxPackage.eINSTANCE);
                ResourceTool.storeBinaryResourceContents(resource.getContents(),
                                                 arffxModelLocation + "bin", "arffxbin");
            }

            Resource resourceBin =
                ResourceTool.loadResourceFromBinary(arffxModelLocation + "bin", "arffxbin",
                                            ARFFxPackage.eINSTANCE);
            // alternative pattern
            // model = createInMemoryEmfModel("DECENT", resourceLocation,
            // "../DECENT.Meta/model/DECENTv3.ecore", read, write, resourceBin,
            // DECENTPackage.eINSTANCE);
            // restoreMetaModels();

            // NOTE: Adding the package is essential as otherwise epsilon breaks
            model = new InMemoryEmfModel("ARFFx", resourceBin, ARFFxPackage.eINSTANCE);
            // model.getModelImpl().getURI().toFileString()
            model.setStoredOnDisposal(write);
            model.setReadOnLoad(read);
            model.setCachingEnabled(true);
            restoreMetaModels();
        }
        else {
            model =
                createEmfModel("ARFFx", arffxModelLocation, metaPath + "ARFFx.ecore", read, write);
        }

        return model;
    }

    /**
     * Converts an arffx model to a binary version
     * 
     * @param location
     *            of the arffx model
     */
    public void convertARFFxModelToBinary(String location) {
        unregisterMetaModels("");
        ARFFxResourceTool tool = new ARFFxResourceTool();
        Resource resource =
            tool.loadResourceFromXMI(location + "/model.arffx", "arffx", ARFFxPackage.eINSTANCE);
        ResourceTool.storeBinaryResourceContents(resource.getContents(), location + "/model.arffx" + "bin",
                                         "arffxbin");
        restoreMetaModels();
    }

    /**
     * Converts an arffx model to xmi
     * 
     * @param location
     *            of the arffx model
     */

    public void convertARFFxModelToXMI(String location) {
        unregisterMetaModels("");
        ARFFxResourceTool tool = new ARFFxResourceTool();
        Resource resource =
            ResourceTool.loadResourceFromBinary(location + "/model.arffxbin", "arffxbin",
                                        DECENTPackage.eINSTANCE);
        restoreMetaModels();
        tool.storeResourceContents(resource.getContents(), location + "/model.arffx", "arffx");
    }

    /**
     * Returns the log model as IModel instance
     * 
     * @param logModelLocation
     *            location of the log model file
     * @param read
     *            indicates if the model should be read on load
     * @param write
     *            indicates if data should be written in the model
     * @return EmFModel (IModel) instance from the log model, which was loaded
     * @throws Exception quick and dirty exception forwarding
     */

    public static IModel getLOGModel(String logModelLocation, boolean read, boolean write)
        throws Exception
    {
        boolean readOnLoad = read;
        if (!new File(logModelLocation).exists()) {
            readOnLoad = false;
        }
        IModel model = createEmfModel("LOG", logModelLocation, metaPath + "LOG.ecore", readOnLoad, write);
        System.setProperty("epsilon.logFileAvailable", "true");
        return model;
    }

    /**
     * Creates an EMF Model
     * 
     * @param name
     *            of the emf model
     * @param model
     *            name of the model
     * @param metamodel
     *            name of the metamodel
     * @param readOnLoad
     *            indicates if the model should be read on load
     * @param storeOnDisposal
     *            indicates if the model should be stored on disposal
     * @return
     * @throws EolModelLoadingException
     * @throws URISyntaxException
     */

    @SuppressWarnings("deprecation")
    protected static EmfModel createEmfModel(String name,
                                      String model,
                                      String metamodel,
                                      boolean readOnLoad,
                                      boolean storeOnDisposal) throws EolModelLoadingException
    {
        EmfModel emfModel = new EmfModel();
        StringProperties properties = new StringProperties();
        properties.put(Model.PROPERTY_NAME, name);
        properties.put(Model.PROPERTY_ALIASES, name);
        properties.put(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI, "file:/" +
            new File(metamodel).getAbsolutePath());
        properties.put(EmfModel.PROPERTY_MODEL_URI, "file:/" + new File(model).getAbsolutePath());
        properties.put(EmfModel.PROPERTY_IS_METAMODEL_FILE_BASED, "true");
        properties.put(Model.PROPERTY_READONLOAD, readOnLoad + "");
        properties.put(CachedModel.PROPERTY_CACHED, "true");
        properties.put(Model.PROPERTY_STOREONDISPOSAL, storeOnDisposal + "");
        emfModel.load(properties, "");
        // System.out.println(emfModel.allContents());
        return emfModel;
    }

    /**
     * Restores the metamodels, so that they are registered in the EPackage registry
     */
    private void restoreMetaModels() {
        for (String key : this.metaModelCache.keySet()) {
            EPackage.Registry.INSTANCE.put(key, this.metaModelCache.get(key));
        }
    }

    /**
     * Unregister the metamodels from the EPackage registry
     * 
     * @param filter
     *            for filtering out certain instances
     */
    private void unregisterMetaModels(String filter) {
        for (String key : EPackage.Registry.INSTANCE.keySet()) {
            if (key.contains(filter)) {
                this.metaModelCache.put(key, EPackage.Registry.INSTANCE.get(key));
            }
        }
        for (String key : this.metaModelCache.keySet()) {
            EPackage.Registry.INSTANCE.remove(key);
        }
    }

    /**
     * Returns true if decent binary model is used
     * 
     * @return true if binary
     */

    public boolean isUseDECENTBinary() {
        return this.useDECENTBinary;
    }

    /**
     * Sets the boolean which indicates, if the decent binary model is used
     * 
     * @param useDECENTBinary if true, binary format is used
     */
    public void setUseDECENTBinary(@SuppressWarnings("hiding") boolean useDECENTBinary) {
        this.useDECENTBinary = useDECENTBinary;
    }

    /**
     * Returns true if arffx binary model is used
     * 
     * @return true if ARFFx
     */
    public boolean isUseARFFxBinary() {
        return this.useARFFxBinary;
    }

    /**
     * Sets the boolean which indicates, if the arffx binary model is used
     * 
     * @param useARFFxBinary if true, binary format is used
     */

    public void setUseARFFxBinary(@SuppressWarnings("hiding") boolean useARFFxBinary) {
        this.useARFFxBinary = useARFFxBinary;
    }

}
