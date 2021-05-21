package rsl.core;

public class ClassGenerationResult {

    private int modelCount = 0;
    private int classCount = 0;

    public ClassGenerationResult(int modelCount, int classCount)
    {
        this.modelCount = modelCount;
        this.classCount = classCount;
    }

    public int getModelCount() {
        return modelCount;
    }

    public void setModelCount(int modelCount) {
        this.modelCount = modelCount;
    }

    public int getClassCount() {
        return classCount;
    }

    public void setClassCount(int classCount) {
        this.classCount = classCount;
    }
}
