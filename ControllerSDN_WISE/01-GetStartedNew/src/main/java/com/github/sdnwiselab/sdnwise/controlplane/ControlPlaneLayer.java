/*
 * Copyright (C) 2016 Seby
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.controlplane;

import com.github.sdnwiselab.sdnwise.adapter.Adapter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Models a layer of the Control Plane. Each layer has a lower and upper adapter
 * and a scanner to intercept commands coming from the standard input
 *
 * @author Sebastiano Milardo
 */
public abstract class ControlPlaneLayer implements Observer, Runnable {

    /**
     * Charset in use.
     */
    protected static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    /**
     * Manages the status of the layer.
     */
    private boolean isStopped;

    /**
     * Identify the layer. This string is reported in each log message.
     */
    private final String layerShortName;

    /**
     * Adapters.
     */
    private final List<Adapter> lower, upper;
    /**
     * Scanner. Reads incoming commands.
     */
    private final Scanner scanner;

    /**
     * Creates a ControlPlane layer give a name and two adapters.
     *
     * @param name the name of the layer
     * @param low lower adapter
     * @param up upper adapter
     */
    public ControlPlaneLayer(final String name,
            final List<Adapter> low,
            final List<Adapter> up) {
        layerShortName = name;
        lower = low;
        upper = up;
        scanner = new Scanner(System.in, "UTF-8");
    }

    /**
     * Gets the layer short name.
     *
     * @return layer short name as a String
     */
    public final String getLayerShortName() {
        return layerShortName;
    }

    /**
     * Gets the lower adapter.
     *
     * @return the lower adapter
     */
    public final List<Adapter> getLower() {
        return lower;
    }

    /**
     * Gets the upper adapter.
     *
     * @return the upper adapter
     */
    public final List<Adapter> getUpper() {
        return upper;
    }

    /**
     * Starts the layer thread and checks if both adapters have been opened
     * correctly. This method is listening for incoming closing messages from
     * the standard input.
     */
    @Override
    public final void run() {
        int started = 0;
        int total = 0;

        if (lower != null) {
            total += lower.size();
            for (Adapter adapter : lower) {
                if (setupAdapter(adapter)) {
                    started++;
                }
            }
        }

        if (upper != null) {
            total += upper.size();
            for (Adapter adapter : upper) {
                if (setupAdapter(adapter)) {
                    started++;
                }
            }
        }

        if (started == total) {
            setupLayer();
            while (!isStopped) {
                if (scanner.nextLine().equals("q")) {
                    isStopped = true;
                }
            }
            lower.stream().forEach((adapter) -> {
                closeAdapter(adapter);
            });
            upper.stream().forEach((adapter) -> {
                closeAdapter(adapter);
            });
        }
    }

    /**
     * Closes the adapter.
     *
     * @param a the adapter to close
     */
    private void closeAdapter(final Adapter a) {
        if (a != null) {
            a.close();
        }
    }

    /**
     * Opens the adapter and add this object as observer.
     *
     * @param a the adapter to open
     * @return true if opened correctly, false otherwise
     */
    private boolean setupAdapter(final Adapter a) {
        if (a.open()) {
            a.addObserver(this);
            return true;
        }
        return false;
    }

    /**
     * Logs messages depending on the verbosity level.
     *
     * @param level a standard logging level
     * @param msg the string message to be logged
     */
    protected final void log(final Level level, final String msg) {
        Logger.getLogger(layerShortName).log(level, msg);
    }

    /**
     * Setup of the ControlPlane layer.
     */
    protected abstract void setupLayer();
}
