package net.duohuo.dhroid.view.PhotoPop;

import java.util.Observable;

/**
 * A ZoomState holds zoom and pan values and allows the user1 to read and listen
 * to changes. Clients that modify ZoomState should call notifyObservers()
 */
public class ZoomState extends Observable {
    /**
     * Zoom level A value of 1.0 means the content fits the view.
     */
    private float mZoom =1f;

    /**
     * Pan position x-coordinate X-coordinate of zoom window center position,
     * relative to the width of the content.
     */
    private float mPanX = 0.5f;

    /**
     * Pan position y-coordinate Y-coordinate of zoom window center position,
     * relative to the height of the content.
     */
    private float mPanY = 0.5f;

    // Public methods

    /**
     * Get current x-pan
     * 
     * @return current x-pan
     */
    public float getPanX() {
        return mPanX;
    }

    /**
     * Get current y-pan
     * 
     * @return Current y-pan
     */
    public float getPanY() {
        return mPanY;
    }

    /**
     * Get current zoom value
     * 
     * @return Current zoom value
     */
    public float getZoom() {
        return mZoom;
    }

    /**
     * Help function for calculating current zoom value in x-dimension
     * 
     * @param aspectQuotient (Aspect ratio content) / (Aspect ratio view)
     * @return Current zoom value in x-dimension
     */
    public float getZoomX(float aspectQuotient) {
        return Math.min(mZoom, mZoom * aspectQuotient);
    }

    /**
     * Help function for calculating current zoom value in y-dimension
     * 
     * @param aspectQuotient (Aspect ratio content) / (Aspect ratio view)
     * @return Current zoom value in y-dimension
     */
    public float getZoomY(float aspectQuotient) {
        return Math.min(mZoom, mZoom / aspectQuotient);
    }

    /**
     * Set pan-x
     * 
     * @param panX Pan-x value to set
     */
    public void setPanX(float panX) {
    	if(mZoom ==1.0f){
    		return ; 
    	}
        if (panX != mPanX) {
            mPanX = panX;
            setChanged();
        }
    }

    /**
     * Set pan-y
     * 
     * @param panY Pan-y value to set
     */
    public void setPanY(float panY) {
    	if(mZoom == 1.0f){
    		return ;
    	}
        if (panY != mPanY) {
            mPanY = panY;
            setChanged();
        }
    }

    /**
     * Set zoom
     * 
     * @param zoom Zoom value to set
     */
    public void setZoom(float zoom) {
        if (zoom != mZoom) {
        	mZoom = mZoom<0.5f?0.5f:zoom;
        	if(mZoom==0.5f){
        		this.mPanX = 0.5f;
        		this.mPanY = 0.5f;
        	}
            setChanged();
        }
    }

}