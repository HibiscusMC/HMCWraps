package de.skyslycer.hmcwraps.preview;

public interface IPreview {

    /**
     * Start the preview.
     */
    void preview();

    /**
     * Cancel the preview.
     * @param open If the inventory should open again
     */
    void cancel(boolean open);

}
