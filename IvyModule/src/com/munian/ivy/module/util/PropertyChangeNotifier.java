package com.munian.ivy.module.util;

import java.beans.PropertyChangeListener;

/**
 *
 * @author raymond
 */
public interface PropertyChangeNotifier {
    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);
    
}
