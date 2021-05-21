package rsl.compiler;

public class CompiledModel {

    private MemoryJavaClassFile[] classes;
    private ModelDescriptor modelDescriptor;

    public CompiledModel(MemoryJavaClassFile[] classes, ModelDescriptor modelDescriptor){
        this.classes = classes;
        this.modelDescriptor = modelDescriptor;
    }

    public MemoryJavaClassFile[] getClasses()
    {
        return classes;
    }

    public ModelDescriptor getModelDescriptor() {
        return modelDescriptor;
    }
}
